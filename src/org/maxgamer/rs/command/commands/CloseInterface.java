package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class CloseInterface implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		String name = args[0].toLowerCase();
		
		for (Interface iface : player.getWindow().getInterfaces()) {
			if (iface.getClass().getSimpleName().toLowerCase().startsWith(name)) {
				player.getWindow().close(iface);
				player.sendMessage("Closed interface: " + iface.getClass().getSimpleName());
			}
		}
	}
	
	@Override
	public int getRankRequired() {
		return 2;
	}
}