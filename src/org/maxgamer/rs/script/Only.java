package org.maxgamer.rs.script;

public @interface Only {
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
}
