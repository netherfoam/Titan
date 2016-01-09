package org.maxgamer.rs.command.commands;

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
import org.maxgamer.rs.model.map.spawns.NPCSpawn;
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
			
			if(!save){
				NPC n = new NPC(Integer.parseInt(args[0]), l);
				p.sendMessage("Spawned NPC " + args[0] + ": " + n.getDefinition().getName() + " temporarily at " + l);
			}
			else{
				if(Core.getServer().getMaps().isPersisted(p.getMap()) == false){
					p.sendMessage("The map you are in is not persistant. You should persist the map before spawning NPC's permanently in it.");
					return;
				}
				try{
					NPCSpawn spawn = new NPCSpawn(Integer.parseInt(args[0]), l);
					spawn.insert(Core.getWorldDatabase().getConnection());
					NPC n = spawn.spawn();
					p.sendMessage("Spawned NPC " + args[0] + ": " + n.getDefinition().getName() + " permanently at " + l);
				}
				catch(SQLException e){
					p.sendMessage("Failed to contact the MySQL database: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")");
					e.printStackTrace();
				}
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