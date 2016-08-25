package org.maxgamer.rs.model.skill.harvest;

import org.maxgamer.rs.model.entity.mob.npc.loot.Weighted;
import org.maxgamer.rs.model.item.ItemStack;

public class HarvestReward implements Weighted {

	private final ItemStack reward;
	private final int requiredLevel;

	public HarvestReward(ItemStack reward, int requiredLevel) {
		this.reward = reward;
		this.requiredLevel = requiredLevel;
	}

	public HarvestReward(int itemId, int requiredLevel) {
		this(ItemStack.create(itemId), requiredLevel);
	}

	@Override
	public final double getWeight() {
		return 100 - requiredLevel;
	}

	public ItemStack getReward() {
		return reward;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

}
