package org.maxgamer.rs.event;

public enum EventPriority {
	/** 
	 * The lowest priority. A listener will receive the warning very early.
	 * It is safe to cancel events here */
	LOWEST,
	/** 
	 * The low priority. A listener will receive the action fairly early 
	 * It is safe to cancel events here*/
	LOW,
	/** 
	 * Normal priority. The listener will receive the action about halfway.
	 * At this stage, you probably don't want to cancel events.
	 * This is the default priority.
	 */
	NORMAL,
	/**
	 * High priority. The listener will receive this action almost last.
	 * At this stage, you don't want to cancel the event. (It is still legal)
	 */
	HIGH,
	/**
	 * Highest priority. The listener will receive this action practically last.
	 * At this stage, you don't want to cancel the event. (It is still legal)
	 */
	HIGHEST,
	/**
	 * Monitor priority. This priority higher than HIGHEST. 
	 * The listener will receive this action practically last.
	 * YOU SHOULD NOT CANCEL THE EVENT HERE! This is designed for things such as
	 * loggers, which need to know whether an action was successful or not.
	 */
	MONITOR;
}