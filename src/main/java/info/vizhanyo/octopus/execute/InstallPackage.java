package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public class InstallPackage implements Executor {

	@Override
	public boolean apply(Connector connector, Map<String, String> args) throws Exception{
        return connector.executeCommand("apt-get install -y " + args.get("pkg"));
	}

}