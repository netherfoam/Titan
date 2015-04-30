package org.maxgamer.rs.model.item.ground;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class GroundItemStack extends Entity implements Comparable<GroundItemStack> {
	/**
	 * The ItemStack on the ground
	 */
	protected ItemStack stack;
	
	/**
	 * The tick when the privacy expires on this item and becomes public
	 */
	protected int privacy;
	
	/**
	 * The tick when this item expires and is removed entirely
	 */
	protected int expires;
	
	/**
	 * The player who owns this item stack, eg for privacy.
	 */
	protected Persona owner;
	
	/**
	 * Creates a new GroundItemStack. This item will not be spawned until the
	 * location is set through {@link GroundItemStack#setLocation(Location)}.
	 * @param stack the item this represents
	 * @param owner The owner of the items, null if none (Immediately public)
	 * @param privacy The number of ticks this item will be private for.
	 * @param expires The number of ticks this item will be valid for. When this
	 *        tick number is reached, it will be removed from the world
	 */
	public GroundItemStack(ItemStack stack, Persona owner, int privacy, int expires) {
		if (stack == null) throw new NullPointerException("ItemStack may not be null");
		if (owner == null && privacy > 0) throw new IllegalArgumentException("If owner is null, privacy may not be > 0. Given privacy " + privacy);
		if (owner != null && privacy < 0) throw new IllegalArgumentException("If owner != null, then privacy ticks must be >= 0");
		if (expires < 0) throw new IllegalArgumentException("Expires must be >= 0");
		
		this.stack = stack;
		this.owner = owner;
		this.privacy = privacy + Core.getServer().getTicker().getTicks();
		this.expires = expires + Core.getServer().getTicker().getTicks();
	}
	
	@Override
	public void setLocation(Location l) {
		if (getLocation() != null) {
			Core.getServer().getGroundItems().remove(this);
		}
		
		super.setLocation(l);
		
		if (getLocation() != null) {
			Core.getServer().getGroundItems().add(this);
		}
	}
	
	/**
	 * The ItemStack available here
	 * @return
	 */
	public ItemStack getItem() {
		return stack;
	}
	
	/**
	 * True if this item has expired and has been deleted
	 * @return true if this item has expired
	 */
	public boolean hasExpired() {
		return Core.getServer().getTicker().getTicks() >= expires;
	}
	
	/**
	 * True if this item is currently public (privacy has expired or owner is
	 * null)
	 * @return true if this item is currently public
	 */
	public boolean isPublic() {
		return owner == null || Core.getServer().getTicker().getTicks() >= privacy;
	}
	
	/**
	 * Fetches the owner of this item, may be null if public
	 * @return the owner of this item
	 */
	public Persona getOwner() {
		return owner;
	}
	
	public boolean isVisible(Mob to) {
		return isPublic() || getOwner() == to;
	}
	
	@Override
	public int compareTo(GroundItemStack g2) {
		int t2;
		if (g2.isPublic()) {
			t2 = g2.expires;
		}
		else {
			t2 = g2.privacy;
		}
		
		int t1;
		if (this.isPublic()) {
			t1 = this.expires;
		}
		else {
			t1 = this.privacy;
		}
		//If we ever actually need long ticks, this will die for items
		//who were private before at tick 0
		//Technically, > 2b ticks is roughly 100 years of runtime.
		return (t1 - t2);
	}
	
	@Override
	public void destroy() {
		//Worth noting that this calls setLocation(null), which will
		//remove the item from the GroundItemManager.
		super.destroy();
	}
}