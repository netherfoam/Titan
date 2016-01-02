package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

public class PlayerItemOnItemEvent extends PlayerEvent {

	private final int fromButtonId;
	private final int fromItemId;
	private final int fromSlot;
	private final int toButtonId;
	private final int toItemId;
	private final int toSlot;

	public PlayerItemOnItemEvent(Player p, int fromButtonId, int fromItemId, int fromSlot, int toButtonId, int toItemId, int toSlot) {
		super(p);
		this.fromButtonId = fromButtonId;
		this.fromItemId = fromItemId;
		this.fromSlot = fromSlot;
		this.toButtonId = toButtonId;
		this.toItemId = toItemId;
		this.toSlot = toSlot;
	}

	public int getFromButtonId() {
		return fromButtonId;
	}

	public int getFromItemId() {
		return fromItemId;
	}

	public int getFromSlot() {
		return fromSlot;
	}

	public int getToButtonId() {
		return toButtonId;
	}

	public int getToItemId() {
		return toItemId;
	}

	public int getToSlot() {
		return toSlot;
	}

}
