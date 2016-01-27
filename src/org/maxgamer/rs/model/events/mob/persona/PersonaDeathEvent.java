package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.skill.prayer.PrayerType;

/**
 * Called during the start() call of a Persona for the first time
 * 
 * @author netherfoam
 *
 */
public class PersonaDeathEvent extends PersonaEvent {

	private int kept;
	private boolean safe = false;

	public PersonaDeathEvent(Persona p) {
		super(p);
	}

	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public void setKeepSize(int num) {
		this.kept = num;
	}

	public int getKeepSize() {
		this.kept = 3;
		if (getMob().getModel().isSkulled())
			this.kept -= 3;
		if (getMob().getPrayer().isEnabled(PrayerType.CURSE_PROTECT_ITEM) || getMob().getPrayer().isEnabled(PrayerType.PROTECT_ITEM))
			this.kept += 1;
		return this.kept;
	}

}