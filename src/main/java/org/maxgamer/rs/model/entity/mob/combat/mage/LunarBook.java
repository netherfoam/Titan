package org.maxgamer.rs.model.entity.mob.combat.mage;

import java.util.HashMap;

/**
 * @author netherfoam
 */
public class LunarBook extends Spellbook {
	private HashMap<Integer, Spell> spells = new HashMap<Integer, Spell>();
	
	protected LunarBook() {
		super(430);
	}
	
	@Override
	public Spell getSpell(int id) {
		return spells.get(id);
	}
	
	@Override
	public Spell getSpell(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Spell[] getSpells() {
		return spells.values().toArray(new Spell[spells.size()]);
	}
}