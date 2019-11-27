package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public class RemovePackage implements Executor {

	@Override
	public boolean apply(Connector connector, Map<String, String> args) throws Exception{
        return connector.executeCommand("apt-get remove -y " + args.get("pkg"));
	}

}