package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class TasksInterface extends SideInterface {
	public TasksInterface(Player p) {
		super(p, (short) 1056, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 203 : 88));
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}
}
