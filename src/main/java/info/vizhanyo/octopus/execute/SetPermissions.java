package info.vizhanyo.octopus.execute;

import java.util.Map;

import info.vizhanyo.octopus.connect.Connector;

public class SetPermissions implements Executor {

	@Override
	public boolean apply(Connector connector, Map<String, String> args) throws Exception {
		boolean result = true;
		String file = " " + args.get("file");
		result = result && connector.executeCommand("chown " + args.get("owner") + file);
		result = result && connector.executeCommand("chgrp " + args.get("group") + file);
		result = result && connector.executeCommand("chmod " + args.get("mode") + file);
		return result;
	}

}