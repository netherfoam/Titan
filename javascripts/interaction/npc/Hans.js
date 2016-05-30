importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(npc, "Hello. What are you doing here?");
	var opt = option(["I'm looking for whoever is in charge of this place.", "I have come to kill everyone in this castle!", "I don't know. I'm lost. Where am I?", "Can you tell me how long I've been here?", "Nothing."]);
	if (opt == 0) {
		chat(player, "I'm looking for whoever is in charge of this place.");
		chat(npc, "Who, the Duke? He's in his study, on the first floor.");
		
	}
	else if (opt == 1){
		chat(player, "I have come to kill everyone in this castle!");
		chat(npc, "Help! Help!", SpeechDialogue.SCARED);
		//TODO: Force talk for Hans saying 'Help! Help!'
	}
	else if (opt == 2){
		chat(player, "I don't know. I'm lost. Where am I?", SpeechDialogue.CONFUSED);
		chat(npc, "You are in Lumbridge Castle.");
	}
	else if (opt == 3){
		chat(player, "Can you tell me how long I've been here?");
		chat(npc, "Ahh, I see all the newcomers arriving in Lumbridge, fresh-faced and eager for adventure. I remember you...");
		chat(npc, "You've spent (hours and days) in the world since you arrived (days) days ago.");
		//TODO: Add play time or creation date details. 
	}
	else if (opt == 4){
		chat(player, "Nothing.");
	}
}