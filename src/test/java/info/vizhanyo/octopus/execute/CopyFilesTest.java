package info.vizhanyo.octopus.execute;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import info.vizhanyo.octopus.connect.MockConnector;

public class CopyFilesTest {
    private static final Logger logger = LogManager.getLogger(CopyFilesTest.class);

    MockConnector connector = new MockConnector();
    Map<String, String> args = new HashMap<String, String>();
    
    @Test
    public void testCopyFile() {
        args.put("source", "C:\\test.txt");
        args.put("destination", "/test.txt");
        Executor e = new CopyFile();
        try {
            e.apply(connector, args);
        } catch (Exception exc) {
            logger.error(exc.getMessage());
        } finally {
            assertEquals("Sending C:\\test.txt to /test.txt\n", connector.getCommands());
        }
    }
}
