package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.events.mob.MobTeleportEvent.TeleportCause;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.repository.AbstractRepository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

public class Warp implements PlayerCommand {
    private DestinationRepository repository;

    public Warp() {
        Core.getServer().getDatabase().flush();
        repository = Core.getServer().getDatabase().getRepository(DestinationRepository.class);
    }

    @Override
    public void execute(Player p, String[] args) throws Exception {
        if (args.length < 1) {
            p.sendMessage("Usage: ::warp [target]");
            List<Destination> destinations = repository.findAll();
            StringBuilder sb = new StringBuilder(destinations.size() * 12);
            for (Destination d : destinations) {
                sb.append(d.name + ", ");
            }
            if (sb.length() > 0) {
                // Trim off the last ", "
                sb = sb.replace(sb.length() - 2, sb.length(), "");
            }
            p.sendMessage("Destinations: " + sb.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                p.sendMessage("Usage ::warp add [name of warp]");
                return;
            }

            Destination dest = new Destination();
            Location l = p.getLocation();
            dest.x = l.x;
            dest.y = l.y;
            dest.z = l.z;

            dest.name = args[1].toLowerCase();
            for (int i = 2; i < args.length; i++) dest.name += " " + args[i];

            Core.getServer().getSession().persist(dest);

            p.sendMessage("Saved the warp as ::warp " + dest.name);

            return;
        }

        String name = args[0];
        for (int i = 1; i < args.length; i++) name += " " + args[i];

        Destination d = repository.find(name.toLowerCase());
        if (d == null) {
            d = repository.findByPrefix(name.toLowerCase());

            if (d == null) {
                p.sendMessage("No such destination '" + name + "'. Do ::warp for a list of destinations.");
                return;
            }
        }

        p.teleport(new Location(d.x, d.y, d.z), TeleportCause.SERVER);
        p.sendMessage("Warped to " + d.name);
    }

    @Override
    public int getRankRequired() {
        return Rights.MOD;
    }

    @Entity(name = "Destination")
    @Table(name = "Warp")
    public static class Destination {
        @Id
        private String name;

        @Column
        private int x;

        @Column
        private int y;

        @Column
        private int z;
    }

    public static class DestinationRepository extends AbstractRepository<Destination> {
        public DestinationRepository() {
            super(Destination.class);
        }

        public Destination findByPrefix(String name) {
            return (Destination) getManager().createQuery("FROM " + this.name() + " WHERE name LIKE :name").setParameter("name", name + "%").uniqueResult();
        }
    }
}