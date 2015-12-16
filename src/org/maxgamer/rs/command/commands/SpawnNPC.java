package org.maxgamer.rs.command.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.Util;

/**
 * @author netherfoam
 */
@CmdName(names = { "spawnmob", "mob" })
public class SpawnNPC implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) throws SQLException {
		if (args.length < 1) {
			p.sendMessage("Arg0: NPC Id");
			p.sendMessage("Arg1: Permanent (true|false, default is false)");
			return;
		}
		
		try {
			boolean save = false;
			if(args.length >= 2){
				try{
					save = Util.parseBoolean(args[1]);
				}
				catch(ParseException e){
					p.sendMessage("Invalid true|false value given for Arg1, given '" + args[1] + "', use true or false instead.");
					return;
				}
			}
			
			Location l = p.getLocation();
			NPC n = new NPC(Integer.parseInt(args[0]), l);
			n.setSpawn(p.getLocation());
			
			if(!save){
				p.sendMessage("Spawned NPC " + args[0] + ": " + n.getDefinition().getName() + " temporarily at " + l);
			}
			else{
				if(l.getMap() != Core.getServer().getMap()){
					p.sendMessage("Currently, we cannot spawn mobs in worlds other than the main one permanently. The maps API needs more work and it's not simple!");
					return;
				}
				Connection con = Core.getWorldDatabase().getConnection();
				PreparedStatement ps = con.prepareStatement("INSERT INTO npc_spawns (npc, x, y, z) VALUES (?, ?, ?, ?)");
				ps.setInt(1, n.getDefinition().getId());
				ps.setInt(2, l.x);
				ps.setInt(3, l.y);
				ps.setInt(4, l.z);
				ps.execute();
				p.sendMessage("Spawned NPC " + args[0] + ": " + n.getDefinition().getName() + " permanently at " + l);
			}
		}
		catch (WorldFullException e) {
			p.sendMessage("World has no more room for NPC's");
			return;
		}
		catch (NumberFormatException e) {
			p.sendMessage("Invalid number supplied for NPC ID, given " + args[0]);
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}