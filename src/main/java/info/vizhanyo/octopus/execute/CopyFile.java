package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public class CopyFile implements Executor {

	@Override
	public boolean apply(Connector connector, Map<String, String> args) throws Exception{
        return connector.sendFile(args.get("source"), args.get("destination"));
	}
}
