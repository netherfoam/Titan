chat(npc, "Stupendous! Look what Foreman George and his team of builders have done! This house is perfect!");
chat(npc, "I just need to get a carpet and a few decorations and those months of sleeping rough in the ruins of my house will become a distant memory..");
chat(npc, "Now, how can I help you?");
var opt = option(["Tell me about the Task System.", "I need a quest.", "Nothing."]);
if (opt == 0){
	chat(player, "Tell me about the Task System.");
	chat(npc, "I'm sorry pal, the Task System is currently not available. There is nothing to tell you about...");
	chat(npc, "As soon as the development team gives me more details I'll inform you.");
	chat(player, "Thank you, Explorer Jack.");
}
else if (opt == 1){
	chat(player, "I've been told you have a quest. Is that true?");
	chat(npc, "I'm not sure who told you that, but it's a lie!");
	chat(npc, "See you around, bud. I have work to do.");
	chat(player, "Goodbye.");
}
else if (opt == 2){
	chat(player, "Nothing.");
	chat(npc, "Adventurers these days...");
}
