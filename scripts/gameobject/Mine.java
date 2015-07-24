package gameobject;

import java.util.Map;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.model.skill.harvest.HarvestTool;
import org.maxgamer.rs.model.skill.harvest.Harvestable;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=GameObject.class, options={"Mine"})
public class Mine extends ActionHandler{
	public static final HarvestTool BRONZE_PICKAXE = 	new HarvestTool(ItemStack.create(1265), SkillType.MINING, 1, 10001, 1.0);
	public static final HarvestTool IRON_PICKAXE = 		new HarvestTool(ItemStack.create(1267), SkillType.MINING, 1, 10002, 1.3);
	public static final HarvestTool STEEL_PICKAXE = 	new HarvestTool(ItemStack.create(1269), SkillType.MINING, 6, 10003, 1.4);
	public static final HarvestTool MITHRIL_PICKAXE = 	new HarvestTool(ItemStack.create(1273), SkillType.MINING, 21, 10004, 1.6);
	public static final HarvestTool ADAMANT_PICKAXE = 	new HarvestTool(ItemStack.create(1271), SkillType.MINING, 31, 10005, 1.8);
	public static final HarvestTool RUNE_PICKAXE = 		new HarvestTool(ItemStack.create(1275), SkillType.MINING, 41, 10006, 1.9);
	public static final HarvestTool DRAGON_PICKAXE = 	new HarvestTool(ItemStack.create(15259), SkillType.MINING, 61, 10007, 2.0);
	public static final HarvestTool INFERNO_ADZE = 		new HarvestTool(ItemStack.create(13661), SkillType.MINING, 41, 10022, 1.9);

	public static HarvestTool[] getTools(){
		return new HarvestTool[]{BRONZE_PICKAXE, IRON_PICKAXE, STEEL_PICKAXE, MITHRIL_PICKAXE, ADAMANT_PICKAXE, RUNE_PICKAXE, DRAGON_PICKAXE, INFERNO_ADZE};
	}

//									 Name								Ore Item				Skill			LVL EXP  CUTICS NCuts Replac Respawn TOOLS
	public static final Harvestable CLAY_ROCK = 		new Harvestable(ItemStack.create(434), SkillType.MINING, 1, 5.0,  2, 6, 1, 1, 11552, 5, getTools());
	public static final Harvestable COPPER_ROCK = 		new Harvestable(ItemStack.create(436), SkillType.MINING, 1, 17.5, 2, 6, 1, 1, 11552, 20, getTools());
	public static final Harvestable TIN_ROCK = 			new Harvestable(ItemStack.create(438), SkillType.MINING, 1, 17.5, 2, 6, 1, 1, 11555, 20, getTools());
	public static final Harvestable IRON_ROCK = 		new Harvestable(ItemStack.create(440), SkillType.MINING, 15, 250,  4, 10, 1, 1, 11552, 30, getTools());
	public static final Harvestable COAL_ROCK = 		new Harvestable(ItemStack.create(453), SkillType.MINING, 30, 100,  4, 12, 1, 1, 11552, 35, getTools());
	public static final Harvestable GOLD_ROCK =  		new Harvestable(ItemStack.create(444), SkillType.MINING, 40, 125,  4, 9, 1, 1, 11552, 35, getTools());
	public static final Harvestable MITHRIL_ROCK = 		new Harvestable(ItemStack.create(447), SkillType.MINING, 45, 85,   6, 15, 1, 1, 11552, 40, getTools());
	public static final Harvestable ADAMANTITE_ROCK = 	new Harvestable(ItemStack.create(449), SkillType.MINING, 70,  25,   10,  20,  1, 1, 11552, 50, getTools());
	public static final Harvestable RUNITE_ROCK = 		new Harvestable(ItemStack.create(451), SkillType.MINING, 85, 175,  20, 40, 1, 1, 11552, 70, getTools());
	public static final Harvestable SILVER_ROCK = 		new Harvestable(ItemStack.create(442),  SkillType.MINING, 36, 0,    3, 8,   1, 1, 11552, 28, getTools());

	public static Harvestable[] getRocks(){
		return new Harvestable[]{CLAY_ROCK, COPPER_ROCK, TIN_ROCK, IRON_ROCK, COAL_ROCK, GOLD_ROCK, MITHRIL_ROCK, ADAMANTITE_ROCK, RUNITE_ROCK, SILVER_ROCK};
	}
	
	public void run(Mob m, Map<String, Object> args) throws SuspendExecution{
		GameObject obj = (GameObject) args.get("target");
		
		int objectId = obj.getId();
		HarvestTool tool = null;
		Harvestable harvest = null;
		
		if(objectId == 11960 || objectId == 11961 || objectId == 11962 || objectId == 37309 || objectId == 37307 ){
			harvest = COPPER_ROCK;
		}
		else if(objectId == 11933 || objectId == 11934 || objectId == 11935 || objectId == 11936 || objectId == 37305 || objectId == 37304 || objectId == 37306){
			harvest = TIN_ROCK;
		}
		else if(objectId == 37310 || objectId == 37312){
			harvest = GOLD_ROCK;
		}
		else if (objectId == 11939 || objectId == 11940 || objectId == 11941 ){
			harvest = ADAMANTITE_ROCK;
		}
		else if (objectId == 11942 || objectId == 11943 || objectId == 11944 ){
			harvest = MITHRIL_ROCK;
		}
		else if (objectId == 11954 || objectId == 11955 || objectId == 11956 ){
			harvest = IRON_ROCK;
		}
		else if (objectId == 11957 || objectId == 11958 || objectId == 11959 ){
			harvest = TIN_ROCK;
		}
		else if (objectId == 11930 || objectId == 11931 || objectId == 11932 ){
			harvest = COAL_ROCK;
		}
		else{
			Log.info("Not a rock. ID: " + objectId);
			return;
		}
		
		int harvestTime = harvest.getHarvestTime();
		if(m instanceof Persona){ //todo: use equipment holder or inventory holder
			tool = harvest.getTool((Persona) m);
			
			if(tool != null){
				harvestTime = (int) (harvestTime / tool.getEfficiency());
			}
		}
		
		if(tool == null){
			if(m instanceof Client){
				((Client) m).sendMessage("You need the appropriate tool to do that.");
				return;
			}
		}
		
		if(obj.hasData() == false){
			obj.setData(harvest.getResidualAmount());
		}
		
		while(obj.getData() > 0){
			m.getUpdateMask().setAnimation(tool.getAnimation(), 3);
			
			Action.wait(harvestTime);
			
			obj.setData(obj.getData() - 1);
			
			if(m instanceof Persona){
				harvest.applyReward((Persona) m);
			}
		}
		
		harvest.replenish(obj);
		m.getUpdateMask().setAnimation(null, 3);
	}
}