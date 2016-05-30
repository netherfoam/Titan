importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(npc, "Buy me a drrink pleassh.", SpeechDialogue.DRUNK_HAPPY_TIRED);
	chat(npc , "Oh, itsh you. What do you want?", SpeechDialogue.DRUNK_HAPPY_TIRED);
	chat(player, "I succesfully slayed the vampyre! Morgan's village is now free of threat and all is well.");
	chat(npc, "Well, that calls for a drink to celebrate! How 'bout yoush buy me one?", SpeechDialogue.DRUNK_HAPPY_TIRED);
}