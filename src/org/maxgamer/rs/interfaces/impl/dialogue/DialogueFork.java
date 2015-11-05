package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class DialogueFork extends DialogueInterface {
	private String[] options = new String[5];
	
	public DialogueFork(Player p) {
		super(p, 0);
	}
	
	public void set(int option, String text){
		this.options[option] = text;
		
		//Calculate the correct interface ID for the given dialogue fork
		int i = 0;
		
		for(int n = 0; n < options.length; n++){
			if(options[n] == null) continue;
			setString(2 + i, options[n]);
			i++;
		}
		setChildId(225 + i * 2);
	}
	
	@Override
	public void onOpen(){
		int size = 0;
		for(String s : options){
			if(s == null) continue;
			size++;
		}
		
		if (size < 2 || size > 5) {
			throw new IllegalArgumentException("Options length must be between 2 and 5 inclusive. Given " + size + " options.");
		}
		
		int i = 0;
		
		for(int n = 0; n < options.length; n++){
			if(options[n] == null) continue;
			setString(2 + i, options[n]);
			i++;
		}
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public final void onClick(int option, int buttonId, int slotId, int itemId) {
		getPlayer().getWindow().close(this);
		
		int position = buttonId - 2;
		for(int i = 0; position >= i; i++){
			if(options[i] == null) position++;
		}
		
		
		onSelect(position); //TODO: Correct this to a number between 0-4
	}
	
	public final void onSelect(int option){
		System.out.println("Clicked option " + options[option]);
	}
}