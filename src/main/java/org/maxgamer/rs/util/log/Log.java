package org.maxgamer.rs.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author netherfoam
 */
public class Log {
	private static final Logger LOGGER = LoggerFactory.getLogger("Titan");
	
	/**
	 * Logs the given string as a debug message
	 * @param s the string to log
	 */
	public static void debug(String s) {
		LOGGER.debug(s);
	}
	
	/**
	 * Logs the given string as an info message
	 * @param s the string to log
	 */
	public static void info(String s) {
		LOGGER.info(s);
	}
	
	/**
	 * Logs the given string as a warning message
	 * @param s the string to log
	 */
	public static void warning(String s) {
		LOGGER.warn(s);
	}
	
	/**
	 * Logs the given string as a severe message
	 * @param s the string to log
	 */
	public static void severe(String s) {
		LOGGER.error(s);
	}
}