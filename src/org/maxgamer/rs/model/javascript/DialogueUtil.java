package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.interfaces.impl.chat.ItemPickerDialogue;
import org.maxgamer.rs.interfaces.impl.chat.StringRequestInterface;
import org.maxgamer.rs.interfaces.impl.dialogue.ForkDialogue;
import org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue;
import org.maxgamer.rs.interfaces.impl.dialogue.ThoughtDialogue;
import org.maxgamer.rs.interfaces.impl.primary.VendorInterface;
import org.maxgamer.rs.lib.Chat;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.vendor.VendorContainer;

public class DialogueUtil {
	public static void chat(final JavaScriptFiber fiber, Player recipient, Mob speaker, final String message, int emote) {
		final String[] lines = Chat.lines(message, 50);
		
		String[] set = new String[Math.min(SpeechDialogue.MAX_LINES, lines.length)];
		for(int i = 0; i < set.length; i++){
			set[i] = lines[i];
		}
		
		final JavaScriptCall call = fiber.context().getCall();
		SpeechDialogue dialogue = new SpeechDialogue(recipient) {
			private int pos = 0;
			
			@Override
			public void onContinue() {
				pos += this.text.length;
				
				if(pos < lines.length) {
					String[] set = new String[Math.min(SpeechDialogue.MAX_LINES, lines.length - pos)];
					for(int i = 0; i < set.length; i++){
						set[i] = lines[pos + i];
					}
					this.setLines(set);
					getPlayer().getWindow().open(this);
				}
				else{
					fiber.unpause(call, null);
				}
			}
		};
		
		dialogue.setLines(set);
		if (speaker instanceof NPC) {
			NPC npc = (NPC) speaker;
			dialogue.setFace(npc.getId(), npc.getName(), emote);
		}
		else if(speaker instanceof Persona == false || speaker != recipient){
			throw new IllegalArgumentException("Invalid speaker given, requested " + speaker + " to talk to " + recipient);
		}
		recipient.getWindow().open(dialogue);
		
		// This will pause our script.
		fiber.pause();
	}
	
	public static void option(final JavaScriptFiber fiber, Player recipient, String[] options, String title) {
		final JavaScriptCall call = fiber.context().getCall();
		
		ForkDialogue fork = new ForkDialogue(recipient) {
			@Override
			public void onSelect(int option) {
				fiber.unpause(call, option);
			}
		};
		
		for (String s : options) {
			fork.add(s);
		}
		fork.setTitle(title);
		
		recipient.getWindow().open(fork);
		fiber.pause();
	}
	
	//TODO: Title isn't used
	public static void think(final JavaScriptFiber fiber, Player recipient, String text, String title) { 
		final JavaScriptCall call = fiber.context().getCall();
		
		final String[] lines = Chat.lines(text, 50);
		
		String[] set = new String[Math.min(ThoughtDialogue.MAX_LINES, lines.length)];
		for(int i = 0; i < set.length; i++){
			set[i] = lines[i];
		}
		
		ThoughtDialogue thought = new ThoughtDialogue(recipient) {
			private int pos = 0;
			
			@Override
			public void onContinue() {
				pos += this.text.length;
				
				if(pos < lines.length) {
					String[] set = new String[Math.min(SpeechDialogue.MAX_LINES, lines.length - pos)];
					for(int i = 0; i < set.length; i++){
						set[i] = lines[pos + i];
					}
					this.setLines(set);
					getPlayer().getWindow().open(this);
				}
				else{
					fiber.unpause(call, null);
				}
			}
		};
		
		thought.setLines(set);
		
		recipient.getWindow().open(thought);
		fiber.pause();
	}
	
	public static void pick(final JavaScriptFiber fiber, Player recipient, int[] ids, int maxAmount) {
		final JavaScriptCall call = fiber.context().getCall();
		
		ItemStack[] items = new ItemStack[ids.length];
		for (int i = 0; i < ids.length; i++) {
			items[i] = ItemStack.create(ids[i], 1);
		}
		
		ItemPickerDialogue picker = new ItemPickerDialogue(recipient, maxAmount) {
			@Override
			public void pick(ItemStack item) {
				fiber.unpause(call, item);
			}
		};
		
		for (ItemStack item : items) {
			picker.add(item);
		}
		
		recipient.getWindow().open(picker);
		fiber.pause();
	}
	
	public static void readString(final JavaScriptFiber fiber, Player recipient, String question) {
		final JavaScriptCall call = fiber.context().getCall();
		
		StringRequestInterface req = new StringRequestInterface(recipient, question) {
			@Override
			public void onInput(String value) {
				fiber.unpause(call, value);
			}
		};
		recipient.getWindow().open(req);
		fiber.pause();
	}
	
	public static void readNumber(final JavaScriptFiber fiber, Player recipient, String question) {
		final JavaScriptCall call = fiber.context().getCall();
		
		IntRequestInterface req = new IntRequestInterface(recipient, question) {
			@Override
			public void onInput(long value) {
				fiber.unpause(call, value);
			}
		};
		recipient.getWindow().open(req);
		fiber.pause();
	}
	
	public static void openVendor(final JavaScriptFiber fiber, Player recipient, VendorContainer container) {
		final JavaScriptCall call = fiber.context().getCall();
		
		recipient.getWindow().open(new VendorInterface(recipient, container){
			@Override
			public void onClose(){
				super.onClose();
				fiber.unpause(call, null);
			}
		});
		fiber.pause();
	}
	
	public static void openVendor(final JavaScriptFiber fiber, Player recipient, String shop) {
		VendorContainer container = Core.getServer().getVendors().get(shop);
		if(container == null){
			throw new IllegalArgumentException("No such shop exists with name '" + shop + "'");
		}
		
		openVendor(fiber, recipient, container);
	}
	
	public static void openVendor(final JavaScriptFiber fiber, Player recipient, int shop) {
		VendorContainer container = Core.getServer().getVendors().get(shop);
		if(container == null){
			throw new IllegalArgumentException("No such shop exists with ID " + shop + "");
		}
		
		openVendor(fiber, recipient, container);
	}
	
	private DialogueUtil() {
		//Private constructor
	}
}
