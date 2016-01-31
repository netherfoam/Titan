function talkTo(player, npc){
	chat(player, "Salutations!");
	chat(npc, "Hello, I'm glad to see an adventurer about. There's been an increase in goblins hanging around the are.");
}

function pickpocket(player, npc){
	animate(player, 881);
	var item = ItemStack.create(995, random(10) + 2);
	try{
		player.getInventory().add(item);
		player.getSkills().addExp(SkillType.THIEVING, 10);
	}
	catch(e){
		player.sendMessage("You need more space.");
	}
}