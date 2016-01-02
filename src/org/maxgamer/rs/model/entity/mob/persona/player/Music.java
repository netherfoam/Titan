package org.maxgamer.rs.model.entity.mob.persona.player;

import java.util.Map.Entry;

import org.maxgamer.rs.cache.format.ClientScriptSettings;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author Albert Beaupre
 */
public class Music implements YMLSerializable {

	/**
	 * The maximum amount of music that can be unlocked by a player.
	 */
	public static final int MAX_MUSIC = 962;

	private boolean[] unlockedTracks = new boolean[MAX_MUSIC]; // The flags to
																// check if the
																// player has
																// unlocked a
																// certain track
	private final Player p; // The player who uses this music

	private boolean selectivePlaying; // The music is being played by selection
	private int currentTrackPlaying; // The music id of the current track being
										// played

	/**
	 * Constructs a new {@code Music} from the specified {@link Player}
	 * {@code p}.
	 * 
	 * @param p
	 *            the player who uses this music
	 */
	public Music(Player p) {
		this.p = p;
	}

	/**
	 * Refreshes the music configuration values.
	 */
	public void refreshMusicConfiguration() {
		int[] MUSIC_CONFIGS = { 20, 21, 22, 23, 24, 25, 255, 298, 311, 346, 414, 464, 598, 662,
				721, 906, 1009, 1104, 1136, 1180, 1202, 1381, 1394, 1434, 1596, 1618, 1619, 1620,
				1621, 1622, 1623, 1624, 1625, 1626, 1865, 1864, 2246, 2019, 2430 };
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
	 * @param musicId
	 *            the id of the song to be played
	 */
	public void playMusicTrack(int musicId, boolean selective) {
		if (!unlockedTracks[musicId]) {
			p.getCheats().log(1, p.getName() + " attempted to play an unlocked music track.");
			return;
		}
		setSelectivePlaying(selective);
		currentTrackPlaying = musicId;
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
	public void unlockMusicTrack(int musicId) {
		if (!unlockedTracks[musicId]) {
			String musicName = ClientScriptSettings.getSettings(1345).getStringValue(musicId);
			p.sendMessage("<col=ff0000>You have unlocked a new music track: " + (musicName == null ? "UNKNOWN TRACK (" + musicId + ")" : musicName) + ".");
		}
		unlockedTracks[musicId] = true;
		refreshMusicConfiguration();

		if (!selectivePlaying)
			playMusicTrack(musicId, false);
	}

	@Override
	public ConfigSection serialize() {
		ConfigSection s = new ConfigSection();
		for (int i = 0; i < unlockedTracks.length; i++) {
			s.put("" + i, unlockedTracks[i]);
		}
		return s;
	}

	@Override
	public void deserialize(ConfigSection map) {
		for (Entry<String, Object> e : map.entrySet()) {
			int musicId = Integer.parseInt(e.getKey().trim());
			boolean unlocked = Boolean.parseBoolean("" + e.getValue());
			unlockedTracks[musicId] = unlocked;
		}
		refreshMusicConfiguration();
	}

	public boolean isSelectivePlaying() {
		return selectivePlaying;
	}

	public void setSelectivePlaying(boolean selectivePlaying) {
		this.selectivePlaying = selectivePlaying;
	}

	public int getCurrentTrackPlaying() {
		return currentTrackPlaying;
	}

	public void setCurrentTrackPlaying(int currentTrackPlaying) {
		this.currentTrackPlaying = currentTrackPlaying;
	}
}
