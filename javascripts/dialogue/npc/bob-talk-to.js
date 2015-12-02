var opt = option(["I'd like to trade.", "Can you repair my items for me?", "Goodbye."]);
if(opt == 0){
	chat(player, "I'd like to trade.");
	chat(npc, "Great! I buy and sell pickaxes and hatchets. There are plenty to choose from, and I've some free samples too. Take your pick...or hatchet.");
	//TODO: Open shop
}
else if(opt == 1){
	chat(player, "Can you repair my items for me?");
	chat(npc, "Of course I can, though the materials may cost you. Just hand me the item and I'll have a look.");
	//TODO: Item repair
}
else if(opt == 2){
	chat(player, "Goodbye.");
}