package org.maxgamer.rs.model.javascript.dialogue;

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
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.mozilla.javascript.ContinuationPending;

public class DialogueUtil {
	public static void chat(final JavaScriptFiber fiber, Player recipient, Mob speaker, String message, int emote) {
		final ContinuationPending state = fiber.state();
		SpeechDialogue dialogue = new SpeechDialogue(recipient) {
			@Override
			public void onContinue() {
				try {
					fiber.unpause(state, null);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		dialogue.setText(message);
		if (speaker instanceof NPC) {
			NPC npc = (NPC) speaker;
			dialogue.setFace(npc.getId(), npc.getName(), emote);
		}
		
		recipient.getWindow().open(dialogue);
		
		// This will pause our script.
		throw state;
	}
	
	public static void option(final JavaScriptFiber fiber, Player recipient, String[] options, String title) {
		final ContinuationPending state = fiber.state();
		
		ForkDialogue fork = new ForkDialogue(recipient) {
			@Override
			public void onSelect(int option) {
				try {
					fiber.unpause(state, option);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		
		for (String s : options) {
			fork.add(s);
		}
		fork.setTitle(title);
		
		recipient.getWindow().open(fork);
		throw state;
	}
	
	//TODO: Title isn't used
	public static void think(final JavaScriptFiber fiber, Player recipient, String text, String title) { 
		final ContinuationPending state = fiber.state();
		
		ThoughtDialogue thought = new ThoughtDialogue(recipient) {
			@Override
			public void onContinue() {
				try {
					fiber.unpause(state, null);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		
		thought.setText(text);
		
		recipient.getWindow().open(thought);
		throw state;
	}
	
	public static void pick(final JavaScriptFiber fiber, Player recipient, int[] ids, int maxAmount) {
		final ContinuationPending state = fiber.state();
		
		ItemStack[] items = new ItemStack[ids.length];
		for (int i = 0; i < ids.length; i++) {
			items[i] = ItemStack.create(ids[i], 1);
		}
		
		ItemPickerDialogue picker = new ItemPickerDialogue(recipient, maxAmount) {
			@Override
			public void pick(ItemStack item) {
				try {
					fiber.unpause(state, item);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		
		for (ItemStack item : items) {
			picker.add(item);
		}
		
		recipient.getWindow().open(picker);
		throw state;
	}
	
	public static void readString(final JavaScriptFiber fiber, Player recipient, String question) {
		final ContinuationPending state = fiber.state();
		
		StringRequestInterface req = new StringRequestInterface(recipient, question) {
			@Override
			public void onInput(String value) {
				try {
					fiber.unpause(state, value);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		recipient.getWindow().open(req);
		throw state;
	}
	
	public static void readNumber(final JavaScriptFiber fiber, Player recipient, String question) {
		final ContinuationPending state = fiber.state();
		
		IntRequestInterface req = new IntRequestInterface(recipient, question) {
			@Override
			public void onInput(long value) {
				try {
					fiber.unpause(state, value);
				}
				catch (ContinuationPending e) {
					/*
					 * Occurs when the script calls pause(). Before pause() is
					 * called, another method should copy the Continuation
					 * state, and unpause using that when it is ready. Therefore
					 * we don't handle this, we just silently discard it.
					 */
				}
			}
		};
		recipient.getWindow().open(req);
		throw state;
	}
	
	private DialogueUtil() {
		//Private constructor
	}
}
