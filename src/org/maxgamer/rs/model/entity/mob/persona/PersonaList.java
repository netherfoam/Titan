package org.maxgamer.rs.model.entity.mob.persona;

import java.util.HashMap;

import org.maxgamer.rs.model.entity.EntityList;
import org.maxgamer.rs.structure.TrieSet;

/**
 * Represents an Entity list with a special feature for fetching players based
 * on their username.
 * @author netherfoam
 */
public class PersonaList extends EntityList<Persona> {
	/** The players */
	private HashMap<String, Persona> personas;
	private TrieSet names = new TrieSet();
	
	/**
	 * Constructs a new PlayerList of the given size. The size should be 2047.
	 * @param max The max size.
	 */
	public PersonaList(int max) {
		super(max);
		personas = new HashMap<String, Persona>(max);
	}
	
	/**
	 * Fetches the player by the given name, case insensitive. This uses a
	 * hashmap to get the player. If the autocomplete flag is true, then this
	 * method will attempt to get the player with a name that is prefixed by the
	 * given String. If none exists, this returns null. The suffix can be zero
	 * length (Eg, using autocomplete on a full name of an online player will
	 * return that player). Returns null if the player is not online.
	 * @param name the name of the player
	 * @param autocomplete true if you wish to autocomplete the name, false
	 *        otherwise
	 * @return the player or null if not found.
	 */
	public Persona getPersona(String name, boolean autocomplete) {
		if (name == null) throw new NullPointerException();
		name = name.toLowerCase();
		if (autocomplete) {
			//Wishes for autocomplete
			String complete = names.nearestKey(name);
			if (complete == null) return null; //Guarantees it's not in the map
			return personas.get(complete);
		}
		else {
			//No autocomplete
			return personas.get(name);
		}
	}
	
	@Override
	protected Persona set(int index, Persona p) {
		if (p != null && personas.get(p.getName().toLowerCase()) != null) {
			throw new IllegalArgumentException(p.getName() + " is already online!");
		}
		
		Persona old = super.set(index, p);
		//Update our name list.
		if (old != null) {
			personas.remove(old.getName().toLowerCase());
			names.remove(old.getName().toLowerCase());
		}
		if (p != null) {
			personas.put(p.getName().toLowerCase(), p);
			names.add(p.getName().toLowerCase());
		}
		return old;
	}
	
	/**
	 * Fetches the player by the given name, case insensitive. This uses a
	 * hashmap to get the player. Returns null if the player is not online. This
	 * calls getPersona(name, false)
	 * @param name the name of the player
	 * @return the player or null if not found.
	 */
	public Persona getPersona(String name) {
		return getPersona(name, false);
	}
}