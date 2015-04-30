package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;

/**
 * @author netherfoam
 */
public class PickupItemAction extends Action {
	private GroundItemStack item;
	
	public PickupItemAction(Persona mob, GroundItemStack item) {
		super(mob);
		this.item = item;
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public boolean run() {
		if (item.isDestroyed()) return true; //Dead item, end of task
		
		if (item.getLocation().equals(getOwner().getLocation())) {
			item.destroy();
			try {
				getOwner().getInventory().add(item.getItem());
			}
			catch (ContainerException e) {
				//TODO: Should we just ignore this?
			}
			return true;
		}
		
		//Else, we've failed to pick the item up since we're not
		//at that location. We still return true because we are
		//done though.
		return true;
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
	
	public GroundItemStack getItem() {
		return item;
	}
}