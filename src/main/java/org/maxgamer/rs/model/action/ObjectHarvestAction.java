package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.util.Erratic;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Graphics;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.map.object.DynamicGameObject;
import org.maxgamer.rs.model.map.object.GameObject;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ObjectHarvestAction extends Action {
	private GameObject obj;
	private Animation anim;
	private Graphics gfx;
	
	private int minDelay;
	private int maxDelay;
	private int curDelay;
	private int replaceId;
	private int respawnTicks;
	
	private ItemStack reward;
	
	public ObjectHarvestAction(Mob mob, GameObject obj, int anim, int gfx, int minDelay, int maxDelay, int itemId, int amount, int replacementId, int respawnTicks) {
		super(mob);
		
		if (obj == null) {
			throw new NullPointerException("Target object may not be null");
		}
		if (minDelay > maxDelay) {
			throw new IllegalArgumentException("MinDelay must be <= maxDelay");
		}
		
		this.obj = obj;
		if (anim > 0) {
			this.anim = new Animation(anim);
		}
		if (gfx > 0) {
			this.gfx = new Graphics(gfx, 0, 0);
		}
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.replaceId = replacementId;
		this.respawnTicks = respawnTicks;
		
		curDelay = Erratic.nextInt(minDelay, maxDelay);
		this.reward = ItemStack.create(itemId, amount);
	}
	
	@Override
	protected void run() throws SuspendExecution {
		if (obj.getData() <= 0) {
			return; //Done, object is destroyed possibly by another player
		}
		if (anim != null) {
			getOwner().getUpdateMask().setAnimation(anim, 3);
		}
		if (gfx != null) {
			getOwner().getUpdateMask().setGraphics(gfx);
		}
		
		while(obj.getData() > 0){
			wait(curDelay);
			
			if (reward != null && mob instanceof Persona) {
				Persona p = (Persona) mob;
				try {
					p.getInventory().add(reward);
				}
				catch (ContainerException e) {
					if (p instanceof Player) {
						((Player) p).sendMessage("Not enough space!");
					}
					return;
				}
			}
			
			obj.setData(obj.getData() - 1);
			if (obj.hasData() == false) {
				if (anim != null) {
					getOwner().getUpdateMask().setAnimation(null, 3);
				}
				
				obj.hide();
				
				DynamicGameObject rep = null;
				if (replaceId >= 0) {
					rep = new DynamicGameObject(replaceId, obj.getType());
					rep.setFacing(obj.getFacing());
					rep.setLocation(obj.getLocation());
					
				}
				
				final DynamicGameObject del = rep;
				//Core.getServer().getTicker().submit(respawnTicks, new Tickable() {
				new Tickable() {
					@Override
					public void tick() {
						if (del != null) {
							del.destroy();
						}
						obj.show();
						obj.setData(-1);
					}
				}.queue(respawnTicks);
				
				return;
			}
			
			//Generate our next delay
			curDelay = Erratic.nextInt(minDelay, maxDelay);
			wait(1);
			continue;
		}
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}