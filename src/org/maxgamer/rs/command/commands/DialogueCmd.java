package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class DialogueCmd implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		//Have a player or NPC speak
		SpeechDialogue speech = new SpeechDialogue(player){
			@Override
			public void onContinue() {
				player.say("Well, that was weird.");
			}
		};
		speech.setText("How much would would a Wood Chuck chuck, if a Wood Chuck could chuck wood?");
		speech.setFace(SpeechDialogue.PLAYER_FACE, player.getName(), SpeechDialogue.BAD_ASS);
		player.getWindow().open(speech);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}