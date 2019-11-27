package info.vizhanyo.octopus.connect;

import info.vizhanyo.octopus.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnector implements Connector {

    private static final Logger logger = LogManager.getLogger(SSHConnector.class);

    private Host host;
    private int timeout;

    private Session session;

    private SSHConnector(Host host, int timeout) {
        logger.debug("SSHExecutor created with settings " + host.toString() + ", timeout=" + timeout);
        this.host = host;
        this.timeout = timeout;
    }

    public SSHConnector(Host host) {
        this(host, 10000);
    }

    @Override
    public void connect() throws JSchException {
        logger.debug("connecting to " + host.getName());
        JSch jsch = new JSch();
        session = jsch.getSession(host.getUser(), host.getName(), host.getPort());
        session.setPassword(host.getPassword());
        // find a better way to manage known_hosts
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(timeout);
        logger.debug("conneced to " + host.getName());
    }

    @Override
    public void disconnect() {
        if (session != null && session.isConnected()) {
            logger.debug("disconnecting from " + host.getName());
            session.disconnect();
        }
        logger.debug("disconneced from " + host.getName());
    }

    @Override
    public boolean executeCommand(String command) throws JSchException {
        boolean result = false;

        ChannelExec channel = null;
        try {
            if (session == null || !session.isConnected()) {
                connect();
            }

            logger.debug("opening channel to " + session.getHost());
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            InputStream out = channel.getInputStream();
            InputStream err = channel.getErrStream();
            logger.debug("executing " + command + " on " + session.getHost());
            channel.connect();

            StringBuilder sb = new StringBuilder();
            int byteRead = out.read();
            while (byteRead > -1) {
                sb.append((char) byteRead);
                byteRead = out.read();
            }
            if (sb.length() > 0) {
                logger.info("result for " + command + " on " + session.getHost() + " is " + sb.toString());
            }

            sb = new StringBuilder();
            byteRead = err.read();
            while (byteRead > -1) {
                sb.append((char) byteRead);
                byteRead = err.read();
            }
            if (sb.length() > 0) {
                logger.error("errors for " + command + " on " + session.getHost() + " is " + sb.toString());
            }

            logger.debug("return code for " + command + " on " + session.getHost() + " is " + channel.getExitStatus());
            result = (channel.getExitStatus() == 0);

        } catch (IOException ioe) {
            // never actually thrown in actual implementation
            assert (false);
        } finally {
            if (channel != null && channel.isConnected()) {
                logger.debug("closing channel to " + session.getHost());
                channel.disconnect();
            }
        }
        return result;
    }

    @Override
    public boolean sendFile(String source, String destination) throws JSchException {
        ChannelExec channel = null;
        
        File file = new File(source);
        if (!file.exists()) {
            throw new IllegalArgumentException("File " + source + " does not exists");
        }

        try {
            if (session == null || !session.isConnected()) {
                connect();
            }

            // send "scp -t" command
            destination = "'" + destination.replace("'", "'\"'\"'") + "'";
            String command = "scp -t " + destination;

            logger.debug("opening channel to " + session.getHost());
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = null;
            InputStream in = null;
            try {
                out = channel.getOutputStream();
                in = channel.getInputStream();
            } catch (IOException e) {
                assert (false);
            }

            logger.debug("executing " + command + " on " + session.getHost());
            channel.connect();
            checkResponse(in);

            // send "C0644 filesize filename", where filename should not include '/'
            command = "C0644 " + file.length() + " ";
            if (source.lastIndexOf('/') > 0) {
                command += source.substring(source.lastIndexOf('/') + 1);
            } else {
                command += source;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            logger.debug("executing " + command + " on " + session.getHost());
            checkResponse(in);

            logger.debug("sending file content to " + session.getHost());
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0)
                    break;
                out.write(buf, 0, len);
            }
            out.flush();
            fis.close();

            logger.debug("sending \\0 " + session.getHost());
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            out.close();
            checkResponse(in);

        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            return false;
        } finally {
            if (channel != null && channel.isConnected()) {
                logger.debug("closing channel to " + session.getHost());
                channel.disconnect();
            }
        }
        return true;
    }

    private void checkResponse(InputStream in) throws JSchException {
        try {
            int b = in.read();
            logger.debug("response: " + b);
            if (b > 0) {
                StringBuffer sb = new StringBuffer();
                int c;
                do {
                    c = in.read();
                    sb.append((char) c);
                } while (c != '\n');
                throw new JSchException(sb.toString());
            }
        } catch (IOException e) {
            throw new JSchException(e.getMessage());
        }
    }
}
