package org.maxgamer.rs.command.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class Organise implements PlayerCommand {
	
	@Override
	public void execute(final Player p, String[] args) throws IOException {
		HashMap<String, ArrayList<ItemStack>> words = new HashMap<String, ArrayList<ItemStack>>();
		
		Container source = p.getBank();
		
		int checksum = 0;
		
		for(ItemStack item : source){
			if(item == null) continue;
			checksum += (item.getAmount());
			
			String[] parts = item.getName().toLowerCase().split(" ");
			for(String part : parts){
				if(part.equals("of")) continue; // Unrelated
				if(part.contains("(") && part.contains(")")) continue; // Quantifier
				
				ArrayList<ItemStack> group = words.get(part);
				if(group == null){
					group = new ArrayList<ItemStack>();
					words.put(part, group);
				}
				group.add(item);
			}
		}
		
		ContainerState before = source.getState();
		ContainerState after = source.getState();
		after.clear();
		
		while(before.isEmpty() == false){
			String name = mostCommon(words);
			if(name == null){
				throw new RuntimeException("Bad logic, there's items left but there's nothing to sort left?");
			}
			
			for(ItemStack item : words.remove(name)){
				try{
					before.remove(item);
				}
				catch(ContainerException e){
					// We've already sorted that item.
					continue;
				}
				
				// Should never throw a ContainerException.
				after.add(item);
			}
		}
		
		after.apply();
		
		for(ItemStack item : after){
			if(item == null) continue;
			checksum -= (item.getAmount());
		}
		if(checksum != 0){
			throw new IllegalArgumentException("Failed, checksum was different, missing " + checksum + " items!");
		}
	}
	
	private String mostCommon(HashMap<String, ArrayList<ItemStack>> words){
		int max = 0;
		String best = null;
		
		for(Entry<String, ArrayList<ItemStack>> entry : words.entrySet()){
			int count = 0;
			for(ItemStack item : entry.getValue()){
				count += item.getAmount();
			}
			
			if(count > max){
				max = count;
				best = entry.getKey();
			}
		}
		
		return best;
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}