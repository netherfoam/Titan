package org.maxgamer.rs.model.javascript;

import co.paralleluniverse.fibers.Fiber;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.ItemPickerDialogue;
import org.maxgamer.rs.model.interfaces.impl.chat.StringRequestInterface;
import org.maxgamer.rs.model.interfaces.impl.dialogue.ForkDialogue;
import org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue;
import org.maxgamer.rs.model.interfaces.impl.dialogue.ThoughtDialogue;
import org.maxgamer.rs.model.interfaces.impl.primary.VendorInterface;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.vendor.Vendor;
import org.maxgamer.rs.util.Chat;
import org.maxgamer.rs.util.FiberLocal;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Arrays;
import java.util.LinkedList;

public class DialogueUtil {
    private static FiberLocal<Mob> cause = new FiberLocal<>();

    private static <E> E cast(Object[] args, int index, Class<E> type) {
        return cast(args, index, type, null);
    }

    private static <E> E cast(Object[] args, int index, Class<E> type, E fallback) {
        // Safely handle omitted values
        if(index >= args.length) return fallback;

        Object arg = args[index];

        if(arg instanceof ScriptableObject) {
            return (E) Context.jsToJava(arg, type);
        }

        return type.cast(arg);
    }

    /**
     * Splits the given message into sets of messages, with each set containing SpeechDialogue.MAX_LINES lines at most
     * @param message the message
     * @return the set of messages
     */
    private static LinkedList<String[]> split(String message, int maxLines) {
        LinkedList<String[]> sets = new LinkedList<>();

        final String[] lines = Chat.lines(message, 50);

        for(int i = 0; lines.length - i > 0; i += maxLines) {
            String[] set = Arrays.copyOfRange(lines, i, i + Math.min(lines.length - i, maxLines));

            sets.add(set);
        }

        return sets;
    }

    /**
     * Sets the current cause of the dialogue. This is used for convenience, eg. to access the player that started the chat
     * @param m
     */
    public static void setCause(Fiber<?> fiber, Mob m) {
        if(m == null) {
            cause.remove(fiber);
        } else {
            cause.set(fiber, m);
        }
    }

    public static <M extends Mob> M getCause(Class<M> clazz) {
        return clazz.cast(cause.get());
    }

    public static void chat(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        Mob speaker = cast(args, 0, Mob.class);
        String message = cast(args, 1, String.class);
        Integer emote = cast(args, 2, Integer.class, SpeechDialogue.CALM_TALK);

        final LinkedList<String[]> split = split(message, SpeechDialogue.MAX_LINES);
        final Fiber<?> currentFiber = Fiber.currentFiber();

        SpeechDialogue dialogue = new SpeechDialogue(player) {
            @Override
            public void onContinue() {
                if(split.isEmpty()) {
                    currentFiber.unpark();

                    return;
                }

                setLines(split.pop());
                getPlayer().getWindow().open(this);
            }
        };

        if(speaker instanceof NPC) {
            NPC npc = (NPC) speaker;
            dialogue.setFace(npc.getId(), npc.getName(), emote);
        } else if (!(speaker instanceof Persona) || speaker != player) {
            throw new IllegalArgumentException("Invalid speaker given, requested " + speaker + " to talk to " + player);
        }

        dialogue.setLines(split.pop());
        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void option(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        String[] options = cast(args, 0, String[].class);
        String title = cast(args, 1, String.class, "Select an option");

        final JavaScriptCallFiber currentFiber = (JavaScriptCallFiber) Fiber.currentFiber();

        ForkDialogue dialogue = new ForkDialogue(player) {
            @Override
            public void onSelect(int option) {
                currentFiber.resume(option);
            }
        };

        for(String option : options) {
            dialogue.add(option);
        }

        dialogue.setTitle(title);
        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void thought(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        // Alias for think(..) which might make code more readable
        think(cx, thisObj, args, funObj);
    }

    public static void think(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        String message = cast(args, 0, String.class);
        String title = cast(args, 1, String.class);

        final LinkedList<String[]> split = split(message, ThoughtDialogue.MAX_LINES);
        final Fiber<?> currentFiber = Fiber.currentFiber();

        ThoughtDialogue dialogue = new ThoughtDialogue(player) {
            @Override
            public void onContinue() {
                if(split.isEmpty()) {
                    currentFiber.unpark();

                    return;
                }

                setLines(split.pop());
                getPlayer().getWindow().open(this);
            }
        };

        dialogue.setLines(split.pop());
        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void pick(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        int[] ids = cast(args, 0, int[].class);
        int maxAmount = cast(args, 1, Integer.class, 28);
        final JavaScriptCallFiber currentFiber = (JavaScriptCallFiber) Fiber.currentFiber();

        ItemStack[] items = new ItemStack[ids.length];
        for (int i = 0; i < ids.length; i++) {
            items[i] = ItemStack.create(ids[i], 1);
        }

        ItemPickerDialogue dialogue = new ItemPickerDialogue(player, maxAmount) {
            @Override
            public void pick(ItemStack item) {
                currentFiber.resume(item);
            }
        };

        for(ItemStack item : items) {
            dialogue.add(item);
        }

        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void string(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        String question = cast(args, 0, String.class);
        final JavaScriptCallFiber currentFiber = (JavaScriptCallFiber) Fiber.currentFiber();

        StringRequestInterface dialogue = new StringRequestInterface(player, question) {
            @Override
            public void onInput(String value) {
                currentFiber.resume(value);
            }
        };

        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void number(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        String question = cast(args, 0, String.class);
        final JavaScriptCallFiber currentFiber = (JavaScriptCallFiber) Fiber.currentFiber();

        IntRequestInterface dialogue = new IntRequestInterface(player, question) {
            @Override
            public void onInput(long value) {
                currentFiber.resume(value);
            }
        };

        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    public static void vendor(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Player player = getCause(Player.class);

        String shopName = cast(args, 0, String.class);
        Vendor container = Core.getServer().getVendors().get(shopName);

        if(container == null) {
            throw new IllegalArgumentException("No such shop exists with the name '" + shopName + "'");
        }

        final JavaScriptCallFiber currentFiber = (JavaScriptCallFiber) Fiber.currentFiber();

        VendorInterface dialogue = new VendorInterface(player, container) {
            @Override
            public void onClose() {
                super.onClose();

                currentFiber.unpark();
            }
        };

        player.getWindow().open(dialogue);

        throw cx.captureContinuation();
    }

    private DialogueUtil() {
        //Private constructor
    }
}
