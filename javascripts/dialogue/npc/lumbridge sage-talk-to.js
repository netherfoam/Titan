chat(npc, "Greetings, adventurer. How may I help you?");
var opt = option(["Who are you?", "Tell me about the town of Lumbridge.", "Goodbye."]);
if(opt == 0){
	chat(player, "Who are you?");
	chat(npc, "I am Phileas, the Lumbridge Sage. In times past people came from all around to ask me for advice.");
	chat(npc, "My renown seems to have diminished somewhat in recent years, though. Can I help you with anything?");
	chat(player, "I'm fine for now, thanks.");
	chat(npc, "Good adventuring, traveller.");
}
else if(opt == 1){
	chat(player, "Tell me about the town of Lumbridge.");
	chat(npc, "Lumbridge is one of the older towns in the human-controlled kingdoms.");
	chat(npc, "It was founded over two hundred years ago towards the end of the Fourth Age. It's called Lumbridge because of this bridge built over the River Lum.");
	chat(npc, "The town is governed by Duke Horacio who is a good friend of our monarch, King Roald of Misthalin.");
	chat(npc, "Recently, however, there have been great changes due to the Battle of Lumbridge.");
	chat(player, "What about the battle?");
	chat(npc, "Well, the battle rages even now, on the far side of the castle. Saradomin and Zamorak are locked in battle, neither able to gain the upper hand.");
	chat(npc, "Where once was forest, there is a giant crater,\nin which soldiers and creatures fight to the death.");
	chat(npc, "And the city of Lumbridge has seen the ill-effects already! The castle walls themselves have fallen!");
	chat(player, "Is there anything I can do?");
	chat(npc, "You could join the battle if you wish. Both sides are seeking help from any individual who is willing, and not just for fighting -");
	chat(npc, "they are seeking something known as divine tears.");
	chat(npc, "You can join saradomin by going to the camp at the north of the battlefield, and Zamorak by going to the south.");
	chat(player, "Sounds like I should help, thanks.");
}
else if(opt == 2){
	chat(player, "Goodbye.");
}