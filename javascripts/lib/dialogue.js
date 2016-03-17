/* These are implicitly set later by the JSFiber */
var player;

importClass(org.maxgamer.rs.model.javascript.DialogueUtil);
importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

function chat(speaker, message, emote){
	emote = emote || org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue.CALM_TALK;
	
	DialogueUtil.chat(fiber, player, speaker, message, emote);
}

function option(options, title){
	title = title || "Select an option";
	return DialogueUtil.option(fiber, player, options, title);
}

function think(text, title){
	title = title || "";
	DialogueUtil.think(fiber, player, text, title);
}

function thought(text, title){
	think(text, title);
}

function pick(items, max){
	max = max || 28;
	return DialogueUtil.pick(fiber, player, items, max);
}

function string(question){
	return DialogueUtil.readString(fiber, player, question);
}

function number(question){
	return DialogueUtil.readNumber(fiber, player, question);
}

function vendor(name){
	return DialogueUtil.openVendor(fiber, player, name);
}