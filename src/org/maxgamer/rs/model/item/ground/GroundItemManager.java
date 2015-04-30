package org.maxgamer.rs.model.item.ground;

import java.util.PriorityQueue;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;

/**
 * @author netherfoam
 */
public class GroundItemManager extends Tickable {
	/**
	 * The queue of items which are currently private
	 */
	private PriorityQueue<GroundItemStack> privates = new PriorityQueue<>();
	
	/**
	 * The queue of items which are currently public
	 */
	private PriorityQueue<GroundItemStack> publics = new PriorityQueue<>();
	
	/**
	 * Constructs a new, empty ground item manager
	 */
	public GroundItemManager() {
		
	}
	
	/**
	 * Adds the given GroundItemStack to this GroundItemManager. This manager
	 * then performs all necessary maintenance on the item.
	 * @param item the item to add, not null
	 */
	protected void add(GroundItemStack item) {
		PriorityQueue<GroundItemStack> queue;
		if (item.isPublic()) {
			queue = publics;
		}
		else {
			queue = privates;
		}
		queue.add(item);
		
		if (queue.iterator().next() == item) {
			//So the first item in the queue (First to expire/Become public) is
			//the item we just added. So we must adjust the server ticker as appropriate.
			//Core.getServer().getTicker().cancelAndSubmit(1, this);
			if (this.isQueued()) this.cancel();
			this.queue(1);
		}
		
		for (Viewport view : item.getLocation().getNearby(Viewport.class, 0)) {
			if (item.isPublic() || item.getOwner() == view.getOwner()) {
				view.getOwner().getProtocol().sendGroundItem(item.getLocation(), item.getItem());
			}
			//else, they can't see it yet.
		}
	}
	
	/**
	 * Removes the given GroundItemStack from this GroundItemManager. This
	 * manager will no longer perform all necessary maintenances on the item.
	 * @param item the item to remove, not null. No error is thrown if this item
	 *        does not exist in it, this simply returns.
	 */
	protected void remove(GroundItemStack item) {
		PriorityQueue<GroundItemStack> queue;
		
		//TODO: There may be a bug here, if isPublic returns the wrong queue. If so, the exception will be raised
		if (item.isPublic()) {
			queue = publics;
			assert privates.contains(item) == false : "Privates contains a public item";
		}
		else {
			queue = privates;
			assert publics.contains(item) == false : "Publics contains a private item";
		}
		
		if (queue.contains(item) == false) {
			//Item no longer exists?
			return;
		}
		
		queue.remove(item);
		if (queue.isEmpty() == false && queue.iterator().next() == item) {
			//So the first item in the queue (First to expire/Become public) is
			//the item we just added. So we must adjust the server ticker as appropriate.
			//Core.getServer().getTicker().cancelAndSubmit(1, this);
			if (this.isQueued()) this.cancel();
			this.queue(1);
		}
		
		for (Viewport view : item.getLocation().getNearby(Viewport.class, 0)) {
			if (item.isPublic() || item.getOwner() == view.getOwner()) {
				view.getOwner().getProtocol().removeGroundItem(item.getLocation(), item.getItem());
			}
			//else, they can't see it yet.
		}
		
	}
	
	/**
	 * Ticks all ground items controlled by this GroundItemManager, and then
	 * resubmits this manager for the a future tick, based on when the next
	 * ground item checkup is required.
	 */
	@Override
	public void tick() {
		int min = -1; //-1 will cancel us if need be
		int tick = Core.getServer().getTicker().getTicks();
		
		GroundItemStack g;
		
		while ((g = privates.peek()) != null) {
			if (g.isDestroyed()) {
				privates.remove(); //Well that's odd. It should not be here.
				continue;
			}
			if (tick >= g.privacy) {
				//Item needs to become public
				for (Viewport view : g.getLocation().getNearby(Viewport.class, 0)) {
					if (g.getOwner() != view.getOwner()) {
						//Without sending the item to the owner again, send it to everyone else.
						view.getOwner().getProtocol().sendGroundItem(g.getLocation(), g.getItem());
					}
				}
				privates.remove(g);
				g.owner = null; //TODO: This is a hack fix for a bug with no implications. If privacy is used it will be at the front of the queue anyway
				publics.add(g);
			}
			else {
				min = g.privacy - tick;
				break;
			}
		}
		
		while ((g = publics.peek()) != null) {
			if (g.isDestroyed()) {
				publics.remove(); //Well that's odd. It should not be here.
				continue;
			}
			
			if (tick >= g.expires) {
				//Item needs to be destroyed
				g.destroy(); //By effect, calls this.remove(g)
				assert publics.contains(g) == false; //G should no longerbe in the public queue.
			}
			else {
				min = Math.min(min, g.expires - tick);
				break;
			}
		}
		
		if (min > 0) {
			//Core.getServer().getTicker().cancelAndSubmit(min, this);
			if (this.isQueued()) this.cancel();
			this.queue(min);
		}
	}
}