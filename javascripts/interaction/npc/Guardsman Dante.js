importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(player, "Greetings.");
	chat(npc, "Hello, adventurer.");
	var opt = option(["Tell me about the Lumbridge Guardsmen.", "What is there to do around here?", "Tell me about Lumbridge.", "What are you guarding?", "Bye."]);

	if (opt == 0){
		chat(npc, "I won't pretend that we're an elite fighting force, but we know how to work with the castle's defences.");
		chat(npc, "That means just a few of us can hold a fairly strong defence, if we ever need to again.");
		chat(player, "Can I join?");
		chat(npc, "No my friend, you're not ready yet.");
		chat(npc, "Talk to me again once you've mastered your skills and I'll reconsider.");
		chat(player, "Thank you, I guess.", SpeechDialogue.SAD);
	}
	else if (opt == 1){
		chat(npc, "If you want to train your creative skills, there are trees to cut, or you could collect leather from the cow fields to the east.");
		chat(player, "I'll check it out! Thank you Guardsman Peale!");
		chat(npc, "My pleasure, my friend.");
	}
	else if (opt == 2){
		chat(npc, "Lumbridge used to be a safe haven where you could find your feet. It is safe again now, but I wonder if we will ever recover what we've lost.");
	}
	else if (opt == 3){
		chat(npc, "We work for the safety of the people and the Duke, and we must be vigilant at all times against potential threats, be they acts of god or goblin invasions.");
		chat(player, "Potential threats?", SpeechDialogue.CONFUSED);
		chat(npc, "Well, we defend our castle against vile and evil creatures; like goblins, giant spiders but lately...");
		chat(player, "What? What happened Guardsman Peale?");
		chat(npc, "Lately we've spotted creatures with equal abilities and forces.");
		chat(npc, "The worst part is that they look just like you and me!", SpeechDialogue.SCARED);
		chat(npc, "We call them Bots and we're confident that they are planning a massive attack.");
		chat(npc, "When that day arrives, can we count on you "+player.getName()+ "?");
		
		var opt = option(["Yes!", "No!"]);
		
		if (opt == 0){
			chat(player, "Yes! I'm ready and armed!", SpeechDialogue.BAD_ASS);
			chat(npc, "All right my friend, we salute you!");
		} 
		else if (opt == 1){
			chat(player, "No! They're not evil; they are too dumb to plan an attack...");
			chat(npc, "You fool!", SpeechDialogue.MEAN_FACE);
		}
	}
	else if (opt == 4){
		chat(player, "Goodbye.");
		chat(npc, "Catcha.");
	}
}