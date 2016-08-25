package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.skill.prayer.PrayerType;

/**
 * Called during the start() call of a Persona for the first time
 * @author netherfoam
 *
 */
public class PrayerToggleEvent extends PersonaEvent implements Cancellable {
	private boolean cancel;
	private boolean enable;
	
	public PrayerToggleEvent(Persona p, PrayerType prayer, boolean enable) {
		super(p);
		this.enable = enable;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	/**
	 * Returns true if the prayer is being enabled
	 * @return true if the prayer is being enabled
	 */
	public boolean isEnabling() {
		return enable;
	}
	
	/**
	 * Returns true if the prayer is being disabled
	 * @return true if the prayer is being disabled
	 */
	public boolean isDisabling() {
		return !enable;
	}
}