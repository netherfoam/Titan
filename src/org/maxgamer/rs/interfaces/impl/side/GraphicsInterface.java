package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class GraphicsInterface extends PrimaryInterface {
	
	public GraphicsInterface(Player p) {
		super(p, (short) (742));
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 14:
				getPlayer().getWindow().close(this);
				break;
		}
	}
}
