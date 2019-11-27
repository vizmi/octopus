package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public interface Executor {
    public boolean apply(Connector connector, Map<String, String> args) throws Exception;
}
