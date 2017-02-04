package org.maxgamer.rs.model.skill.harvest;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.map.object.GameObject;

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

        if (!target.hasData()) {
            target.setData(harvest.getResidualAmount());
        }
        this.harvestTime = 0;
        if (mob instanceof Persona) {
            this.tool = harvest.getTool((Persona) mob);

            if (tool != null) {
                this.harvestTime = harvest.getHarvestTime((Persona) mob, tool);
            }
        }
        // mob.getUpdateMask().getFacing().setTarget(target.getCenter());
        mob.setFacing(Facing.face(target.getCenter()));
    }

    @Override
    protected void run() throws SuspendExecution {
        if (tool == null) {
            if (getOwner() instanceof Player) {
                getOwner().sendMessage("You need the appropriate tool to do that.");
                return;
            }
        }

        while (target.isVisible(getOwner()) && target.getData() > 0) {
            getOwner().getUpdateMask().setAnimation(tool.getAnimation(), 3);

            harvestTime--;
            if (harvestTime > 0) {
                // Keep harvesting
                wait(1);
                continue;
            }

            target.setData(target.getData() - 1);

            if (getOwner() instanceof Persona) {
                harvest.applyReward((Persona) getOwner());
            }

            if (target.getData() == 0) {
                harvest.replenish(target);
                getOwner().getUpdateMask().setAnimation(null, 3);
                return;
            }
        }
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