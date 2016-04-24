package org.maxgamer.rs.interfaces.impl.dialogue;

import java.util.Arrays;

import org.maxgamer.rs.lib.Chat;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * Class represents a 'thought' dialogue - when a message appears to a player in the chat box
 * area and requires 'Click to Continue', but the interface contains no speaker entity.
 * 
 * @author Dirk Jamieson / Netherfoam
 */
public abstract class ThoughtDialogue extends Dialogue {
	public static final int MAX_LINES = 4;
	
	protected String[] text;
	
	public ThoughtDialogue(Player p) {
		super(p);
	}
	
	/**
	 * Sets the child ID according to the setup of the dialogue
	 */
	protected void setChildId(){
		this.setChildId(209 + text.length);
	}
	
	/**
	 * Sets the text of this dialogue to the given paragraph.  This will raise an Exception if the text
	 * is too long.  Text will be automatically split on words at 50 characters or at newline characters.
	 * @param text the text
	 */
	public void setText(String text){
		//Very primitive line splitting here.
		String[] lines = Chat.lines(text, 50);
		this.setLines(lines);
	}
	
	/**
	 * Gives fine grain control over the lines that will appear on this dialogue.  
	 * @param lines the lines of this dialogue
	 * @throws IllegalArgumentException if 1 < lines.length or lines.length > 5
	 */
	public void setLines(String... lines){
		if(lines.length < 1 || lines.length > MAX_LINES){
			throw new IllegalArgumentException("Given " + lines.length + "lines! Text would be \n" + Arrays.toString(lines) + "\nBut must display 1-4 lines!");
		}
		
		for(int i = 0; i < lines.length; i++){
			if(lines[i] == null){
				throw new NullPointerException("Line at pos " + i + " is null!");
			}
		}
		
		this.text = lines;
		
		//Raises an exception on failure.
		setChildId();
	}
	
	@Override
	public void onOpen(){
		for(int i = 0; i < text.length; i++){
			this.setString(i + 1, this.text[i]);
		}
	}

	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		getPlayer().getWindow().close(this);
		onContinue();
	}
	
	/**
	 * Method that can be overridden to handle when the user clicks 'Continue'
	 */
	public abstract void onContinue();
}
