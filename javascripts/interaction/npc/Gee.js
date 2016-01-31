/**
 * @author Dirk Jamieson
 * @date 16 Nov 2015
 *
 * An example of a basic NPC script. This code will be run
 * whenever the NPC is spoken to by a player. The file must be
 * placed in jsenginemodule/js/npc/ and must be called npc_name.js
 * or, npc_name-option.js (where option is eg "talk-to"). All files
 * should be lowercase names.
 * 
 * Dialogue functions are available in ../lib/npc.js, which is interpreted
 * prior to this file.
 *
 * npc.js includes the following functions:
 * 		chat(NPC|Player speaker, string message, int emote = SpeechDialogue.CALM) : null;
 *		option([list, of, strings], string title = "") : int (0 to list.lenght - 1);
 *		think(string text, string title = "") : null;
 *		thought(string text, string title = "") : null;
 * 		pick([list, of, item, ids], int max_amount = 28) : ItemStack;
 *		string(string question) : string;
 *		number(string question) : long
 *
 * The following variables are defined:
 * 		fiber : JSFiber
 * 		player: Mob (Usually Player, but assume it is a mob for future safety)
 * 		npc   : The NPC being spoken to
 */

function talkTo(player, npc){
	var opt = option(["How are you?", "Your shoelace is untied", "Can I buy your Staff?"]);
	if(opt == 0){
		chat(npc, "Seems like a fine day to me");
	}
	else if(opt == 1){
		chat(npc, "No it isn't!");
	}
	else if(opt == 2){
		chat(npc, "It's a powerful staff!");
		chat(player, "Hold on! That's no staff!");
		chat(npc, "Fine, the truth is I'm saving up to buy a powerful staff from Zaff in Varrock");
	}
}