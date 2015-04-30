package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "z", "int" })
public class WorldMapBank implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		
		player.getWindow().open(new PrimaryInterface(player, (short) Short.parseShort(args[0])) {
			@Override
			public void onClick(int option, int buttonId, int slotId, int itemId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isMobile() {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		//player.getProtocol().sendBConfig(622, player.getLocation().x << 14 | player.getLocation().y | player.getLocation().z << 28);
		//player.getUpdateMask().setAnimation(new Animation(840));
		//player.getProtocol().sendBConfig(674, player.getLocation().x << 14 | player.getLocation().y | player.getLocation().z << 28);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}