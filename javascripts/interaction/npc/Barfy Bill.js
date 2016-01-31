importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(player, "Salutations!");
	chat(npc, "Oh! Hello there.");
	var opt = option(["Who are you?", "Can you teah me about canoeing?"]);
	if (opt == 0) {
		chat(player, "Who are you?");
		chat(npc, "My name is Ex Sea Captain Barfy Bill.");
		chat(player, "Ex sea captain?", SpeechDialogue.CONFUSED);
		chat(npc, "Yeah, I bought a lovely ship and was planning to make a fortune running her as a merchant vessel.", SpeechDialogue.SAD);
		chat(player, "Why are you not still sailing?", SpeechDialogue.CONFUSED);
		chat(npc, "Chronic sea sickness. My first, and only, voyage was spent dry heaving over the rails.", SpeechDialogue.SAD);
		chat(npc, "If I had known about the sea sickness I could have saved myself a lot of money.");
		chat(player, "What are you up to now then?", SpeechDialogue.CONFUSED);
		chat(npc, "Well my ship had a little fire related problem. Fortunately it was well insured.");
		chat(npc, "Anyway, I don't have to work anymore so I've taken to canoeing on the river.");
		chat(npc, "I don't get river sick!", SpeechDialogue.LAUGH_EXCITED);
		chat(npc, "Would you like to know how to make a canoe?", SpeechDialogue.CONFUSED);
		var opt = option(["Yes.", "No."]);
		if (opt == 0) {
			chat(player, "Yes.");
			chat(npc, "It's really quite simple. Just walk down to that tree on the bank and chop it down.");
			chat(npc, "When you have done that you can shape the log further with your hatchet to make a canoe.");
			chat(npc, "Ho! You look like you know which end of a hatchet is which!", SpeechDialogue.LAUGH_EXCITED);
			chat(npc, "You can easily build one of those Wakas. Be careful if you travel into the Wilderness though.");
			chat(npc, "I've heard tell of great evil in that blasted wasteland.", SpeechDialogue.SCARED);
		} else if (opt == 1) {
			chat(player, "No.")
		}
	} else if (opt == 1) {
		chat(player, "Can you teah me about canoeing?");
		chat(npc, "It's really quite simple. Just walk down to that tree on the bank and chop it down.");
		chat(npc, "When you have done that you can shape the log further with your hatchet to make a canoe.");
		chat(npc, "Ho! You look like you know which end of a hatchet is which!", SpeechDialogue.LAUGH_EXCITED);
		chat(npc, "You can easily build one of those Wakas. Be careful if you travel into the Wilderness though.");
		chat(npc, "I've heard tell of great evil in that blasted wasteland.", SpeechDialogue.SCARED);
	}
}