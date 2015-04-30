package org.maxgamer.rs.model.skill.harvest;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.map.GameObject;

/**
 * @author netherfoam
 */
public class HarvestAction extends Action {
	private GameObject target;
	private Harvestable harvest;
	private HarvestTool tool;
	private int harvestTime;
	
	public HarvestAction(Mob mob, GameObject target, Harvestable harvest) {
		super(mob);
		
		if (target == null) {
			throw new NullPointerException("Target may not be null");
		}
		if (harvest == null) {
			throw new NullPointerException("Harvestable may not be null");
		}
		
		this.target = target;
		this.harvest = harvest;
		
		if (target.hasData() == false) {
			target.setData(harvest.getResidualAmount());
		}
		this.harvestTime = harvest.getHarvestTime();
		if (mob instanceof Persona) {
			this.tool = harvest.getTool((Persona) mob);
			
			if (tool != null) {
				this.harvestTime = (int) (this.harvestTime / this.tool.getEfficiency());
			}
		}
		//mob.getUpdateMask().getFacing().setTarget(target.getCenter());
		mob.setFacing(Facing.face(target.getCenter()));
	}
	
	@Override
	protected boolean run() {
		if (tool == null) {
			if (getOwner() instanceof Player) {
				((Player) getOwner()).sendMessage("You need the appropriate tool to do that.");
				return true;
			}
		}
		if (target.getData() <= 0) {
			//We are done
			return true;
		}
		
		getOwner().getUpdateMask().setAnimation(tool.getAnimation(), 3);
		
		harvestTime--;
		if (harvestTime > 0) {
			//Keep harvesting
			return false;
		}
		
		target.setData(target.getData() - 1);
		
		if (getOwner() instanceof Persona) {
			harvest.applyReward((Persona) getOwner());
		}
		
		if (target.getData() == 0) {
			harvest.replenish(target);
			getOwner().getUpdateMask().setAnimation(null, 3);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void onCancel() {
		getOwner().getUpdateMask().setAnimation(null, 25);
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}