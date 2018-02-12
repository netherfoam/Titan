package org.maxgamer.rs.structure.configs;

import java.util.*;

/**
 * TODO: Document this
 */
public class SystemConfigSection extends ConfigSection {
    private String prefix;

    public SystemConfigSection() {
        this("");
    }

    public SystemConfigSection(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isEmpty() {
        for (Object key : System.getProperties().keySet()) {
            if (!(key instanceof String)) {
                // Not a String key: we can't decode that
                continue;
            }

            if (((String) key).startsWith(prefix)) return false;
        }

        return true;
    }

    @Override
    public Object getObject(String key) {
        return System.getProperty(transformKey(key));
    }

    @Override
    public ConfigSection getSection(String key, ConfigSection fallback) {
        return new SystemConfigSection(transformKey(key) + ".");
    }

    @Override
    public Collection<String> keys() {
        Set<String> keys = new HashSet<>();

        for (Object key : System.getProperties().keySet()) {
            if (!(key instanceof String)) {
                // Not a String key: we can't decode that
                continue;
            }

            String s = (String) key;
            if (s.startsWith(prefix)) {
                s = s.substring(prefix.length(), s.length());

                int separator = s.indexOf(".");
                if (separator >= 0) {
                    s = s.substring(0, separator);
                }

                keys.add(s);
            }
        }

        return keys;
    }

    private String transformKey(String key) {
        return prefix + key;
    }
}
