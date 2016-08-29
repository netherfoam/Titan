package org.maxgamer.rs.model.entity.mob.persona.player;

import java.util.ArrayList;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.util.Log;
import org.maxgamer.rs.network.Client;

/**
 * @author netherfoam
 */
public class CheatLog {
	private Client c;
	private ArrayList<String> violations = new ArrayList<String>();
	private int severity = 0;
	
	private long lastTick;
	
	public CheatLog(Client client) {
		this.c = client;
		this.lastTick = Core.getServer().getTicker().getTicks();
	}
	
	/**
	 * Logs the given cheat against this user with the given severity. It is up
	 * to the devleoper to correct, the cheat, this is just a logging system
	 * which will help remove the player if there are repeat occurances. The
	 * severity of cheats depletes over time.
	 * @param severity the cheat severity
	 * @param message the message for the log.
	 */
	public void log(int severity, String message) {
		violations.add(message);
		this.severity += severity;
		
		Log.warning("Cheat Warning for " + c + ": " + message + ", current severity: " + getSeverity());
	}
	
	/**
	 * The reasons that the player has received warnings. These are a
	 * description, and are printed when they are generated. They are stored
	 * here if a developer needs to access the messages. Warnings are not
	 * removed from this list, desite the fact the severity rating decrements
	 * slowly. This is a shallow copy, and modifying this list will result in
	 * modifying the internal list.
	 * @return the list of messages, possibly empty if no warnings are removed.
	 */
	public ArrayList<String> getMessages() {
		return violations;
	}
	
	public Client getClient() {
		return c;
	}
	
	/**
	 * Fetches the current severity of this player's cheats, if any. Zero
	 * represents no cheat log. 20 is minor, 100 is excessive.
	 * @return the severity.
	 */
	public int getSeverity() {
		//We want to vary this depending on how many ticks have passed
		long ticks = Core.getServer().getTicks();
		int delta = (int) (ticks - this.lastTick);
		
		//Every 100 ticks (60 seconds) the player's hack rating decreases by 1.
		this.severity = this.severity - (delta / 100);
		//Only decrement every 100 ticks.
		this.lastTick = this.lastTick + (delta - (delta % 100));
		
		if (severity < 0) severity = 0;
		
		return severity;
	}
}