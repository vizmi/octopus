package info.vizhanyo.octopus.execute;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import info.vizhanyo.octopus.connect.MockConnector;

public class SetPermissionsTest {
    private static final Logger logger = LogManager.getLogger(SetPermissionsTest.class);

    MockConnector connector = new MockConnector();
    Map<String, String> args = new HashMap<String, String>();
    
    @Test
    public void testSetPermission() {
		args.put("owner", "test");
		args.put("group", "test-group");
		args.put("mode", "664");
        args.put("file", "/test.txt");

        Executor e = new SetPermissions();
        try {
            e.apply(connector, args);
        } catch (Exception exc) {
            logger.error(exc.getMessage());
        } finally {
            assertEquals("Executing chown test /test.txt\n" +
                    "Executing chgrp test-group /test.txt\n" +
                    "Executing chmod 664 /test.txt\n",
                connector.getCommands());
        }
    }
}
