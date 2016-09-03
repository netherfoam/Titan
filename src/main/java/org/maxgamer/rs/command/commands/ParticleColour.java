package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

import java.util.Random;

public class ParticleColour implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) {
        if (args[0].equalsIgnoreCase("print")) {
            p.sendMessage("Colour: " + Integer.toHexString(p.getModel().red) + "" + Integer.toHexString(p.getModel().green) + "" + Integer.toHexString(p.getModel().blue));
            p.sendMessage("Ambient: " + p.getModel().ambient);
            p.sendMessage("Intensity: " + p.getModel().intensity);
            return;
        } else if (args[0].equalsIgnoreCase("default")) {
            p.getModel().applyCustom = false;
            p.getModel().setChanged(true);
            p.sendMessage("Default sweet default!");
            return;
        } else if (args[0].equalsIgnoreCase("random")) {
            Random r = new Random();
            p.getModel().red = r.nextInt(255);
            p.getModel().green = r.nextInt(255);
            p.getModel().blue = r.nextInt(255);
            p.getModel().ambient = 30 + r.nextInt(30);
            p.getModel().intensity = 30 + r.nextInt(30);
            p.getModel().applyCustom = true;
            p.getModel().setChanged(true);
            p.sendMessage("Ahhh... Nothing better than a random colour!");
            return;
        }
        if (args.length < 2) {
            p.sendMessage("Arg0: Hex Colour");
            p.sendMessage("Arg1: Ambient");
            p.sendMessage("Arg2: Intensity");
            return;
        }
        String red = "" + args[0].substring(0, 2);
        String green = "" + args[0].substring(2, 4);
        String blue = "" + args[0].substring(4, 6);
        p.getModel().red = Integer.parseInt(red, 16);
        p.getModel().green = Integer.parseInt(green, 16);
        p.getModel().blue = Integer.parseInt(blue, 16);
        p.getModel().ambient = Integer.parseInt(args[1]);
        p.getModel().intensity = Integer.parseInt(args[2]);
        p.getModel().applyCustom = true;
        p.getModel().setChanged(true);
        return;
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}