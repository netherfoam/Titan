package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.structure.Util;
import org.maxgamer.rs.util.Log;

/**
 * @author netherfoam
 */
public class Status implements GenericCommand {
    private int lastTick = Core.getServer().getTicks();
    private long lastPrint = System.currentTimeMillis();

    @Override
    public void execute(CommandSender s, String[] args) {
        s.sendMessage("Server Status:");
        s.sendMessage("Players: " + Core.getServer().getPersonas().getCount() + "/" + Core.getServer().getPersonas().getMax() + ", NPCs: " + Core.getServer().getNPCs().getCount() + "/" + Core.getServer().getNPCs().getMax());
        Log.info("Primary Thread Load: " + String.format("%.2f", (Core.getServer().getThread().getUsage() * 100)) + "%, " + ", Ticks/sec: " + ((double) (Core.getServer().getTicks() - lastTick) / ((System.currentTimeMillis() - lastPrint) / 1000.0)) + ", Active Threads: " + Thread.activeCount());
        s.sendMessage("RAM (JVM): " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB, RAM (Used): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "MB");
        long period = 0;
        int up = 0;
        int down = 0;
        for (Session session : Core.getServer().getNetwork().getSessions()) {
            period += System.currentTimeMillis() - session.getLastBandwidthReset();
            up += session.getUpload();
            down += session.getDownload();
            session.resetBandwidth();
        }
        if (period != 0) {
            s.sendMessage(String.format("Network Upload: %.2fkBps, Down: %.2fkBps", up * 1000 / period / 1000.0, down * 1000 / period / 1000.0));
        }
        long total = 0;
        for (Persona p : Core.getServer().getPersonas()) {
            for (SkillType t : SkillType.values()) {
                total += p.getSkills().getLevel(t);
            }
        }
        Log.info("Total level of all players: " + total + ", Uptime: " + Util.toDuration(System.currentTimeMillis() - Core.getServer().getStartTime()));
        lastPrint = System.currentTimeMillis();
        lastTick = Core.getServer().getTicks();
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}