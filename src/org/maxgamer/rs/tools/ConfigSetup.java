package org.maxgamer.rs.tools;

import java.util.regex.Pattern;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.structure.configs.FileConfig;

/**
 * Configuration utility that requests the user to configure their YML files. This can be used
 * when the server is first booting.
 * 
 * @author Dirk Jamieson
 * @date 18 Nov 2015
 */
public class ConfigSetup {
	/**
	 * Interacts with the terminal and asks the user fill in the required components of the
	 * world.yml config file.
	 * @param config the config file.
	 */
	public static void world(FileConfig config){
		Prompter p = new Prompter();
		
		p.println();
		p.println("Please take a moment to configure world.yml. This is required set-up, or your server will not function!");
		System.out.println("THIS SHOULD NEVER PRINT!");
		p.println(" -- Database Setup --");
		p.println("Please note that SQLite is not functional yet!");
		p.print("Use MySQL [true]: ");
		if(p.getBoolean(true)){
			config.set("database.type", "mysql");
			
			p.print("Database host [localhost]: ");
			config.set("database.host", p.getString("localhost"));
			
			p.print("Database user [root]: ");
			config.set("database.user", p.getString(Pattern.compile("[A-Za-z0-9_]{1,}"), "root"));
			
			p.print("Database password for " + config.getString("database.user") + ": ");
			config.set("database.pass", p.getString());
			
			p.print("Database name [titan]: ");
			config.set("database.name", p.getString(Pattern.compile("[A-Za-z0-9_]{1,}"), "titan"));
			
			p.print("Database port [3306]: ");
			config.set("database.port", p.getInt(3306));
		}
		else{
			config.set("database.type", "sqlite");
			p.print("File [data/titan.db]: ");
			config.set("database.file", p.getString("data/titan.db"));
		}
		
		p.println("Testing database connection...");
		/* This will fail and abort the JVM if the database details were incorrect */
		Core.getWorldDatabase();
		
		p.print("-- Client Setup --");
		p.print("Server Port [43594]: ");
		config.set("world.port", p.getInt(43594));
		
		p.println("Thanks for taking the time to configure your server!");
		p.println();
		p.close();
	}
	
	/**
	 * Interacts with the terminal and asks the user fill in the required components of the
	 * logon.yml config file.
	 * @param config the config file.
	 */
	public static void logon(FileConfig config){
		Prompter p = new Prompter();
		p.println();
		p.println("Please take a moment to configure logon.yml. This is required set-up, or your server will not function!");
		p.println(" -- Database Setup --");
		p.println("Please note that SQLite is not functional yet!");
		System.out.println("THIS SHOULD NEVER PRINT EITHER!");
		p.print("Use MySQL [true]: ");
		if(p.getBoolean(true)){
			config.set("database.type", "mysql");
			
			p.print("Database host [localhost]: ");
			config.set("database.host", p.getString("localhost"));
			
			p.print("Database user [root]: ");
			config.set("database.user", p.getString(Pattern.compile("[A-Za-z0-9_]{1,}"), "root"));
			
			p.print("Database password for " + config.getString("database.user") + ": ");
			config.set("database.pass", p.getString());
			
			p.print("Database name [titan]: ");
			config.set("database.name", p.getString(Pattern.compile("[A-Za-z0-9_]{1,}"), "titan"));
			
			p.print("Database port [3306]: ");
			config.set("database.port", p.getInt(3306));
		}
		else{
			config.set("database.type", "sqlite");
			p.print("File [data/titan.db]: ");
			config.set("database.file", p.getString("data/titan.db"));
		}
		
		p.println(" -- Logon Security -- ");
		p.print("Host [localhost]: ");
		config.set("host", p.getString("localhost"));
		
		if("localhost".equalsIgnoreCase(config.getString("host"))){
			String random = Erratic.nextString(12);
			p.print("Pass [" + random + "]: ");
			config.set("pass", p.getString(random));
		}
		else{
			p.print("Pass: ");
			config.set("pass", p.getString());
		}
		
		p.print("Port [9692]: ");
		config.set("port", p.getInt(9692));
		
		p.println("Thanks for taking your time to configure your login server!");
		p.println();
		p.close();
	}
	
	private ConfigSetup(){
		// Private constructor
	}
}
