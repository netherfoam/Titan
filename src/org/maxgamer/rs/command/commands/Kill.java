package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.PersonaOption;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Kill implements PlayerCommand {
	public static final PersonaOption KILL = new PersonaOption("Kill") {
		@Override
		public void run(Persona clicker, Persona target) {
			target.setHealth(0);
			if (target instanceof Player) {
				((Player) target).sendMessage(clicker + " has killed you");
			}
			if (clicker instanceof Player) {
				((Player) clicker).sendMessage("Killed " + target + ". Not a fan of fair, huh?");
			}
		}
	};
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		if (player.getPersonaOptions().contains(KILL)) {
			player.getPersonaOptions().remove(KILL);
			player.sendMessage("Right Click -> Kill Disabled");
		}
		else {
			player.getPersonaOptions().add(KILL, false);
			player.sendMessage("Right Click -> Kill Enabled");
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}