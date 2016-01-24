package org.maxgamer.rs.lib.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.maxgamer.rs.lib.log.Logger.LogLevel;

/**
 * @author netherfoam
 */
public class Log {
	private static Logger log;
	private static PrintStream ps;
	
	static {
		//If we are not initialized properly, then we
		//use a log level of debug until we are. This
		//is used if other API's hook into the project
		//without booting the core.
		init(LogLevel.DEBUG);
	}
	
	public static void init(final LogLevel level) {
		try {
			File f = new File("server.log");
			f.createNewFile();
			final PrintStream file = new PrintStream(new FileOutputStream(f, true));
			
			ps = new PrintStream(System.out) {
				@Override
				public void print(String s) {
					super.print(s);
					file.print(s);
					file.println();
				}
				
				@Override
				public void close() {
					super.close();
					file.close();
				}
			};
			
			System.setOut(ps);
			System.setErr(ps);
			log = new GenericLogger(level);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to initialize log");
		}
	}
	
	/**
	 * Logs the given string as a debug message
	 * @param s the string to log
	 */
	public static void debug(String s) {
		log.debug(s);
	}
	
	/**
	 * Logs the given string as an info message
	 * @param s the string to log
	 */
	public static void info(String s) {
		log.info(s);
	}
	
	/**
	 * Logs the given string as a warning message
	 * @param s the string to log
	 */
	public static void warning(String s) {
		log.warning(s);
	}
	
	/**
	 * Logs the given string as a severe message
	 * @param s the string to log
	 */
	public static void severe(String s) {
		log.severe(s);
	}
	
	/**
	 * Logs the given string at the given log level.
	 * @param level the level to log it at (debug, info, severe, warning)
	 * @param s the string to log
	 */
	public static void log(LogLevel level, String s) {
		log.log(level, s);
	}
	
	/**
	 * Returns the minimum level required for a log message to be printed. Say
	 * this is set to LogLevel.WARNING, then anything that is not WARNING or
	 * SEVERE will be discarded.
	 * @return the log level required for a log message to be printed
	 */
	public static LogLevel getLevel() {
		return log.getLevel();
	}
	
	/**
	 * Closes the internal printstream for this log. Calling any log functions
	 * will result in a {@link NullPointerException} after calling this.
	 */
	public static void close() {
		ps.println(); //Place a newline when shutting down
		if (ps != null) {
			ps.close();
			ps = null;
		}
	}
}