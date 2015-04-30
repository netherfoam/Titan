package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.events.mob.persona.PrayerToggleEvent;
import org.maxgamer.rs.interfaces.SettingsBuilder;
import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.skill.prayer.PrayerType;

/**
 * @author netherfoam
 */
public class PrayerInterface extends SideInterface {
	private static final SettingsBuilder INTERFACE_CONFIG;
	static {
		INTERFACE_CONFIG = new SettingsBuilder();
		INTERFACE_CONFIG.setSecondaryOption(0, true); //Can use this option.
	}
	
	public PrayerInterface(Player p) {
		super(p, (short) 271, (short) 93);
		//Allows the player to click 'Activate' on prayers
		setAccessMask(INTERFACE_CONFIG.getValue(), 0, 29, 8);//Amask to enable prayer activating and deactivating
		setAccessMask(INTERFACE_CONFIG.getValue(), 0, 29, 42);//AMask to enable selecting in the quick prayer editing interface
		//Required for the above access mask to be used
		player.getProtocol().sendBConfig(181, 0);
		//The type of prayer book
		player.getProtocol().sendConfig(1584, p.getPrayer().getPrayerBook()); //1 for ancient curses, 0 for normal prayers
		int statBits = (30) | (30 << 6) | (30 << 12) | (30 << 18) | (30 << 24);//Default effect 0%
		player.getProtocol().sendConfig(1583, statBits);//stat adjustments
		
	}
	
	@Override
	public void onClick(int option, int buttonId, int slot, int itemId) {
		if (65535 == slot && buttonId != 43) return;//We don't want the server search for key with slotID 65535 because it just doesn't exist, this is the stat bar.
		PrayerType key = PrayerType.findBySlotID(slot, player.getPrayer().isAncient());
		if (buttonId == 43) {
			player.getPrayer().setQuickPrayerEditing(false);
		}
		else {
			if (player.getPrayer().passReqs(player, key)) {
				if (player.getPrayer().isSelectingQuickPrayers()) {
					player.getPrayer().setQuick(!player.getPrayer().isQuick(key), key);
				}
				else {
					boolean enable = !player.getPrayer().isEnabled(key);
					PrayerToggleEvent e = new PrayerToggleEvent(getPlayer(), key, enable);
					e.call();
					if (e.isCancelled()) {
						return; //Cancelled
					}
					player.getPrayer().setEnabled(enable, key);
				}
			}
		}
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
}
