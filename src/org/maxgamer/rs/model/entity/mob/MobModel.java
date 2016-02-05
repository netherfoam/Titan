package org.maxgamer.rs.model.entity.mob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.maxgamer.rs.io.OutputStreamWrapper;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * Represents the model currently used for a mob. This is an abstract class,
 * which is implemetned by PersonaModel and NPCModel
 * 
 * @author netherfoam
 */
public abstract class MobModel implements YMLSerializable {
	/**
	 * True if this model has been modified and needs to be resent to all
	 * interested players
	 */
	private boolean	changed				= true;

	/**
	 * True if this model is female, false if it is male
	 */
	private boolean	female				= false;

	/**
	 * True if this model is to display PKing skull, false if not
	 */
	private boolean	skulled				= false;

	/**
	 * The prayer icon this model is to display, -1 for none
	 */
	private byte	prayerIcon			= -1;

	/**
	 * True if this mob is to display the combat colour - eg. Green for lower
	 * level, red for higher level
	 */
	private boolean	combatColour		= true;

	/**
	 * The name of this mob, I don't believe this works for NPC's but it is sent
	 * for both anyway
	 */
	private String	name				= "Unknown";

	/**
	 * The combat level for this mob, I don't believe this works for NPC's but
	 * it is sent for both anyway This is an unsigned byte.
	 */
	private byte	combatLevel			= 3;

	/**
	 * The title ID for this mob, this is retrieved from the cache
	 */
	private int		title				= 0;

	private int		renderAnimationId	= 1426;

	/**
	 * Fetches the maximum colour number for the given slot, where the given
	 * slot is a value from 0-4 representing the piece of character design that
	 * is being modified. Values which are out of bounds will likely cause the
	 * character to become partially invisible. This method is for setting the
	 * colour of things such as beards, skin, torso and legs
	 * 
	 * @param index the id of the look to edit (eg beard, torso, bottoms, chest)
	 *        0-4
	 * @return the maximum value (Eg, valid values are 0-maximum inclusive)
	 */
	public static final int getMaxColor(int index) {
		switch (index) {
		case 0:
			return 25;
		case 1:
			return 29;
		case 2: // bottom
			return 29;
		case 3:
			return 5;
		case 4:
			return 5;
		}

		throw new IllegalArgumentException();
	}

	/*
	 * Taken from
	 * http://www.rune-server.org/runescape-development/rs-503-client-
	 * server/configuration
	 * /295398-all-508-graphics-interfaces-emotes-items-ids-more.html hair:
	 * 1:white 2:grey 3:black 4:orange 5:blonde 6.light brown 7.brown 8.turqoise
	 * 9.green 10.ORANGE 11.Purple 12.Black 13.grey 14.faded yellow, dark white
	 * 15.peach 16.cyan 17.dark blue 18.faint purple 19.strawberry 20.red(dark)
	 * 21.mint green 22.dark green 23.indigo 24.violet 25.dark brown
	 * 
	 * torso: 1:dark grey 2:Redish color 3:Blue 4:Tan 5:Whitish color 6.red
	 * 7.dark blue 8.light green 9.yellow 10.purple 11.orange 12.pink 13.moss
	 * green 14.turqoise 15.green 16.black 17.gray 18.papyrus 19.orange 20.cyan
	 * 21.blue 22.peach 23.light red 24.burgundy 25.mint green 26.dark green
	 * 27.purple 28.light purple 29.light brown
	 * 
	 * bottom: 0:green 1:khaki 2:dark grey 3:redish color (same as torso) 4:cool
	 * blue 5:yellow 6.light grey 7.reddish 8.blue 9.green 10.yellow 11.indigo
	 * 12.sepia 13.pink 14.moss green 15.turqoise 16.black 17.light grey
	 * 18.papyrus 19.orange 20.cyan 21.blue 22.peach 23.light red 24.burgundy
	 * 25.mint green 26.dark green 27.purple 28.light purple 29.light brown
	 * 
	 * 
	 * boots: 1:kinda grenish 2:light brown/dark tan 3:black 4:brown
	 * 5:grey/silver
	 * 
	 * skin color: gets darker the bigger the number
	 */

	/**
	 * The colours for a player
	 */
	private byte[]	colour	= new byte[] { 3, 16, 16, 11, 14, };

	/**
	 * A cached version of this mobs model in byte form. If the changed flag is
	 * set, this data will be scrapped and regenerated. Otherwise, for example,
	 * when a new player appears on screen for another, the second player will
	 * receive this cached data as an update instead.
	 */
	private byte[]	cache;

	public int		red, green, blue, ambient, intensity;
	public boolean	applyCustom;

	/**
	 * Returns the cached update data
	 * 
	 * @return
	 */
	public byte[] getUpdateData() {
		if (hasChanged() || this.cache == null) {
			// Do the update.
			ByteArrayOutputStream data = new ByteArrayOutputStream(96);
			OutputStreamWrapper out = new OutputStreamWrapper(data);
			try {
				int hash = isMale() ? 0 : 1;
				// 0x2 is "hasDisplayName"
				//Remaining pieces are 3 bits "size" and some other 3 bit value
				if (isCombatColoured())
					hash |= 0x4;
				out.writeByte(hash);
				out.writeByte(title); // Titles 0-4 (Mob armies related)
				out.writeByte(isSkulled() ? 0 : -1);
				out.writeByte(getPrayerIcon());
				out.writeByte(0); // 0 = visible, 1 = invisible.

				appendUpdate(out);

				if (applyCustom) {
					out.writeByte(1);
					out.writeByte(red);
					out.writeByte(green);
					out.writeByte(blue);
					out.writeByte(intensity);
					out.writeByte(ambient);
				} else
					out.writeByte(0);

				byte[] colour = getColour();
				for (int i = 0; i < 5; i++) {
					out.writeByte(colour[i]);
				}

				// Render ID. This varies for players and mobs,
				// and can be found in the cache. The render ID
				// varies depending on which weapon the player
				// is currently wielding.
				out.writeShort(renderAnimationId); // TODO mob render animation

				out.write(getName());
				out.writeByte(getCombatLevel());

				// if(!combatColoured){
				out.writeShort(0); // Unknown
				out.writeShort(0); // Unknown
				// }else{
				// out.writeShort(?);

				this.cache = data.toByteArray();
			} catch (IOException e) {
				// Can't happen with a ByteArray stream
				e.printStackTrace();
			}
		}
		return cache;
	}

	/**
	 * The title ID this mob has currently
	 * 
	 * @return the title ID this mob has currently
	 */
	public int getTitle() {
		return title;
	}

	/**
	 * Sets the name of this MobModel
	 */
	public void setName(String name) {
		this.name = name;
		this.setChanged(true);
	}

	/**
	 * Toggles combat colouring on this model's right click menu.
	 * @param color true for color (green-yellow-red) or false for white
	 */
	public void setCombatColoured(boolean color) {
		this.combatColour = color;
		this.setChanged(true);
	}

	/**
	 * Toggles the skull above this model's head
	 * @param skulled true to enable the skull
	 */
	public void setSkulled(boolean skulled) {
		this.skulled = skulled;
		this.setChanged(true);
	}

	/**
	 * Sets the title for this mob. No change is made if the given title is the
	 * same as the current title.
	 * 
	 * @param title the title ID for this mob
	 */
	public void setTitle(int title) {
		if (title != this.title) {
			this.changed = true;
			this.title = title;
		}
	}

	/**
	 * Fetches the update data from this model.
	 * 
	 * @return
	 */
	protected abstract void appendUpdate(OutputStreamWrapper data) throws IOException;

	/**
	 * Returns true if this mob is male
	 * 
	 * @return true if this mob is male
	 */
	public boolean isMale() {
		return !female;
	}

	/**
	 * Returns true if this mob is female
	 * 
	 * @return true if this mob is female
	 */
	public boolean isFemale() {
		return female;
	}

	/**
	 * The combat level currently displayed by this model
	 * 
	 * @return The combat level currently displayed by this model
	 */
	public int getCombatLevel() {
		return combatLevel & 0xFF;
	}

	/**
	 * The name currently displayed by this model
	 * 
	 * @return The name currently displayed by this model
	 */
	public String getName() {
		return name;
	}

	/**
	 * True if this model's combat level is coloured according to level
	 * difference (Eg green for lower level, red for higher)
	 * 
	 * @return True if this model's combat level is coloured according to level
	 *         difference
	 */
	public boolean isCombatColoured() {
		return combatColour;
	}

	/**
	 * The colours for this model. This is not a copy, thi sis the actual colour
	 * array. Be wary
	 * 
	 * @return The colours for this model. This is not a copy, thi sis the
	 *         actual colour array. Be wary
	 */
	public byte[] getColour() {
		return colour; // TODO: Bad design, make individual getters and setters for colours without exposing array
	}

	/**
	 * Returns true if this model is skulled
	 * 
	 * @return true if this model is skulled
	 */
	public boolean isSkulled() {
		return skulled;
	}

	/**
	 * Returns the prayer icon currently used for this mob
	 * 
	 * @return the prayer icon currently used for this mob
	 */
	public byte getPrayerIcon() {
		return prayerIcon;
	}

	/**
	 * Sets the prayer icon for this mob
	 * 
	 * @param value the new prayer icon
	 */
	public void setPrayerIcon(int value) {
		this.prayerIcon = (byte) value;
		this.changed = true;
	}

	/**
	 * Sets the changed value. If this is set to true, on the next mask update,
	 * this model will be sent to all nearby players
	 * 
	 * @param changed true if this model needs to be sent to nearby players,
	 *        false otherwise
	 */
	public void setChanged(boolean changed) {
		if (changed && this.changed == false) {
			// We are becoming changed, we weren't before.
			this.cache = null;
		}
		this.changed = changed;
	}

	/**
	 * Sets the displayed combat level on this mob, may only work for Personas
	 * and not NPC's
	 * 
	 * @param lev the displayed combat level on this mob
	 */
	public void setCombatLevel(int lev) {
		if (lev != (this.combatLevel & 0xFF)) {
			this.combatLevel = (byte) lev;
			this.changed = true;
		}
	}

	/**
	 * Returns true if this model is pending an update to nearby players
	 * 
	 * @return true if this model is pending an update to nearby players
	 */
	public boolean hasChanged() {
		return this.changed;
	}

	@Override
	public ConfigSection serialize() {
		ConfigSection map = new ConfigSection();

		map.set("colour", this.colour);
		map.set("female", this.female);
		map.set("level", this.combatLevel & 0xFF); // Save as unsigned.
		map.set("title", this.title);
		map.set("red", this.red);
		map.set("green", this.green);
		map.set("blue", this.blue);
		map.set("ambient", this.ambient);
		map.set("intensity", this.intensity);
		map.set("apply", this.applyCustom);
		return map;
	}

	@Override
	public void deserialize(ConfigSection map) {
		this.colour = map.getByteArray("colour", this.colour).clone();
		this.female = map.getBoolean("female", this.female);
		this.combatLevel = (byte) map.getInt("combatLevel", this.combatLevel);
		this.title = map.getInt("title", this.title);
		this.red = map.getInt("red", this.red);
		this.green = map.getInt("green", this.green);
		this.blue = map.getInt("blue", this.blue);
		this.ambient = map.getInt("ambient", this.ambient);
		this.intensity = map.getInt("intensity", this.intensity);
		this.applyCustom = map.getBoolean("apply", this.applyCustom);
	}

	public int getRenderAnimationId() {
		return renderAnimationId;
	}

	public void setRenderAnimationId(int renderAnimationId) {
		this.renderAnimationId = renderAnimationId;
	}
}