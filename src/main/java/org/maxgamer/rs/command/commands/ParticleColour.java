package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.MobParticles;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.util.Erratic;

public class ParticleColour implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) {
        if (args[0].equalsIgnoreCase("print")) {
            MobParticles particles = p.getModel().getParticles();
            if(particles == null) {
                p.sendMessage("No particles active");
                return;
            }

            p.sendMessage("Colour: " + Integer.toHexString(particles.getRed()) + "" + Integer.toHexString(particles.getGreen()) + "" + Integer.toHexString(particles.getBlue()));
            p.sendMessage("Ambient: " + particles.getAmbient());
            p.sendMessage("Intensity: " + particles.getIntensity());
            return;
        } else if (args[0].equalsIgnoreCase("default")) {
            p.getModel().setParticles(null);
            p.sendMessage("Default sweet default!");
            return;
        } else if (args[0].equalsIgnoreCase("random")) {
            MobParticles particles = new MobParticles(Erratic.nextInt(255), Erratic.nextInt(255), Erratic.nextInt(255), Erratic.nextInt(30, 60), Erratic.nextInt(30, 60));
            p.getModel().setParticles(particles);
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
        int r = Integer.parseInt(red, 16);
        int g = Integer.parseInt(green, 16);
        int b = Integer.parseInt(blue, 16);
        int a = Integer.parseInt(args[1]);
        int i = Integer.parseInt(args[2]);

        MobParticles particles = new MobParticles(r, g, b, a, i);
        p.getModel().setParticles(particles);
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}