package org.maxgamer.rs.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.maxgamer.rs.lib.log.Log;

import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;

public class ScriptClassLoader extends ClassLoader{
	/**
	 * Fetches all files in the given folder that can be accepted by the given
	 * filter. This method is recursive.
	 * 
	 * @param dir The directory to search
	 * @param filter The filter. Must not be null.
	 * @return The list of files, never null.
	 */
	private static LinkedList<File> getFiles(File dir) {
		LinkedList<File> files = new LinkedList<File>();
		if (dir == null || dir.isDirectory() == false) {
			return files;
		}
		
		for (File f : dir.listFiles()) {
			if (f.getName().startsWith(".")) {
				continue;
			}
			if (f.isDirectory()) {
				files.addAll(getFiles(f));
			}
			else{
				if(f.getName().endsWith(".class")){
					files.add(f);
				}
			}
		}
		
		return files;
	}
	
	private static byte[] read(File f) throws IOException{
		FileInputStream in = new FileInputStream(f);
		byte[] payload = new byte[in.available()];
		in.read(payload);
		in.close();
		return payload;
	}
	
	private HashMap<String, Class<?>> classes = new HashMap<>();
	private HashMap<String, byte[]> binaries = new HashMap<>();
	
	public ScriptClassLoader(ClassLoader parent){
		super(parent);
	}
	
	public synchronized void reload(File scripts){
		//Remove any previously loaded classes
		classes.clear();
		
		//First we load all of our scripts in binary form according to their class name
		for(File f : getFiles(scripts)){
			try{
				byte[] payload = read(f);
				
				ClassReference ref = ClassReference.decode(payload);
				QuasarInstrumentor inst = new QuasarInstrumentor(Thread.currentThread().getContextClassLoader());
				payload = inst.instrumentClass(ref.getClassName(), payload);
				
				binaries.put(ref.getClassName(), payload);
			}
			catch(IOException e){
				e.printStackTrace();
				Log.warning("Failed to read script class " + f.getAbsolutePath() + " from disk");
				continue;
			}
		}
		
		//Now we attempt to define the classes. Some classes will force us to load a new class
		//when they are being defined. Therefore, this will be able to skip over some classes,
		//as loading others will have already caused them to be loaded.
		for(Entry<String, byte[]> entry : this.binaries.entrySet()){
			Class<?> clazz;
			try {
				clazz = this.loadClass(entry.getKey());
				//this.defineClass(null, payload, 0, payload.length);
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(e); //This shouldn't ever happen
			} 
			this.classes.put(entry.getKey(), clazz);
		}
		//We no longer need the binaries
		this.binaries.clear();
	}
	
	public Class<?> loadClass(String name) throws ClassNotFoundException{
		try {
			return super.loadClass(name);
		}
		catch (ClassNotFoundException e) {
			if(this.binaries != null){
				byte[] payload = this.binaries.get(name);
				if(payload != null){
					return this.defineClass(name, payload, 0, payload.length);
				}
			}
			throw e;
		}
	}
	
	public Collection<Class<?>> getClasses(){
		return classes.values();
	}
}