importClass(org.maxgamer.rs.model.skill.SkillType);
importClass(org.maxgamer.rs.model.item.ItemStack);

function talkTo(player, npc){
	if (player.getSkills().getLevel(SkillType.COOKING, false) < 99) {
		chat(npc, "I'm too busy to talk... Can't you see I'm cooking?");
	} else {
		chat(npc, "Hello, welcome to the Cooking Guild. It's always great to have such an accomplished chef visit us. Say, would you be interested in a Skillcape of Cooking?");
		chat(npc, "They're only available to master chefs.");
		var opt = option(["No thanks.", "Yes please."]);
		if (opt == 0) {
			chat(player, "No thanks.");
			chat(npc, "Okay, come back to me if you change your mind.");
		} else if (opt == 1) {
			chat(player, "Can I buy a Skillcape of Cooking from you?");
			chat(npc, "Of course. It'll cost you 99,000 coins.");
			var opt = option(["Okay.", "No, that's too much!"]);
			if (opt == 0) {
				chat(player, "Okay.");
				if (player.getInventory().contains(ItemStack.create(995, 99000))){
					if (player.getInventory().getFreeSlots() >= 2) {
						player.getInventory().remove(1, ItemStack.create(995, 99000));
						player.getInventory().add(1, ItemStack.create(9801, 1));
						player.getInventory().add(1, ItemStack.create(9803, 1));
					} else {
						player.sendMessage("Not enough space.");
					}
				} else {
					player.sendMessage("Not enough money.");
				}
			} else if (opt == 1) {
				chat(player, "No, that's too much.");
			}
		}
	}
}