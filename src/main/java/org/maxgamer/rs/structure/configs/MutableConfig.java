package org.maxgamer.rs.structure.configs;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * TODO: Document this
 */
public class MutableConfig extends ConfigSection {
    /**
     * The map of <String, Object> contained in this section.
     * This is the key-values of the map. Note that the key
     * cannot contain full stops ('.').
     */
    protected Map<String, Object> map;

    /**
     * Creates a new, blank config section
     */
    public MutableConfig() {
        this(new HashMap<String, Object>());
    }

    public MutableConfig(ConfigSection source) {
        for (String key : source.keys()) {
            set(key, source.getObject(key));
        }
    }

    @SuppressWarnings("unchecked")
    public MutableConfig(String string) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml parser = new Yaml(options);
        map = (Map<String, Object>) parser.load(string);

        if (map == null) {
            map = new HashMap<>();
        }
    }

    /**
     * Creates a new ConfigSection, using the given InputStream
     * as a source.
     *
     * @param in the input stream to read the config from.
     */
    @SuppressWarnings("unchecked")
    public MutableConfig(InputStream in) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml parser = new Yaml(options);
        map = (Map<String, Object>) parser.load(in);

        if (map == null) {
            map = new HashMap<>();
        }
    }

    /**
     * Creates a new ConfigSection from the given map of
     * values.
     *
     * @param values The values
     * @throws NullPointerException if the values map is null
     */
    public MutableConfig(Map<String, Object> values) {
        if (values == null) {
            throw new NullPointerException("Config section may not have a null map.");
        }

        this.map = values;
    }

    /**
     * Returns true if this config section contains no values.
     *
     * @return true if the map is empty.
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Removes the given key by placing null there
     * @param key the key
     */
    public void remove(String key) {
        // TODO: Use an actual delete instead here
        set(key, null);
    }

    @Override
    public MutableConfig getSection(String key) {
        ConfigSection c = getSection(key, null);
        if (c == null) c = new MutableConfig();

        return (MutableConfig) c;
    }

    /**
     * Sets the object at the given location to the given object.
     * If you attempt to do this with an object which is not serializable,
     * then chances are you'll end up with garbage data. Consider
     * writing a method to serialize the required object into a
     * ConfigSection if this is the case.  Numbers, Strings and
     * ConfigSections are stored properly and may be fetched later.
     *
     * @param s the key for the object (May include '.')
     * @param o the object to set.
     */
    @SuppressWarnings("unchecked")
    public void set(String s, Object o) {
        if (s == null) {
            throw new NullPointerException("Key may not be null.");
        }

        String[] parts = s.split("\\.");

        if (map == null) {
            map = new HashMap<>();
        }

        Map<String, Object> node = map;

        for (int i = 0; i < parts.length - 1; i++) {
            Object q = node.get(parts[i]);
            if (q == null) {
                q = new HashMap<String, Object>();
                node.put(parts[i], q);
                node = (HashMap<String, Object>) q;
            } else if (q instanceof Map) {
                node = (HashMap<String, Object>) q;
            } else {
                // Q is not a map
                // We must delete Q to store O.
                q = new HashMap<String, Object>();
            }
        }

        // Now node is the parent of where we wish to store O.
        if (o instanceof MutableConfig) {
            // ConfigSection is a nice way of saying map.
            o = ((MutableConfig) o).map;
        }
        if (o == null) {
            node.remove(parts[parts.length - 1]);
            if (node.isEmpty() && parts.length > 1) {
                StringBuilder sb = new StringBuilder(parts[0]);
                for (int i = 1; i < parts.length - 1; i++) {
                    sb.append(".").append(parts[i]);
                }
                set(sb.toString(), null);
            }
        } else {
            node.put(parts[parts.length - 1], o);
        }
    }

    /**
     * Fetches the section under the given key, or returns fallback if there
     * was an issue.  If the section contains the key, but it is not a Config
     * Section, then the fallback is returned.
     *
     * @param key      The key
     * @param fallback return value if the value was not specified or is not a ConfigSection
     * @return the section
     */
    @Override
    public MutableConfig getSection(String key, ConfigSection fallback) {
        Object o = getObject(key);
        try {
            if (o != null) {
                if (o instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) o;

                    return new MutableConfig(map);
                } else if (o instanceof ConfigSection) {
                    return (MutableConfig) o;
                }
            }
        } catch (Exception ignored) {
            // Something went wrong, we'll return the fallback
        }

        return (MutableConfig) fallback;
    }

    @Override
    public Collection<String> keys() {
        return map.keySet();
    }

    /**
     * Fetches the raw object at the given location.
     *
     * @param s the key to search for, may contain '.' for subsections.
     * @return the object
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getObject(String s) {
        if (s == null) {
            throw new NullPointerException("Key may not be null.");
        }

        String[] parts = s.split("\\.");

        Map<String, Object> last = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object q = last.get(parts[i]);
            if (q == null || !(q instanceof Map)) {
                return null;
            }
            last = (Map<String, Object>) q;
        }

        if (last == null) return null;

        Object o = last.get(parts[parts.length - 1]);

        if (o instanceof Map) {
            /*
             * If we're retrieving a map, then we should check if we can cast
             * it to a config section. This is typesafe in that it prevents us
             * from using a map that does not have keys as strings.
             */
            HashMap<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!(e.getKey() instanceof String)) return o; //Key is not String, we can't help.

                result.put((String) e.getKey(), e.getValue());
            }

            return new MutableConfig(result);
        }

        return o;
    }

    @Override
    public String toString() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml parser = new Yaml(options);
        return parser.dump(map);
    }
}
