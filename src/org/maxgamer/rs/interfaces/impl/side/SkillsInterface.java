package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class SkillsInterface extends SideInterface {
	public SkillsInterface(Player p) {
		super(p, (short) 320, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 204 : 89));
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}
}
