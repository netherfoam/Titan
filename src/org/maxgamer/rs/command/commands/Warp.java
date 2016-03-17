package org.maxgamer.rs.command.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.events.mob.MobTeleportEvent.TeleportCause;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

public class Warp implements PlayerCommand{
	
	private static class Destination extends Transparent{
		@Mapping
		private String name;
		@Mapping
		private int x;
		@Mapping
		private int y;
		@Mapping
		private int z;
		
		public Destination(){
			super("Warp", new String[]{"name"}, null);
		}
	}
	
	private HashMap<String, Destination> destinations = new HashMap<String, Destination>();
	
	public Warp(){
		try{
			PreparedStatement ps = Core.getWorldDatabase().getConnection().prepareStatement("SELECT * FROM Warp");
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Destination d = new Destination();
				d.reload(rs);
				destinations.put(d.name.toLowerCase(), d);
			}
		}
		catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(Player sender, String[] args) throws Exception {
		if(args.length < 1){
			sender.sendMessage("Usage: ::warp [target]");
			StringBuilder sb = new StringBuilder(this.destinations.size() * 12);
			for(Destination d : this.destinations.values()){
				sb.append(d.name + ", ");
			}
			if(sb.length() > 0){
				// Trim off the last ", "
				sb = sb.replace(sb.length() - 2, sb.length(), "");
			}
			sender.sendMessage("Destinations: " + sb.toString());
			return;
		}
		
		Destination d = destinations.get(args[0].toLowerCase());
		if(d == null){
			sender.sendMessage("No such destination. Do ::warp for a list of destinations.");
			return;
		}
		
		sender.teleport(new Location(d.x, d.y, d.z), TeleportCause.SERVER);
		sender.sendMessage("Warped to " + d.name);
	}

	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}