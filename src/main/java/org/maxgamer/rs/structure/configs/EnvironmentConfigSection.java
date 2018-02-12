package org.maxgamer.rs.structure.configs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Document this
 */
public class EnvironmentConfigSection extends ConfigSection {
    private String prefix;

    public EnvironmentConfigSection() {
        this("");
    }

    public EnvironmentConfigSection(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isEmpty() {
        for(Map.Entry<String, String> entry : System.getenv().entrySet()) {
            if (entry.getKey().startsWith(prefix)) return false;
        }

        return true;
    }

    @Override
    public Object getObject(String key) {
        return System.getenv(transformKey(key));
    }

    @Override
    public ConfigSection getSection(String key, ConfigSection fallback) {
        return new EnvironmentConfigSection(transformKey(key) + "_");
    }

    @Override
    public Iterable<String> keys() {
        Set<String> keys = new HashSet<>();

        for (String key : System.getenv().keySet()) {
            if (key.startsWith(prefix)) {
                key = key.substring(0, prefix.length());

                int separator = key.indexOf("_");
                if (separator >= 0) {
                    key = key.substring(0, separator);
                }

                keys.add(key);
            }
        }

        return keys;
    }

    private String transformKey(String key) {
        return prefix + key.replaceAll("\\.", "_").toUpperCase();
    }
}
