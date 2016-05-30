importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(player, "Howdy.");
	chat(npc, "Ha Ha! Hello!", SpeechDialogue.DRUNK_HAPPY_TIRED);
	var opt = option(["Who are you?", "Can you teach me about canoeing?"]);
	if (opt == 0) {
		chat(player, "Who are you?");
		chat(npc, "I'm Sigurd the Great and Brainy.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(player, "Why do they call you the Great and Brainy?");
		chat(npc, "Because I invented the Log Canoe.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(player, "Log Canoe?");
		chat(npc, "Yeash! Me and my cousins were having a great party by the river when we decided to have a game of 'Smack The Tree'.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(player, "Smack the Tree?");
		chat(npc, "It's a game where you take it in turnsh shmacking a tree. First one to uproot the tree winsh!", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "Anyway, I won the game with a flying tackle. The tree came loose and down the river bank I went, still holding the tree.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "I woke up a few hours later and found myself several miles down river. And thatsh how I invented the log canoe!");
		chat(player, "So you invented the 'Log Canoe' by falling into the river hugging a tree?", SpeechDialogue.LAUGH_EXCITED);
		chat(npc, "Well I refined the design from the original you know!", SpeechDialogue.MEAN_FACE);
		chat(npc, "I cut all the branches off to make it more comfortable. I could tell you how to if you like?", SpeechDialogue.DRUNK_HAPPY_TIRED);
		var opt = option(["Yes.", "No."]);
		if (opt == 0) {
			chat(player, "Yes.");
			chat(npc, "It's really quite simple. Just walk down to that tree on the bank and chop it down.", SpeechDialogue.DRUNK_HAPPY_TIRED);
			chat(npc, "Then take your hatchet to it and shape it how you like!", SpeechDialogue.DRUNK_HAPPY_TIRED);
			chat(npc, "You look like you know your way around a tree, you can make a Waka canoe.", SpeechDialogue.DRUNK_HAPPY_TIRED);
			chat(player, "What's a Waka?");
			chat(npc, "I've only ever seen Hari using them. People say he's found a way to canoe the river underground and into the Wilderness.", SpeechDialogue.DRUNK_HAPPY_TIRED);
			chat(npc, "Hari hangs around up near Edgeville.", SpeechDialogue.DRUNK_HAPPY_TIRED);
			chat(npc, "He's a nice bloke.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		} else if (opt == 1) {
			chat(player, "No thanks.");
		}
	} else if (opt == 1) {
		chat(player, "Can you teach me about canoeing?");
		chat(npc, "It's really quite simple. Just walk down to that tree on the bank and chop it down.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "Then take your hatchet to it and shape it how you like!", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "You look like you know your way around a tree, you can make a Waka canoe.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(player, "What's a Waka?");
		chat(npc, "I've only ever seen Hari using them. People say he's found a way to canoe the river underground and into the Wilderness.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "Hari hangs around up near Edgeville.", SpeechDialogue.DRUNK_HAPPY_TIRED);
		chat(npc, "He's a nice bloke.", SpeechDialogue.DRUNK_HAPPY_TIRED);
	}
}