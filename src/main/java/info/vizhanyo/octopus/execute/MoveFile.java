package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public class MoveFile implements Executor {

	@Override
	public boolean apply(Connector connector, Map<String, String> args) throws Exception{
        return connector.executeCommand("mv " + args.get("source") + " " + args.get("destination"));
	}
}
