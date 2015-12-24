package org.maxgamer.rs.model.skill.harvest;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.map.DynamicGameObject;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class Harvestable {
	/**
	 * The item given to the player after harvesting this
	 */
	private ItemStack reward;

	/**
	 * The minimum time to harvest this in ticks
	 */
	private int minTime;

	/**
	 * The maximum time to harvest this in ticks
	 */
	private int maxTime;

	/**
	 * The minimum number of logs/ores/fish that this object has
	 */
	private int minResidual;

	/**
	 * The maximum number of logs/ores/fish that this object has
	 */
	private int maxResidual;

	/**
	 * The ID of the replacement object, -1 represents no replacement
	 */
	private int replaceObjectId;

	/**
	 * The number of ticks it takes to respawn this object
	 */
	private int respawnTicks;

	/**
	 * The tools which can be used to harvest this object
	 */
	private HarvestTool[] tools;

	/**
	 * The level requirement to harvest this object
	 */
	private int level;

	/**
	 * The skill type for the given level to harvest this object
	 */
	private SkillType skill;

	/**
	 * The reward exp given for successfully harvesting this object
	 */
	private double exp;

	/**
	 * Constructs a harvestable item
	 * 
	 * @param reward
	 *            the item reward given for harvesting this, may be null
	 * @param skill
	 *            the skill type for harvesting this, must not be null if level
	 *            or exp is non-zero
	 * @param level
	 *            the level required to harvest this, must be non-zero if skill
	 *            is non-null
	 * @param exp
	 *            the exp given for harvesting this pre-modifiers, must be zero
	 *            is skill is null
	 * @param minTime
	 *            the minimum time in ticks required to harvest this pre
	 *            modifiers
	 * @param maxTime
	 *            the maximum time in ticks required to harvest this pre
	 *            modifiers
	 * @param minResidual
	 *            the minimum number of times this object may be harvested
	 *            before replenished
	 * @param maxResidual
	 *            the maximum number of times this object may be harvested
	 *            before replenished
	 * @param replaceObjId
	 *            the replacement object id when repleneshing, may be -1
	 * @param respawnTicks
	 *            the number of ticks to respawn this in, must be > 0
	 * @param tools
	 *            the tools which can be used to harvest this. May be null. If
	 *            supplied, one of the given tools is required.
	 */
	public Harvestable(ItemStack reward, SkillType skill, int level, double exp, int minTime, int maxTime, int minResidual, int maxResidual, int replaceObjId, int respawnTicks, HarvestTool... tools) {
		if (minTime > maxTime) {
			throw new IllegalArgumentException("MinTime must be <= maxTime");
		}

		if (minResidual > maxResidual) {
			throw new IllegalArgumentException("MinResidual must be <= maxResidual");
		}

		if (minResidual <= 0 || maxResidual <= 0) {
			throw new IllegalArgumentException("Residual amounts must be > 0");
		}

		if (minTime < 0 || maxTime < 0) {
			throw new IllegalArgumentException("MinTime must be >= 0 and MaxTime must be >= 0");
		}

		if (skill == null && level > 0 || skill != null && level <= 0) {
			throw new IllegalArgumentException("Bad skill requirement. Skill " + skill + ", level " + level);
		}

		if (exp != 0 && skill == null) {
			throw new IllegalArgumentException("Skill is null but exp is " + exp);
		}

		for (HarvestTool t : tools) {
			if (t == null)
				throw new NullPointerException("HarvestTools may not be null");
		}

		this.level = level;
		this.skill = skill;
		this.reward = reward;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.minResidual = minResidual;
		this.maxResidual = maxResidual;
		this.replaceObjectId = replaceObjId;
		this.respawnTicks = respawnTicks;
		this.tools = tools;
		this.exp = exp;
	}

	/**
	 * Fetches the best tool the given player has
	 * 
	 * @param p
	 *            the player
	 * @return the tool or null if they do not have the right tool (and level)
	 *         or this requires no tools.
	 */
	public HarvestTool getTool(Persona p) {
		if (p == null) {
			throw new NullPointerException();
		}
		if (tools == null || tools.length <= 0)
			return null; // No tool available to harvest this.

		HarvestTool best = null;
		int i;
		for (i = 0; i < tools.length; i++) {
			if (tools[i].hasRequirements(p)) {
				if (best == null || best.getEfficiency() < tools[i].getEfficiency()) {
					best = tools[i];
				}
			}
		}

		while (++i < tools.length) {
			if (tools[i].has(p) && tools[i].getEfficiency() > best.getEfficiency()) {
				best = tools[i];
			}
		}

		return best;
	}

	/**
	 * Returns true if this harvestable requires a tool
	 * 
	 * @return true if this harvestable requires a tool
	 */
	public boolean hasTool() {
		return tools != null && tools.length > 0;
	}

	/**
	 * Returns true if the given mob has the requirements for harvesting this
	 * resource. Note that this doesn't check tools, but skills and (if
	 * overridden) things like quests.
	 * 
	 * @param p
	 *            the mob attempting to harvest this object
	 * @return true if the mob can harvest it (ignoring tools) false if they
	 *         cannot.
	 */
	public boolean hasRequirements(Mob p) {
		if (p == null) {
			throw new NullPointerException();
		}
		if (skill != null) {
			if (p.getSkills().getLevel(skill, true) < level)
				return false;
		}
		return true;
	}

	/**
	 * Hides the given object, replacing it with the necessary object if given,
	 * and then shows it after the given respawnTicks have passed. The data
	 * value is set to -1 again (Reset)
	 * 
	 * @param g
	 *            the object to replenish
	 */
	public void replenish(final GameObject g) {
		if (g == null) {
			throw new NullPointerException("Replenish target may not be null");
		}
		g.hide();

		DynamicGameObject rep = null;

		if (replaceObjectId >= 0) {
			rep = new DynamicGameObject(replaceObjectId, g.getType());
			rep.setFacing(g.getFacing());
			rep.setLocation(g.getLocation());
		}

		final DynamicGameObject frep = rep;
		if (respawnTicks >= 0) {
			// Core.getServer().getTicker().submit(respawnTicks, new Tickable(){
			new Tickable() {
				@Override
				public void tick() {
					if (frep != null) {
						frep.destroy();
					}

					// Reset data
					g.setData(-1);
					g.show();
				}
			}.queue(respawnTicks);
		}
	}

	/**
	 * Randomly generates a number between minResidual and maxResidual
	 * (Inclusive) and returns the value. This is a value that can be used as
	 * the residual value for any object that this Harvestable represents
	 * 
	 * @return the residual data value for a harvestable object
	 */
	public int getResidualAmount() {
		return Erratic.nextInt(minResidual, maxResidual);
	}

	/**
	 * Randomly generates a number between minTime and maxTime (Inclusive) and
	 * returns the value. This is a value that can be used as the cycle time for
	 * any object that this Harvestable represents. This is not scaled according
	 * to any tools, which should be done after this call.
	 * 
	 * @return the harvest time for a harvestable object
	 */
	public int getHarvestTime(Persona p, HarvestTool tool) {
		int A = p.getSkills().getLevel(this.skill, true);
		double B = tool.getLevel() / (double) A;
		double C = level / (double) tool.getLevel();
		double D = level / (double) A;
		return (int) Math.max(1, (Erratic.nextInt(minTime, maxTime) * ((B + C + D) / tool.getEfficiency())));
	}

	/**
	 * Applies the reward to the given player. Generally, this will just give
	 * them the reward item or message them if they don't have the space, but it
	 * can be overridden. This also applies any exp gains.
	 * 
	 * @param p
	 *            the player to reward
	 * @return true if successful, false if it failed (Eg not enough space)
	 */
	public boolean applyReward(Persona p) {
		if (p == null) {
			throw new NullPointerException();
		}
		if (reward != null) {
			try {
				p.getInventory().add(reward);
				p.sendMessage("You get some " + reward.getDefinition().getName().toLowerCase() + ".");
			} catch (ContainerException e) {
				if (p instanceof Player) {
					((Player) p).sendMessage("You need more space.");
				}
				return false;
			}
		}
		if (skill != null && exp != 0) {
			// TODO: Exp rate modifiers go here.
			p.getSkills().addExp(skill, exp);
		}
		return true;
	}

	public int getLevel() {
		return level;
	}
}