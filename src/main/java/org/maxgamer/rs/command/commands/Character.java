package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.MobModel;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

import java.util.Random;

/**
 * @author netherfoam
 */
public class Character implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) {
        p.sendMessage("Randomizing Character...");
        MobModel model = p.getModel();
        Random r = new Random();
        for (int i = 0; i < model.getColour().length; i++) {
            model.getColour()[i] = (byte) (r.nextInt(MobModel.getMaxColor(i) - 1) + 1);
        }
        model.setChanged(true);
    }

    @Override
    public int getRankRequired() {
        return Rights.USER;
    }
}