package org.maxgamer.rs.model.minigame;

import java.util.Collection;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.util.Log;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.structure.ArrayUtility;

/**
 * This class handles a {@code Collection} of queued players for a {@code MiniGame}.
 * 
 * @author Albert Beaupre
 */
public abstract class MiniGameQueue extends Tickable {

	private MiniGame[] gamesRunning = new MiniGame[0]; // An array of minigames currently running

	/**
	 * Returns a {@code Collection} of {@code Persona} that is queued in this {@code MinigameQueue}.
	 * 
	 * @return a list of the queued players
	 */
	public abstract Collection<Persona> getPlayers();

	/**
	 * Returns true if this {@code MinigameQueue} is ready to start a {@code MiniGame}.
	 * 
	 * @return true if the queue is ready
	 */
	public abstract boolean isReady();

	/**
	 * Resets this {@code MiniGameQueue}.
	 */
	public abstract void resetQueue();

	/**
	 * Initiates a {@code MiniGame} and returns the {@code MiniGame} initiated.
	 * 
	 * @return the minigame initiated
	 */
	public abstract MiniGame initiateMiniGame();

	/**
	 * This method is called every 600 milliseconds.
	 */
	public abstract void tickQueue();

	@Override
	public final void tick() {
		MiniGame game = null;
		for (int i = 0; i < gamesRunning.length; i++) {
			MiniGame minigame = gamesRunning[i];
			if (minigame == null || !!minigame.isRunning() || minigame.isTerminated()) {
				gamesRunning = ArrayUtility.removeIndex(gamesRunning, i);
				continue;
			}
		}
		try {
			if (isReady()) {
				game = initiateMiniGame();
				Collection<Persona> players = getPlayers();
				for (Persona p : players) {
					if (!game.isPlaying(p)) {
						game.addPlayer(p);
					}
				}
				players.clear();
				gamesRunning = ArrayUtility.addElement(gamesRunning, game);
				Core.getServer().getEvents().register(game);
				game.start();
				resetQueue();
				if (!isQueued())
					queue(1);
				return;
			}
			tickQueue();
			if (!isQueued())
				queue(1);
		} catch (Exception e) {
			if (game != null) {
				game.terminate();
				Log.info(game.getClass().getName() + " was terminated due to exception.");
			}
			e.printStackTrace();
		}
	}
}
