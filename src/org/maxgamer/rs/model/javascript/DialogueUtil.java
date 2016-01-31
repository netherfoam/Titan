package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.interfaces.impl.chat.ItemPickerDialogue;
import org.maxgamer.rs.interfaces.impl.chat.StringRequestInterface;
import org.maxgamer.rs.interfaces.impl.dialogue.ForkDialogue;
import org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue;
import org.maxgamer.rs.interfaces.impl.dialogue.ThoughtDialogue;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

public class DialogueUtil {
	public static void chat(final JavaScriptFiber fiber, Player recipient, Mob speaker, String message, int emote) {
		final JavaScriptCall call = fiber.context().getCall();
		SpeechDialogue dialogue = new SpeechDialogue(recipient) {
			@Override
			public void onContinue() {
				fiber.unpause(call, null);
			}
		};
		dialogue.setText(message);
		if (speaker instanceof NPC) {
			NPC npc = (NPC) speaker;
			dialogue.setFace(npc.getId(), npc.getName(), emote);
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
		
		ThoughtDialogue thought = new ThoughtDialogue(recipient) {
			@Override
			public void onContinue() {
				fiber.unpause(call, null);
			}
		};
		
		thought.setText(text);
		
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
	
	private DialogueUtil() {
		//Private constructor
	}
}
