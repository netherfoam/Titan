package org.maxgamer.rs.structure;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Document this
 */
public class MutableConfigTest {
    private MutableConfig config;

    @Before
    public void setup() {
        config = new MutableConfig();
    }

    @Test
    public void testString() {
        config.set("some_key", "some_value");

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

        config.set("some_key", "some_value");

        Assert.assertFalse("Expect not empty keys", config.keys().isEmpty());
        Assert.assertFalse("Expect not empty", config.isEmpty());

        Assert.assertTrue("Expect keys to contain some_key", config.keys().contains("some_key"));
    }

    @Test
    public void testMap() {
        Map<String, String> value = Collections.singletonMap("k", "v");

        config.set("some_key", value);

        ConfigSection child = config.getSection("some_key");

        Assert.assertNotNull("Expect non-null value", child);
        Assert.assertEquals("Expect k -> v", "v", child.getString("k"));
    }
}
