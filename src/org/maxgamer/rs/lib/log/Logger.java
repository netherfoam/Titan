package org.maxgamer.rs.lib.log;

/**
 * Represents an abstract logger, which can log information, usually with
 * timestamps and prefixes.
 * @author netherfoam
 */
public interface Logger {
	/**
	 * Logs the given string as a debug message
	 * @param s the string to log
	 */
	public abstract void debug(String s);
	
	/**
	 * Logs the given string as an info message
	 * @param s the string to log
	 */
	public abstract void info(String s);
	
	/**
	 * Logs the given string as a warning message
	 * @param s the string to log
	 */
	public abstract void warning(String s);
	
	/**
	 * Logs the given string as a severe message
	 * @param s the string to log
	 */
	public abstract void severe(String s);
	
	/**
	 * Logs the given string at the given log level.
	 * @param level the level to log it at (debug, info, severe, warning)
	 * @param s the string to log
	 */
	public abstract void log(LogLevel level, String s);
	
	/**
	 * Returns the minimum level required for a log message to be printed. Say
	 * this is set to LogLevel.WARNING, then anything that is not WARNING or
	 * SEVERE will be discarded.
	 * @return the log level required for a log message to be printed
	 */
	public abstract LogLevel getLevel();
	
	/**
	 * Represents the level a logger requires messages to be at before they will
	 * be passed.
	 * @author netherfoam
	 */
	public static enum LogLevel {
		/**
		 * Debug level, this can get quite spammy.
		 */
		DEBUG(0),
		/**
		 * Info level, this should be a nice balance between not enough and too
		 * much information.
		 */
		INFO(1),
		/**
		 * Warning level, for when things such as recoverable errors occur For
		 * example, a file could not be properly loaded but some default data
		 * can be used.
		 */
		WARNING(2),
		/**
		 * Severe level, for when things are generally irrecoverable. A severe
		 * error does not mean the program has or will crash, but does imply
		 * that something has done dramatically wrong.
		 */
		SEVERE(3);
		
		private int lv;
		
		private LogLevel(int lv) {
			this.lv = lv;
		}
		
		/**
		 * An integer representation of the level, 0 being DEBUG and 3 being
		 * SEVERE.
		 * @return an int representation of the level, higher being increasingly
		 *         serious.
		 */
		public int getLevel() {
			return lv;
		}
	}
}