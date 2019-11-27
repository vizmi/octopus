package info.vizhanyo.octopus.execute;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import info.vizhanyo.octopus.connect.MockConnector;

public class InstallPackageTest {
    private static final Logger logger = LogManager.getLogger(InstallPackageTest.class);

    MockConnector connector = new MockConnector();
    Map<String, String> args = new HashMap<String, String>();
    
    @Test
    public void testInstallPkg() {
        args.put("pkg", "apache2");
        args.put("pkg", "apache2");
        Executor e = new InstallPackage();
        try {
            e.apply(connector, args);
        } catch (Exception exc) {
            logger.error(exc.getMessage());
        } finally {
            assertEquals("Executing apt-get install -y apache2\n", connector.getCommands());
        }
    }
}
