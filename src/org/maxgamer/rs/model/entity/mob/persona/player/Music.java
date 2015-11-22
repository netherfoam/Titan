package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author Albert Beaupre
 */
public class Music implements YMLSerializable {

	/**
	 * The maximum amount of music that can be unlocked by a player.
	 */
	public static final int MAX_MUSIC = 762;

	private final boolean[] unlockedTracks; // The flags to check if the player has unlocked a certain track
	private final Player p; // The player who uses this music

	/**
	 * Constructs a new {@code Music} from the specified {@link Player}
	 * {@code p}.
	 * 
	 * @param p
	 *            the player who uses this music
	 */
	public Music(Player p) {
		this.p = p;
		this.unlockedTracks = new boolean[MAX_MUSIC];
	}

	/**
	 * Refreshes the music configuration values.
	 */
	public void refreshMusicConfiguration() {
		int[] MUSIC_CONFIGS = { 20, 21, 22, 23, 24, 25, 298, 311, 346, 414, 464, 598, 662, 721, 906, 1009, 1104, 1136, 1180, 1202, 1381, 1394, 1434 };
		int totalPos = 0, endSpot = 32;
		while ((totalPos * 32) + endSpot < unlockedTracks.length) {
			int config = 0;
			for (int i = 0; i < endSpot; i++)
				config |= unlockedTracks[(totalPos * 32) + i] ? (1 << i) : 0;
			p.getProtocol().sendConfig(MUSIC_CONFIGS[totalPos], config);
			totalPos++;
		}
		endSpot = unlockedTracks.length - (totalPos * 32);
		int config = 0;
		for (int i = 0; i < endSpot; i++)
			config |= unlockedTracks[(totalPos * 32) + i] ? (1 << i) : 0;
		p.getProtocol().sendConfig(MUSIC_CONFIGS[totalPos], config);
		totalPos++;
	}

	/**
	 * Plays a specific song for the specified {@code musicId}.
	 * 
	 * @param musicId the id of the song to be played
	 */
	public void playMusic(int musicId) {
		if (!unlockedTracks[musicId]) {
			p.getCheats().log(1, p.getName() + " attempted to play an unlocked music track.");
			return;
		}
		p.getProtocol().playMusic(100, 50, musicId);
	}

	/**
	 * @param musicId
	 *            the id of the music track to check
	 * @return {@code true} if the music from the specified {@code musicId} is
	 *         unlocked; return false otherwise
	 */
	public boolean hasUnlockedTrack(int musicId) {
		return unlockedTracks[musicId];
	}

	/**
	 * Unlocks the music track for the specified {@code musicId}.
	 * 
	 * @param musicId
	 *            the music track id to unlock
	 */
	public void unlockTrack(int musicId) {
		unlockedTracks[musicId] = true;
		refreshMusicConfiguration();
	}

	@Override
	public ConfigSection serialize() {
		//TODO save
		return null;
	}

	@Override
	public void deserialize(ConfigSection map) {
		//TODO load
	}
}
