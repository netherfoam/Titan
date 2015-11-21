package org.maxgamer.rs.model.entity.mob.persona;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * Represents an option that may be clicked when a player is right clicked.
 * @author netherfoam
 */
public abstract class PersonaOption {
	/** The display text */
	private final String text;
	
	/**
	 * Creates a new PlayerOption. You should cache this somewhere (Eg, only
	 * create one instance) as it will be more efficient, but this is not
	 * required. (Also, this will prevent duplicates when adding the option to
	 * the player menu)
	 * @param text The display text for the option.
	 */
	public PersonaOption(String text) {
		if (text == null) throw new NullPointerException("Text may not be null");
		this.text = text;
	}
	
	/**
	 * Returns the display text for this option. Eg, 'Attack'
	 * @return the display text for this option. Eg, 'Attack'
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Invoked when the player clicks this option on another player
	 * @param clicker The player who clicked the option
	 * @param target The player who was selected when the option was clicked.
	 */
	public abstract void run(Mob clicker, Persona target);
	
	@Override
	public String toString() {
		return text;
	}
}