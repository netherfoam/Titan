package gameobject;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.model.skill.harvest.HarvestTool;
import org.maxgamer.rs.model.skill.harvest.Harvestable;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=GameObject.class, options={"Chop down"})
public class ChopDown extends ActionHandler {
	public static final HarvestTool BRONZE_AXE = new HarvestTool(ItemStack.create(1351), SkillType.WOODCUTTING, 1, 879, 1.0);
	public static final HarvestTool IRON_AXE = new HarvestTool(ItemStack.create(1349), SkillType.WOODCUTTING, 1, 877, 1.3);
	public static final HarvestTool STEEL_AXE = new HarvestTool(ItemStack.create(1353), SkillType.WOODCUTTING, 1, 875, 1.6);
	public static final HarvestTool BLACK_AXE = new HarvestTool(ItemStack.create(1361), SkillType.WOODCUTTING, 1, 873, 1.9);
	public static final HarvestTool MITHRIL_AXE = new HarvestTool(ItemStack.create(1355), SkillType.WOODCUTTING, 1, 871, 2.2);
	public static final HarvestTool ADAMANT_AXE = new HarvestTool(ItemStack.create(1357), SkillType.WOODCUTTING, 1, 869, 2.5);
	public static final HarvestTool RUNE_AXE = new HarvestTool(ItemStack.create(1359), SkillType.WOODCUTTING, 1, 867, 2.8);
	public static final HarvestTool DRAGON_AXE = new HarvestTool(ItemStack.create(6739), SkillType.WOODCUTTING, 1, 2846, 3.1);
	//						 Name							Log Item				Skill					  LVL EXP  CUTICS NCuts Replac Respawn TOOLS
	public static final Harvestable TREE = new Harvestable(ItemStack.create(1511), SkillType.WOODCUTTING, 1, 25.0, 5, 5, 1, 1, 1342, 5, getTools());
	public static final Harvestable WILLOW = new Harvestable(ItemStack.create(1519), SkillType.WOODCUTTING, 30, 67.5, 15, 15, 4, 4, 1342, 30, getTools());
	public static final Harvestable OAK = new Harvestable(ItemStack.create(1521), SkillType.WOODCUTTING, 15, 37.5, 10, 10, 2, 2, 1342, 15, getTools());
	public static final Harvestable MAGIC = new Harvestable(ItemStack.create(1513), SkillType.WOODCUTTING, 75, 250, 50, 50, 9, 9, 1342, 120, getTools());
	public static final Harvestable MAPLE = new Harvestable(ItemStack.create(1517), SkillType.WOODCUTTING, 45, 100, 25, 25, 5, 5, 1342, 60, getTools());
	public static final Harvestable MAHOGANY = new Harvestable(ItemStack.create(6332), SkillType.WOODCUTTING, 50, 125, 28, 28, 4, 4, 1342, 125, getTools());
	public static final Harvestable TEAK = new Harvestable(ItemStack.create(6333), SkillType.WOODCUTTING, 35, 85, 20, 20, 4, 4, 1342, 100, getTools());
	public static final Harvestable ACHEY = new Harvestable(ItemStack.create(2862), SkillType.WOODCUTTING, 1, 25, 5, 5, 4, 4, 1342, 80, getTools());
	public static final Harvestable YEW = new Harvestable(ItemStack.create(1515), SkillType.WOODCUTTING, 60, 175, 32, 32, 7, 7, 1342, 160, getTools());
	public static final Harvestable DRAMEN = new Harvestable(ItemStack.create(771), SkillType.WOODCUTTING, 36, 0, 5, 5, 4, 4, 1342, 22, getTools());
	
	public static HarvestTool[] getTools() {
		return new HarvestTool[] { BRONZE_AXE, IRON_AXE, STEEL_AXE, BLACK_AXE, MITHRIL_AXE, ADAMANT_AXE, RUNE_AXE, DRAGON_AXE };
	}
	
	public static Harvestable[] getTrees() {
		return new Harvestable[] { TREE, WILLOW, OAK, MAGIC, MAPLE, MAHOGANY, TEAK, ACHEY, YEW, DRAMEN };
	}
	
	public void run(Mob m, Map<String, Object> args) throws SuspendExecution {
		GameObject obj = (GameObject) args.get("target");
		String name = obj.getName();
		Harvestable harvest;
		HarvestTool tool = null;
		if (name.equals("Tree")) {
			harvest = TREE;
		}
		else if (name.equals("Oak")) {
			harvest = OAK;
		}
		else if (name.equals("Willow")) {
			harvest = WILLOW;
		}
		else if (name.equals("Magic")) {
			harvest = MAGIC;
		}
		else if (name.equals("Maple")) {
			harvest = MAPLE;
		}
		else if (name.equals("Mahogany")) {
			harvest = MAHOGANY;
		}
		else if (name.equals("Teak")) {
			harvest = TEAK;
		}
		else if (name.equals("Achey")) {
			harvest = ACHEY;
		}
		else if (name.equals("Yew")) {
			harvest = YEW;
		}
		else if (name.equals("Dramen")) {
			harvest = DRAMEN;
		}
		else {
			return; //Not implemented
		}
		
		if (obj.hasData() == false) {
			obj.setData(harvest.getResidualAmount());
		}
		
		int harvestTime = harvest.getHarvestTime();
		
		if (m instanceof Persona) {
			tool = harvest.getTool((Persona) m);
			
			if (tool != null) {
				harvestTime = (int) (harvestTime / tool.getEfficiency());
			}
		}
		
		if (tool == null) {
			if (m instanceof Player) {
				((Player) m).sendMessage("You need the appropriate tool to do that.");
				return;
			}
		}
		
		while (obj.getData() > 0) {
			m.animate(tool.getAnimation(), 10);
			
			Action.wait(harvestTime);
			
			obj.setData(obj.getData() - 1);
			
			if (m instanceof Persona) {
				harvest.applyReward((Persona) m);
			}
		}
		
		harvest.replenish(obj);
		m.getUpdateMask().setAnimation(null, 3);
	}
}