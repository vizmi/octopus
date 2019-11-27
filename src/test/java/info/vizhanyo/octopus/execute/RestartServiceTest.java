package info.vizhanyo.octopus.execute;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import info.vizhanyo.octopus.connect.MockConnector;

public class RestartServiceTest {
    private static final Logger logger = LogManager.getLogger(RestartServiceTest.class);

    MockConnector connector = new MockConnector();
    Map<String, String> args = new HashMap<String, String>();
    
    @Test
    public void testRestartService() {
        args.put("service", "apache2");
        Executor e = new RestartService();
        try {
            e.apply(connector, args);
        } catch (Exception exc) {
            logger.error(exc.getMessage());
        } finally {
            assertEquals("Executing service apache2 restart\n", connector.getCommands());
        }
    }
}
