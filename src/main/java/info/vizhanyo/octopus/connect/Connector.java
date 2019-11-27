package info.vizhanyo.octopus.connect;

public interface Connector {
    public abstract void connect() throws Exception;

    public abstract boolean executeCommand(String command) throws Exception;

    public abstract boolean sendFile(String source, String destination) throws Exception;

    public abstract void disconnect();
    
}