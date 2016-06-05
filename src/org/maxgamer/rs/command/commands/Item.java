package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.repository.ItemTypeRepository;
import org.maxgamer.rs.structure.TrieSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author netherfoam
 */
public class Item implements PlayerCommand {
	private TrieSet names = new TrieSet();
	private HashMap<String, Integer> items = new HashMap<String, Integer>(20000);
	private boolean ready = false;

	public Item() {
		Map<Integer, String> data = Core.getWorldDatabase().getRepository(ItemTypeRepository.class).findNames();

		for(Map.Entry<Integer, String> entry : data.entrySet()){
			String name = entry.getValue();
			if (name.equals("null")) {
				continue;
			}
			name = name.toLowerCase().replaceAll("[^0-9A-Za-z]", "");

			int id = entry.getKey();

			names.add(name);
			items.put(name, id);
		}
		items.put("coins", 995); // Fix for duplicate items called coins

		synchronized (Item.this) {
			ready = true;
		}
	}

	@Override
	public void execute(Player sender, String[] args) {
		synchronized (this) {
			if (ready == false) {
				sender.sendMessage("Command not fully loaded yet.");
				return;
			}
		}

		if (args.length <= 0) {
			return;
		}

		int amount = 1;

		if (args.length >= 2) {
			try {
				int multiplier = 1;
				args[1] = args[1].toLowerCase();
				if (args[1].endsWith("k")) {
					args[1] = args[1].substring(0, args[1].length() - 1);
					multiplier = 1000;
				}
				if (args[1].endsWith("m")) {
					args[1] = args[1].substring(0, args[1].length() - 1);
					multiplier = 1000000;
				}
				if (args[1].endsWith("b")) {
					args[1] = args[1].substring(0, args[1].length() - 1);
					multiplier = 1000000000;
				}
				amount = Integer.parseInt(args[1]) * multiplier;
			} catch (NumberFormatException e) {
				sender.sendMessage("Invalid amount: " + args[1]);
				return;
			}
		}

		ItemStack stack;
		try {
			stack = ItemStack.create(Integer.parseInt(args[0]), amount);
		} catch (NumberFormatException e) {
			if (args[0].endsWith("?")) {
				int i = 0;

				HashSet<String> matches = names.matches(args[0].substring(0, args[0].length() - 1));
				if (matches.isEmpty()) {
					sender.sendMessage("No matches for: " + args[0]);
					return;
				}
				sender.sendMessage("Matches: --->");
				for (String s : matches) {
					sender.sendMessage(++i + ". " + s);
				}

				return;
			} else if (args[0].endsWith("*")) {
				String partial = args[0].substring(0, args[0].length() - 1); // Strip * from the end

				for (String s : names.matches(partial)) {
					String[] strs = args.clone(); // Copies all args directly
					strs[0] = s; // The item name match.
					execute(sender, strs);
				}
				return;
			}

			args[0] = names.nearestKey(args[0].toLowerCase());
			if (args[0] == null) {
				sender.sendMessage("Item not found.");
				return;
			}

			int id = items.get(args[0]);

			stack = ItemStack.create(id, amount);
		}

		if (args.length > 2 && args[2].equalsIgnoreCase("noted")) {
			stack = stack.getNoted();
		}

		try {
			sender.getInventory().add(stack);
			sender.sendMessage("Received " + stack.getDefinition().getName() + "(" + stack.getId() + ") " + " x" + amount);
		} catch (ContainerException e) {
			sender.sendMessage("Not enough room.");
		}
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}

}