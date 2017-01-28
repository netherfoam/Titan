package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
@CmdName(names = {"statlevel", "skill"})
public class SkillLevel implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) throws Exception {
        if (args.length < 2) {
            p.sendMessage("Arg0: [SKILL_NAME | ALL]");
            p.sendMessage("Arg1: Desired Level");
            return;
        }

        try {
            int level = Integer.parseInt(args[1]);

            if (args[0].equalsIgnoreCase("all")) {
                for (SkillType t : SkillType.values()) {
                    p.getSkills().setLevel(t, level);
                }
                p.sendMessage("Set ALL to level " + level);
                return;
            }

            SkillType s = SkillType.valueOf(args[0].toUpperCase());
            p.getSkills().setLevel(s, level);
            p.sendMessage("Set " + s.getName() + " level to " + level);
        } catch (NumberFormatException e) {
            p.sendMessage("Invalid level supplied, given " + args[1]);
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}