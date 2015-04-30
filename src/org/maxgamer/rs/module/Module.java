package org.maxgamer.rs.module;

import java.io.File;

import org.maxgamer.structure.configs.FileConfig;

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