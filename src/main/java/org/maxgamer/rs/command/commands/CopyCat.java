package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
@CmdName(names = { "" })
public class CopyCat implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		Persona target = Core.getServer().getPersonas().getPersona(args[0], true);
		if (target == null) {
			p.sendMessage("Player not found: " + args[0]);
			return;
		}
		
		p.getInventory().clear();
		for (int i = 0; i < target.getInventory().getSize(); i++) {
			p.getInventory().set(i, target.getInventory().get(i));
		}
		
		p.getEquipment().clear();
		for (int i = 0; i < target.getEquipment().getSize(); i++) {
			p.getEquipment().set(i, target.getEquipment().get(i));
		}
		
		p.getBank().clear();
		for (int i = 0; i < target.getBank().getSize(); i++) {
			p.getBank().set(i, target.getBank().get(i));
		}
		
		for (SkillType skill : SkillType.values()) {
			p.getSkills().setExp(skill, target.getSkills().getExp(skill));
		}
		
		p.sendMessage("Copied inventory, bank, equipment and skills from " + target);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}