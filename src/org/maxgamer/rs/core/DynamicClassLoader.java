package org.maxgamer.rs.core;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.maxgamer.rs.lib.log.Log;

/**
 * @author netherfoam
 */
public class DynamicClassLoader extends URLClassLoader {
	//HashMap of <SimpleName, FullName> for classes
	private HashMap<String, String> classNames = new HashMap<String, String>(512);
	
	public DynamicClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
		
		try {
			while (parent != null) {
				Field f = ClassLoader.class.getDeclaredField("classes");
				f.setAccessible(true);
				
				@SuppressWarnings("unchecked")
				Vector<Class<?>> classes = (Vector<Class<?>>) f.get(parent);
				for (Class<?> c : new Vector<Class<?>>(classes)) {
					classNames.put(c.getSimpleName(), c.getName());
				}
				parent = parent.getParent();
			}
		}
		catch (Exception e) {
			//This could break plugins/etc that use getFullName() or getClassNames();
			Log.warning("Failed to list loaded classes.");
			e.printStackTrace();
		}
	}
	
	public String getFullName(String simpleClassName) {
		return classNames.get(simpleClassName);
	}
	
	@Override
	public void addURL(URL url) { //Public, not protected
		super.addURL(url);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> c = super.loadClass(name);
		
		if (classNames.containsKey(c.getSimpleName()) == false) {
			classNames.put(c.getSimpleName(), c.getName());
		}
		return c;
	}
	
	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		
		Class<?> c = super.loadClass(name, resolve);
		
		if (classNames.containsKey(c.getSimpleName()) == false) {
			classNames.put(c.getSimpleName(), c.getName());
		}
		
		return c;
	}
	
	/**
	 * An unmodifiable map of <SimpleName, FullName> for all classes loaded by
	 * this class loader. Not null.
	 * @return An unmodifiable map of <SimpleName, FullName> for all classes
	 *         loaded by this class loader.
	 */
	public Map<String, String> getClassNames() {
		return Collections.unmodifiableMap(classNames);
	}
	
}