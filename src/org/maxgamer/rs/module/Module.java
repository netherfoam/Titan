package org.maxgamer.rs.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.structure.configs.FileConfig;

/**
 * A Module class which represents a plugin that may be loaded from the modules
 * directory after it has been archived into a .jar file and had a module.yml
 * file added to the root of the JAR. The module.yml should specify a class:
 * package.for.module.ModuleName property as well as a name: ModuleName
 * property. These modules are loaded on startup and shutdown and can allow
 * integration with the server through Java code, instead of say script code.
 * @author netherfoam
 */
public abstract class Module {
	private ModuleMeta meta;
	
	/**
	 * Constructs a new Module with NULL meta.
	 */
	public Module() {
		
	}
	
	/**
	 * Protected method, should not be touched by the module.
	 * @param m the meta
	 */
	protected void setMeta(ModuleMeta m) {
		this.meta = m;
	}
	
	/**
	 * Called on start up when this module is loaded
	 * @throws Exception if something goes wrong
	 */
	protected abstract void load() throws Exception;
	
	/**
	 * Called on shutdown when this module is unloaded
	 * @throws Exception if something goes wrong
	 */
	protected abstract void unload() throws Exception;
	
	/**
	 * The name of this module
	 * @return
	 */
	public final String getName() {
		return meta.getName();
	}
	
	/**
	 * Fetches the config specific to this module. This does not have to be set
	 * to non-null.
	 * @return the config
	 */
	public final FileConfig getConfig() {
		return meta.getConfig();
	}
	
	/**
	 * Fetches the data folder for this plugin. This folder may or may not exist
	 * yet, but will be modules/getName().toLowerCase()
	 * @return
	 */
	public final File getFolder() {
		File f = new File("modules", getName().toLowerCase());
		return f;
	}
	
	public final Server getServer(){
		return Core.getServer();
	}
	
	public final EventManager getEvents(){
		return getServer().getEvents();
	}
	
	public final CommandManager getCommands(){
		return getServer().getCommands();
	}
	
	/**
	 * Fetches the InputStream of a resource from this Module's .JAR file. If the file
	 * is not located in the JAR file, then this method returns null
	 * @param name the path and name of the file inside the jar, using "/" separators
	 * @return the stream or null if not found
	 */
	public final InputStream getResource(String name){
		return this.meta.getLoader().getResourceAsStream(name);
	}
	
	/**
	 * Saves the given resource from the JAR to the corresponding directory in the plugin's data folder.
	 * Example, if the resource "path/to/resource.png" is saved, the result is modules/MODULE_NAME/path/
	 * to/resource.png
	 * 
	 * @param name the name of the file to write
	 * @throws IOException if the file is not found, or an error occurs writing to disk
	 */
	public final File saveResource(String name) throws IOException{
		InputStream stream = getResource(name);
		if(stream == null) throw new FileNotFoundException("File " + name + " not found in JAR");
		
		File dest = new File(getFolder(), name);
		dest.getParentFile().mkdirs();
		dest.createNewFile();
		
		FileOutputStream out = new FileOutputStream(dest);
		byte[] buffer = new byte[Math.min(65536, stream.available())];
		int n;
		while((n = stream.read(buffer)) > 0){
			out.write(buffer, 0, n);
		}
		out.close();
		stream.close();
		return dest;
	}
	
	/**
	 * Fetches the file which this module was loaded from. Typically this will
	 * be a file in the Modules folder.
	 * @return the file this was loaded from
	 */
	public final File getJar() {
		return meta.getJar();
	}
	
	public ModuleMeta getMeta() {
		return meta;
	}
}