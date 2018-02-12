package org.maxgamer.rs.structure;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.EnvironmentConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;
import org.maxgamer.rs.structure.configs.SystemConfigSection;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * TODO: Document this
 */
public class SystemConfigTest {
    private SystemConfigSection config;
    private Properties oldProperties;

    @Before
    public void setup() {
        // Back up our old system properties and hide them
        oldProperties = System.getProperties();
        System.setProperties(new Properties());

        config = new SystemConfigSection();
    }

    @After
    public void teardown() {
        // Restore our old system properties
        System.setProperties(oldProperties);
    }

    @Test
    public void testString() {
        System.setProperty("some_key", "some_value");

        Assert.assertEquals("Expect some_value", "some_value", config.getString("some_key"));
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue("Expect empty", config.isEmpty());
    }

    @Test
    public void testKeys() {
        Assert.assertTrue("Expect empty keys", config.keys().isEmpty());
        Assert.assertTrue("Expect empty", config.isEmpty());

        System.setProperty("some_key", "some_value");

        Assert.assertFalse("Expect not empty keys", config.keys().isEmpty());
        Assert.assertFalse("Expect not empty", config.isEmpty());

        Assert.assertTrue("Expect keys to contain some_key", config.keys().contains("some_key"));
    }

    @Test
    public void testMap() {
        System.setProperty("some_key.k", "v");

        ConfigSection child = config.getSection("some_key");

        Assert.assertNotNull("Expect non-null value", child);
        Assert.assertEquals("Expect k -> v", "v", child.getString("k"));
    }
}
