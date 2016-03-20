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
import org.maxgamer.rs.structure.TrieSet;
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
	
	private TrieSet autocomplete = new TrieSet();
	private HashMap<String, Destination> destinations = new HashMap<String, Destination>();
	
	public Warp(){
		try{
			PreparedStatement ps = Core.getWorldDatabase().getConnection().prepareStatement("SELECT * FROM Warp");
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Destination d = new Destination();
				d.reload(rs);
				destinations.put(d.name.toLowerCase(), d);
				autocomplete.add(d.name.toLowerCase());
			}
		}
		catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(Player p, String[] args) throws Exception {
		if(args.length < 1){
			p.sendMessage("Usage: ::warp [target]");
			StringBuilder sb = new StringBuilder(this.destinations.size() * 12);
			for(Destination d : this.destinations.values()){
				sb.append(d.name + ", ");
			}
			if(sb.length() > 0){
				// Trim off the last ", "
				sb = sb.replace(sb.length() - 2, sb.length(), "");
			}
			p.sendMessage("Destinations: " + sb.toString());
			return;
		}
		
		if(args[0].equalsIgnoreCase("add")){
			if(args.length < 2){
				p.sendMessage("Usage ::warp add [name of warp]");
				return;
			}
			
			Destination dest = new Destination();
			Location l = p.getLocation();
			dest.x = l.x;
			dest.y = l.y;
			dest.z = l.z;
			
			dest.name = args[1];
			for(int i = 2; i < args.length; i++) dest.name += " " + args[i];
			
			try{
				dest.insert(Core.getWorldDatabase().getConnection());
			}
			catch(SQLException e){
				p.sendMessage("Failed to save warp! " + e.getMessage());
				return;
			}
			
			this.destinations.put(dest.name.toLowerCase(), dest);
			autocomplete.add(dest.name.toLowerCase());
			
			p.sendMessage("Saved the warp as ::warp " + dest.name);
			
			return;
		}
		
		String name = args[0];
		for(int i = 1; i < args.length; i++) name += " " + args[i];
		
		Destination d = destinations.get(name.toLowerCase());
		if(d == null){
			String completed = autocomplete.nearestKey(name.toLowerCase());
			if(completed != null){
				d = destinations.get(completed);
			}
			
			if(d == null){
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
}