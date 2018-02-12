package org.maxgamer.rs.structure.configs;

import org.maxgamer.rs.structure.Util;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Represents a configuration section in YML. Internally,
 * this class is simply a map of <String, Object> where the
 * objects are values or possibly, more ConfigSections.
 * <p>
 * This is an easy way of storing and loading data from
 * config files. It uses the SnakeYAML library, coupled
 * with this project.
 *
 * @author netherfoam
 */
public abstract class ConfigSection {
    public abstract boolean isEmpty();
    public abstract Object getObject(String key);
    public abstract ConfigSection getSection(String key, ConfigSection fallback);
    public abstract Iterable<String> keys();

    /**
     * Fetches the section under the given key, or returns a blank config section
     * if there was an issue. Note that this differs from other methods which return
     * a null fallback object. If you desire null on error, use getSection(key, null).
     *
     * @param key the key
     * @return the config section, never null.
     */
    public ConfigSection getSection(String key) {
        ConfigSection c = getSection(key, null);
        if (c == null) c = new MutableConfig();

        return c;
    }

    /**
     * Fetches the integer at the given key.
     *
     * @param k the key
     * @return the value or 0 if not found.
     */
    public int getInt(String k) {
        return getInt(k, 0);
    }

    /**
     * Fetches the integer at the given key, allowing a
     * fallback value to be specified.
     *
     * @param k        the key to search for
     * @param fallback the value to use if the key is not found.
     * @return the value, or fallback if not found.
     */
    public int getInt(String k, int fallback) {
        try {
            return ((Number) getObject(k)).intValue();
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Fetches the long at the given key.
     *
     * @param k the key
     * @return the value or 0 if not found.
     */
    public long getLong(String k) {
        return getLong(k, 0);
    }

    /**
     * Fetches the long at the given key, allowing a
     * fallback value to be specified.
     *
     * @param k        the key to search for
     * @param fallback the value to use if the key is not found.
     * @return the value, or fallback if not found.
     */
    public long getLong(String k, long fallback) {
        try {
            Number n = (Number) getObject(k);
            return n.longValue();
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Fetches the double at the given key.
     *
     * @param k the key
     * @return the value or 0 if not found.
     */
    public double getDouble(String k) {
        return getDouble(k, 0);
    }

    /**
     * Fetches the double at the given key, allowing a
     * fallback value to be specified.
     *
     * @param k        the key to search for
     * @param fallback the value to use if the key is not found.
     * @return the value, or fallback if not found.
     */
    public double getDouble(String k, double fallback) {
        try {
            Number n = (Number) getObject(k);
            return n.doubleValue();
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Fetches the String at the given key.
     *
     * @param k the key
     * @return the value or null if not found.
     */
    public String getString(String k) {
        return getString(k, null);
    }

    /**
     * Fetches the String at the given key, allowing a
     * fallback value to be specified.
     *
     * @param k        the key to search for
     * @param fallback the value to use if the key is not found.
     * @return the value, or fallback if not found.
     */
    public String getString(String k, String fallback) {
        try {
            return getObject(k).toString();
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Fetches the boolean at the given key.
     *
     * @param k the key
     * @return the value or false if not found.
     */
    public boolean getBoolean(String k) {
        return getBoolean(k, false);
    }

    /**
     * Fetches the boolean at the given key, allowing a
     * fallback value to be specified.
     *
     * @param k        the key to search for
     * @param fallback the value to use if the key is not found.
     * @return the value, or fallback if not found.
     */
    public boolean getBoolean(String k, boolean fallback) {
        try {
            Object o = getObject(k);
            if (o instanceof Boolean) {
                return (Boolean) o;
            } else if (o instanceof Number) {
                return ((Number) o).doubleValue() != 0; // Nonzero values are true.
            } else if (o instanceof String) {
                String s = (String) o;
                try {
                    return Util.parseBoolean(s);
                } catch (ParseException ignored) {
                }
            }
            return (Boolean) o; // This will probably fail.
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Fetches the map at the specified key, or if it is null or not
     * a map, returns the given fallback.
     *
     * @param key      the key to search for
     * @param k        the maps key class, eg. String.class (For a Map<String, ?>)
     * @param v        the maps value class, eg. Integer.class (For a Map<?, Integer>)
     * @param fallback the return value if this method fails
     * @return the map, or the fallback on failure
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String key, Class<K> k, Class<V> v, Map<K, V> fallback) {
        Object o = getObject(key);
        if (o == null) return fallback;
        if (!(o instanceof Map)) return fallback;

        Map<?, ?> map = (Map<?, ?>) o;
        Map<K, V> results = new HashMap<>();
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (k.isInstance(e.getKey()) && v.isInstance(e.getValue())) {
                results.put((K) e.getKey(), (V) e.getValue());
            }
        }
        return results;
    }

    /**
     * Fetches the map at the specified key, or if it is null or not
     * a map, returns null
     *
     * @param key the key to search for
     * @param k   the maps key class, eg. String.class (For a Map<String, ?>)
     * @param v   the maps value class, eg. Integer.class (For a Map<?, Integer>)
     * @return the map, or the null on failure.
     */
    public <K, V> Map<K, V> getMap(String key, Class<K> k, Class<V> v) {
        return getMap(key, k, v, null);
    }

    /**
     * Fetches the byte array at the specified key, or returns the
     * given fallback if it was not found or invalid.
     *
     * @param key      The key to retrieve
     * @param fallback the return value if the value was not found or invalid
     * @return the byte array, or the fallback if unsuccessful.
     */
    public byte[] getByteArray(String key, byte[] fallback) {
        Object o = getObject(key);
        if (o == null) return fallback;

        try {
            return (byte[]) o;
        } catch (ClassCastException e) {
            return fallback;
        }
    }

    /**
     * Fetches the byte array at the specified key, or returns
     * null if it was not found or invalid.
     *
     * @param key The key to retrieve
     * @return the byte array, or the null if unsuccessful.
     */
    public byte[] getByteArray(String key) {
        return getByteArray(key, null);
    }

    /**
     * Fetches the list at the requested position in this section. This works
     * by calling getObject(key). If the object is a collection, its elements
     * are added to a new list. If the object is a map, its keys are added to
     * a new list. This new list is returned. If it's neither a set nor map,
     * then the fallback list is returned instead.
     *
     * @param key      the location of the list to fetch
     * @param clazz    the type of list you want. Eg, String.class or Integer.class
     * @param fallback the list to fall back on if key wasn't found or wasn't a list.
     * @return the list if successful, or the fallback if unsuccessful.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz, List<T> fallback) {
        Object o = getObject(key);

        if (o == null) return fallback;

        if (o instanceof Collection) {
            Collection<?> set = (Collection<?>) o;
            List<T> results = new ArrayList<>(set.size());

            for (Object p : set) {
                if (clazz.isInstance(p)) {
                    results.add((T) p);
                }
            }

            return results;
        }

        if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            List<T> results = new ArrayList<>(map.size());

            for (Object p : map.keySet()) {
                if (clazz.isInstance(p)) {
                    results.add((T) p);
                }
            }

            return results;
        }

        return fallback;
    }

    /**
     * Fetches the list at the requested position in this section. This works
     * by calling getObject(key). If the object is a collection, its elements
     * are added to a new list. If the object is a map, its keys are added to
     * a new list. This new list is returned. If it's neither a set nor map,
     * then null is returned instead. This method calls getList(key, clazz, null)
     *
     * @param key   the location of the list to fetch
     * @param clazz the type of list you want. Eg, String.class or Integer.class
     * @return the list if successful, or the null if unsuccessful.
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return getList(key, clazz, null);
    }
}