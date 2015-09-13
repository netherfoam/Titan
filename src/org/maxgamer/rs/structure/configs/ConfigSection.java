package org.maxgamer.rs.structure.configs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Represents a configuration section in YML. Internally,
 * this class is simply a map of <String, Object> where the
 * objects are values or possibly, more ConfigSections.
 * 
 * This is an easy way of storing and loading data from
 * config files. It uses the SnakeYAML library, coupled
 * with this project.
 * @author netherfoam
 */
public class ConfigSection implements Map<String, Object>{
	/**
	 * The map of <String, Object> contained in this section.
	 * This is the key-values of the map. Note that the key
	 * cannot contain full stops ('.').
	 */
	protected Map<String, Object> map;
	
	/**
	 * Creates a new, blank config section
	 */
	public ConfigSection(){
		this(new HashMap<String, Object>());
	}
	
	@SuppressWarnings("unchecked")
	public ConfigSection(String string){
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml parser = new Yaml(options);
		map = (Map<String, Object>) parser.load(string);
	}
	
	/**
	 * Creates a new ConfigSection, using the given InputStream
	 * as a source.
	 * @param in the input stream to read the config from.
	 */
	@SuppressWarnings("unchecked")
	public ConfigSection(InputStream in){
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml parser = new Yaml(options);
		map = (Map<String, Object>) parser.load(in);
	}
	
	/**
	 * Creates a new ConfigSection from the given map of
	 * values.
	 * @param values The values
	 * @throws NullPointerException if the values map is null
	 */
	public ConfigSection(Map<String, Object> values){
		if(values == null){
			throw new NullPointerException("Config section may not have a null map.");
		}
		
		this.map = values;
	}
	
	/**
	 * Returns true if this config section contains no values.
	 * @return true if the map is empty.
	 */
	public boolean isEmpty(){
		return this.map.isEmpty();
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
	public void set(String s, Object o){
		if(s == null){
			throw new NullPointerException("Key may not be null.");
		}
		
		String[] parts = s.split("\\.");
		
		Map<String, Object> node = map;
		
		for(int i = 0; i < parts.length - 1; i++){
			Object q = node.get(parts[i]);
			if(q == null){
				q = new HashMap<String, Object>();
				node.put(parts[i], q);
				node = (HashMap<String, Object>) q;
			}
			else if(q instanceof Map){
				node = (HashMap<String, Object>) q;
			}
			else{
				//Q is not a map
				//We must delete Q to store O.
				q = new HashMap<String, Object>();
			}
		}
		
		//Now node is the parent of where we wish to store O.
		if(o instanceof ConfigSection){
			//ConfigSection is a nice way of saying map.
			o = ((ConfigSection) o).map;
		}
		if(o == null){
			node.remove(parts[parts.length - 1]);
			if(node.isEmpty() && parts.length > 1){
				StringBuilder sb = new StringBuilder(parts[0]);
				for(int i = 1; i < parts.length - 1; i++){
					sb.append("." + parts[i]);
				}
				set(sb.toString(), null);
			}
		}
		else{
			node.put(parts[parts.length - 1], o);
		}
	}
	
	/**
	 * Fetches the map at the specified key, or if it is null or not
	 * a map, returns the given fallback.
	 * @param key the key to search for
	 * @param k the maps key class, eg. String.class (For a Map<String, ?>)
	 * @param v the maps value class, eg. Integer.class (For a Map<?, Integer>)
	 * @param fallback the return value if this method fails
	 * @return the map, or the fallback on failure
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(String key, Class<K> k, Class<V> v, Map<K, V> fallback){
		Object o = getObject(key);
		if(o == null) return fallback;
		if(o instanceof Map == false) return fallback;
		
		Map<?, ?> map = (Map<?, ?>) o;
		Map<K, V> results = new HashMap<K, V>();
		for(Entry<?, ?> e : map.entrySet()){
			if(k.isInstance(e.getKey()) && v.isInstance(e.getValue())){
				results.put((K) e.getKey(), (V) e.getValue());
			}
		}
		return results;
	}
	
	/**
	 * Fetches the map at the specified key, or if it is null or not
	 * a map, returns null
	 * @param key the key to search for
	 * @param k the maps key class, eg. String.class (For a Map<String, ?>)
	 * @param v the maps value class, eg. Integer.class (For a Map<?, Integer>)
	 * @return the map, or the null on failure.
	 */
	public <K, V> Map<K, V> getMap(String key, Class<K> k, Class<V> v){
		return getMap(key, k, v, null);
	}
	
	/**
	 * Fetches the byte array at the specified key, or returns the
	 * given fallback if it was not found or invalid.
	 * @param key The key to retrieve
	 * @param fallback the return value if the value was not found or invalid
	 * @return the byte array, or the fallback if unsuccessful.
	 */
	public byte[] getByteArray(String key, byte[] fallback){
		Object o = getObject(key);
		if(o == null) return fallback;
		
		try{
			return (byte[]) o;
		}
		catch(ClassCastException e){
			return fallback;
		}
	}
	
	/**
	 * Fetches the byte array at the specified key, or returns
	 * null if it was not found or invalid.
	 * @param key The key to retrieve
	 * @return the byte array, or the null if unsuccessful.
	 */
	public byte[] getByteArray(String key){
		return getByteArray(key, null);
	}
	
	/**
	 * Fetches the list at the requested position in this section. This works
	 * by calling getObject(key). If the object is a collection, its elements
	 * are added to a new list. If the object is a map, its keys are added to
	 * a new list. This new list is returned. If it's neither a set nor map,
	 * then the fallback list is returned instead.
	 * @param key the location of the list to fetch
	 * @param clazz the type of list you want. Eg, String.class or Integer.class
	 * @param fallback the list to fall back on if key wasn't found or wasn't a list.
	 * @return the list if successful, or the fallback if unsuccessful.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key, Class<T> clazz, List<T> fallback){
		Object o = getObject(key);
		
		if(o == null) return fallback;
		
		if(o instanceof Collection){
			Collection<?> set = (Collection<?>) o;
			List<T> results = new ArrayList<T>(set.size());
			
			for(Object p : set){
				if(clazz.isInstance(p)){
					results.add((T) p);
				}
			}
			
			return results;
		}
		
		if(o instanceof Map){
			Map<?, ?> map = (Map<?, ?>) o;
			List<T> results = new ArrayList<T>(map.size());
			
			for(Object p : map.keySet()){
				if(clazz.isInstance(p)){
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
	 * @param key the location of the list to fetch
	 * @param clazz the type of list you want. Eg, String.class or Integer.class
	 * @return the list if successful, or the null if unsuccessful.
	 */
	public <T> List<T> getList(String key, Class<T> clazz){
		return getList(key, clazz, null);
	}
	
	
	/**
	 * Fetches a set of strings which represent the keys for this
	 * config section. This set is immutable.
	 * @return the set of strings
	 */
	public Set<String> getKeys(){
		return Collections.unmodifiableSet(map.keySet());
	}
	
	/**
	 * Fetches the section under the given key, or returns fallback if there
	 * was an issue.  If the section contains the key, but it is not a Config
	 * Section, then the fallback is returned.
	 * @param key The key
	 * @param fallback return value if the value was not specified or is not a ConfigSection
	 * @return the section
	 */
	public ConfigSection getSection(String key, ConfigSection fallback){
		Object o = getObject(key);
		try{
			if(o != null && o instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) o;
				return new ConfigSection(map);
			}
		}
		catch(Exception e){}
		return fallback;
	}
	
	/**
	 * Fetches the section under the given key, or returns a blank config section
	 * if there was an issue. Note that this differs from other methods which return
	 * a null fallback object. If you desire null on error, use getSection(key, null).
	 * @param key the key
	 * @return the config section, never null.
	 */
	public ConfigSection getSection(String key){
		ConfigSection c = getSection(key, null);
		if(c == null) c = new ConfigSection();
		return c;
	}
	
	/**
	 * Fetches the raw object at the given location.
	 * @param s the key to search for, may contain '.' for subsections.
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	public Object getObject(String s){
		if(s == null){
			throw new NullPointerException("Key may not be null.");
		}
		
		String[] parts = s.split("\\.");
		
		Map<String, Object> last = map;
		for(int i = 0; i < parts.length - 1; i++){
			Object q = last.get(parts[i]);
			if(q == null  || q instanceof Map == false){
				return null;
			}
			last = (Map<String, Object>) q;
		}
		
		Object o = last.get(parts[parts.length - 1]); 
		
		if(o instanceof Map){
			/*
			 * If we're retrieving a map, then we should check if we can cast
			 * it to a config section. This is typesafe in that it prevents us
			 * from using a map that does not have keys as strings.
			 */
			HashMap<String, Object> result = new HashMap<String, Object>();
			for(Entry<?, ?> e : ((Map<?, ?>) o).entrySet()){
				if(e.getKey() instanceof String == false) return o; //Key is not String, we can't help.
				
				result.put((String) e.getKey(), e.getValue());
			}
			
			return new ConfigSection(result);
		}
		
		return o;
	}
	
	/**
	 * Fetches the integer at the given key.
	 * @param k the key
	 * @return the value or 0 if not found.
	 */
	public int getInt(String k){
		return getInt(k, 0);
	}
	
	/**
	 * Fetches the integer at the given key, allowing a
	 * fallback value to be specified.
	 * @param k the key to search for
	 * @param fallback the value to use if the key is not found.
	 * @return the value, or fallback if not found.
	 */
	public int getInt(String k, int fallback){
		try{
			return ((Number) getObject(k)).intValue();
		}
		catch(Exception e){
			return fallback;
		}
	}
	
	/**
	 * Fetches the long at the given key.
	 * @param k the key
	 * @return the value or 0 if not found.
	 */
	public long getLong(String k){
		return getLong(k, 0);
	}
	
	/**
	 * Fetches the long at the given key, allowing a
	 * fallback value to be specified.
	 * @param k the key to search for
	 * @param fallback the value to use if the key is not found.
	 * @return the value, or fallback if not found.
	 */
	public long getLong(String k, long fallback){
		try{
			Number n = (Number) getObject(k);
			return n.longValue();
		}
		catch(Exception e){
			return fallback;
		}
	}
	
	/**
	 * Fetches the double at the given key.
	 * @param k the key
	 * @return the value or 0 if not found.
	 */
	public double getDouble(String k){
		return getDouble(k, 0);
	}
	
	/**
	 * Fetches the double at the given key, allowing a
	 * fallback value to be specified.
	 * @param k the key to search for
	 * @param fallback the value to use if the key is not found.
	 * @return the value, or fallback if not found.
	 */
	public double getDouble(String k, double fallback){
		try{
			Number n = (Number) getObject(k);
			return n.doubleValue();
		}
		catch(Exception e){
			return fallback;
		}
	}
	
	/**
	 * Fetches the String at the given key.
	 * @param k the key
	 * @return the value or null if not found.
	 */
	public String getString(String k){
		return getString(k, null);
	}
	
	/**
	 * Fetches the String at the given key, allowing a
	 * fallback value to be specified.
	 * @param k the key to search for
	 * @param fallback the value to use if the key is not found.
	 * @return the value, or fallback if not found.
	 */
	public String getString(String k, String fallback){
		try{
			return getObject(k).toString();
		}
		catch(Exception e){
			return fallback;
		}
	}
	
	/**
	 * Fetches the boolean at the given key.
	 * @param k the key
	 * @return the value or false if not found.
	 */
	public boolean getBoolean(String k){
		return getBoolean(k, false);
	}
	
	/**
	 * Fetches the boolean at the given key, allowing a
	 * fallback value to be specified.
	 * @param k the key to search for
	 * @param fallback the value to use if the key is not found.
	 * @return the value, or fallback if not found.
	 */
	public boolean getBoolean(String k, boolean fallback){
		try{
			Object o = getObject(k);
			if(o instanceof Boolean){
				return (Boolean) o;
			}
			else if(o instanceof Number){
				return ((Number) o).doubleValue() != 0; //Nonzero values are true.
			}
			return (Boolean) o; //This will probably fail.
		}
		catch(Exception e){
			return fallback;
		}
	}
	
	@Override
	public String toString(){
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml parser = new Yaml(options);
		return parser.dump(map);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}
}