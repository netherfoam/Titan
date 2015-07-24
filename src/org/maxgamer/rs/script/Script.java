package org.maxgamer.rs.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Script {
	/**
	 * An array of the item ids that this class processes, or empty for all
	 * Eg, item, NPC, or object ids. Default is all
	 * @return an array of the item ids that this class processes
	 */
	public int[] ids() default {};
	
	/**
	 * An array of names that this class processes, or empty for all.
	 * Eg item, NPC or object name. Default is all. Case sensitive
	 * @return an array of the names that this class processes
	 */
	public String[] names() default {};
	
	/**
	 * An array of options that this class processes, or empty for all.
	 * Eg item, NPC or object options (Attack, Mine, Chop, Bait, Lure, etc). 
	 * Default is all. Case sensitive
	 * @return an array of the names that this class processes
	 */
	public String[] options() default {};
	
	/**
	 * The type of class that this action will handle. Classes may extend
	 * the requested type.
	 * @return The type of class that this action handles
	 */
	public Class<?> type();
}
