package info.vizhanyo.octopus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import info.vizhanyo.octopus.execute.Executor;
import info.vizhanyo.octopus.connect.Connector;
import info.vizhanyo.octopus.connect.SSHConnector;

public final class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        // cmd line processing
        logger.debug("parsing cmd line arguments: " + String.join(", ", args));
        String rackFileName = getArg(args, "-s", "--system", "system.json");
        logger.debug("rack file name: " + rackFileName);
        String runbookFileName = getArg(args, "-r", "--runbook", "runbook.json");
        logger.debug("runbook file name: " + runbookFileName);

        // reading config files
        Rack rack = null;
        Runbook runbook = null;
        try {
            rack = getRack(rackFileName);
            logger.debug("hosts file successfully parsed. Host count: " + rack.getHosts().size());

            runbook = getRunbook(runbookFileName);
            logger.debug("runbook file successfully parsed. Steps count: " + runbook.getActions().size());
        } catch (IOException ioe) {
            logger.fatal(ioe.getMessage());
            System.exit(1);
        }

        // cache of executors
        Map<String, Executor> execs = new HashMap<>(); 
        // execute runbook
        for (Host host : rack.getHosts()) {
            Connector connector = new SSHConnector(host);
            try {
                connector.connect();
                for (Action action : runbook.getActions()) {
                    String name = action.getName();
                    Executor exec = execs.get(name);
                    if (exec == null) {
                        exec = getExecutor(name);
                        execs.put(name, exec);
                    }
                    int retry = 2;
                    boolean succeed = false;
                    while (retry-- >= 0 && !succeed) {
                        succeed = exec.apply(connector, action.getArgs());
                    }
                    if (!succeed) {
                        logger.error(action.getName() + " failed on all successive attempts");
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            } finally {
                connector.disconnect();
            }
        }
    }

    static String getArg(String[] args, String shortOpt, String longOpt, String dft) {
        for (int i = 0; i < args.length; i++) {
            if ((args[i].equalsIgnoreCase(shortOpt) ||
                    args[i].equalsIgnoreCase(longOpt)) &&
                    args.length > (i + 1)) {
                return args[i + 1];
            }
        }
        return dft;
    }

    static Rack getRack(String fileName) throws IOException {
        String json = Files.readString(Paths.get(fileName));
        return new Rack(json);
    }

    static Runbook getRunbook(String fileName) throws IOException {
        String json = Files.readString(Paths.get(fileName));
        return new Runbook(json);
    }

    static Executor getExecutor(String name) {
        String className = name.trim();
        if (!className.contains(".")) {
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            className = "info.vizhanyo.octopus.execute." + className;
        }

        try {
            return (Executor) Class.forName(className).getConstructor().newInstance();
        } catch (ClassCastException | NoClassDefFoundError | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
                | ClassNotFoundException e) {
            logger.error("cannot instantiate class " + className + '\n' + e.getMessage());
            throw new IllegalArgumentException("class cannot be instantiated " + className);
        }
    }
}
