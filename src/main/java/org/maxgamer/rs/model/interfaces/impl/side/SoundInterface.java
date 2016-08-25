package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class SoundInterface extends PrimaryInterface {
	
	public SoundInterface(Player p) {
		super(p);
		setChildId(743);
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 20:
				getPlayer().getWindow().close(this);
				break;
		}
	}
}