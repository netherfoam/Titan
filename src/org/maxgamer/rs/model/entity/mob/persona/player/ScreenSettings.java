package org.maxgamer.rs.model.entity.mob.persona.player;

/**
 * @author netherfoam
 */
public class ScreenSettings {
	private byte mode;
	private short width;
	private short height;
	private boolean isWindowActive;
	
	public ScreenSettings() {
	}
	
	public boolean isWindowActive() {
		return isWindowActive;
	}
	
	/**
	 * Display mode of the player. This is 0, 1, 2 or 3 and never anything else.
	 * Values 0 and 1 represent fixed, while 2 and 3 represent resizeable
	 * gameframes.
	 * @return the player's display mode.
	 */
	public int getDisplayMode() {
		return mode;
	}
	
	/**
	 * Returns true if the user has their screen mode set to a fixed size
	 * @return true if the user has their screen mode set to a fixed size
	 */
	public boolean isFixedScreen(){
		if(getDisplayMode() <= 1) return true;
		return false;
	}
	
	/**
	 * Returns true if the user has their screen mode set to a variable / full screen size
	 * @return true if the user has their screen mode set to a variable / full screen size
	 */
	public boolean isFullScreen(){
		return !isFixedScreen();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setWindowActive(boolean active) {
		this.isWindowActive = active;
	}
	
	public void setWidth(int width) {
		if (width < 0 || width > Short.MAX_VALUE) throw new IllegalArgumentException("Width & Height must be 65535 >= Width/Height >= 0 given " + width);
		this.width = (short) width;
	}
	
	public void setHeight(int height) {
		if (height < 0 || height > Short.MAX_VALUE) throw new IllegalArgumentException("Width & Height must be 65535 >= Width/Height >= 0 given " + height);
		this.height = (short) height;
	}
	
	public void setDisplayMode(int mode) {
		if (mode < 0 || mode > 3) throw new IllegalArgumentException("Display mode must be from 0-3.");
		this.mode = (byte) mode;
	}
	
	@Override
	public String toString() {
		return "Mode: " + mode + " (" + width + "x" + height + "), " + (isWindowActive ? "Active" : "Inactive");
	}
}