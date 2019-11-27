package info.vizhanyo.octopus;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testGetArg() {
        String[] args = { "-y", "yes", "--no", "no", "--none" };
        assertEquals("yes", App.getArg(args, "-y", "--yes", "default"));
        assertEquals("no", App.getArg(args, "-n", "--no", "default"));
        assertEquals("default", App.getArg(args, "-n", "--none", "default"));
        assertEquals("default", App.getArg(args, "-x", "--notExists", "default"));
    }

    @Test
    public void testGetExecutor() {
        assertEquals("info.vizhanyo.octopus.execute.CopyFile", App.getExecutor("copyFile").getClass().getName());
        assertEquals("info.vizhanyo.octopus.execute.InstallPackage", App.getExecutor("InstallPackage").getClass().getName());
        try {
            App.getExecutor("somecrazyclass");
        } catch (IllegalArgumentException ex) {
            assertEquals("class cannot be instantiated info.vizhanyo.octopus.execute.Somecrazyclass", ex.getMessage());
        }
    }
}
