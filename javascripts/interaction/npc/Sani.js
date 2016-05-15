function talkTo(player, npc){
	chat(player, "Hello.");
	chat(npc, "Could you be kind and do me a favour?");
	chat(npc, "Bring me 8 bronze bars and I will reward you!");

	if(player.getInventory().contains(ItemStack.create(2349, 8))){
		if(option(["I have them here!", "No thanks"]) == 0) {
			chat(player, "I have them here!");
			chat(npc, "Thank you! Here's your reward!");

			try{
				player.getInventory().remove(ItemStack.create(2349, 8));
			}
			catch(e) {
				e.printStackTrace();
				return;
			}

			player.getInventory().add(ItemStack.create(1019, 1));
			player.getSkills().addExp(SkillType.SMITHING, 250);
		}
	} else {
		chat(player, "I don't have any, sorry!");
	}
}