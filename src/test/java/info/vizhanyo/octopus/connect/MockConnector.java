package info.vizhanyo.octopus.connect;

public class MockConnector implements Connector {

    private StringBuilder cmds = new StringBuilder();

    @Override
    public void connect() throws Exception {
        cmds.append("Connecting").append('\n');
    }

    @Override
    public boolean executeCommand(String command) throws Exception {
        cmds.append("Executing ").append(command).append('\n');
        return true;
    }

    @Override
    public boolean sendFile(String source, String destination) throws Exception {
        cmds.append("Sending ").append(source).append(" to ").append(destination).append('\n');
        return true;
    }

    @Override
    public void disconnect() {
        cmds.append("Disconnecting").append('\n');
    }

    public String getCommands() {
        return cmds.toString();
    }

    public void resetCommands() {
        cmds = new StringBuilder();
    }

}