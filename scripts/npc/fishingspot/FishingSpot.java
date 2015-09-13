/**
 * 
 */
package npc.fishingspot;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.npc.loot.CommonLootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.LootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.WeightedPicker;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
@Script(type=NPC.class, names="Fishing spot")
public class FishingSpot extends ActionHandler {
	private static enum Fish {
	    SHRIMP(317, 10, 1),
	    SARDINE(327, 20, 5),
	    HERRING(345, 30, 10),
	    ANCHOVIES(321, 40, 15),
	    MACKEREL(353, 20, 16),
	    TROUT(335, 50, 20),
	    COD(341, 45, 23),
	    PIKE(349, 60, 25),
	    SALMON(331, 70, 30),
	    TUNA(359, 80, 35),
	    LOBSTER(377, 90, 40),
	    BASS(363, 100, 46),
	    SWORDFISH(371, 100, 50),
	    MONKFISH(7944, 120, 62),
	    SHARK(383, 110, 76),
	    SEA_TURTLE(395, 38, 79),
	    MANTA_RAY(389, 46, 81),
	    CAVE_FISH(15264, 300, 85);

	    private final int id;

	    private Fish(int id, int xp, int level) {
	        this.id = id;
	    }
	    
	    public ItemStack item(){
	    	return ItemStack.create(this.id);
	    }
	    
	    public CommonLootItem loot(double weight){
	    	return new CommonLootItem(this.item(), weight);
	    }
	}
	
	private static class BaitType{
		/**
		 * Compatible NPCs
		 */
		private int[] npcIds;
		private String option;
		private WeightedPicker<LootItem> loots;
		private Animation animation;
	    private int level;
	    private double xp;
	    private ItemStack tool;
		
		public BaitType(int[] npcs, String option, ItemStack tool, Animation animation, int level, double xp, LootItem...options){
			this.npcIds = npcs;
			this.option = option;
			this.animation = animation;
			this.level = level;
			this.xp = xp;
			this.loots = new WeightedPicker<LootItem>(options);
			this.tool = tool;
		}
	}
	
	private static ItemStack NET = ItemStack.create(303);
	private static ItemStack ROD = ItemStack.create(307);
	private static ItemStack CAGE = ItemStack.create(301);
	private static ItemStack HARPOON = ItemStack.create(311);
	
	private static BaitType[] types;
	
	static{
		types = new BaitType[]{
				//Bait and Lure fishing spots TODO: There are loads of these that aren't done yet.
				new BaitType(new int[]{233, 234, 235, 236}, "Bait", NET, new Animation(621), 1, 10, Fish.SHRIMP.loot(50)),
				
				new BaitType(new int[]{309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 328, 329, 331}, "Lure", ROD, new Animation(622), 20, 50, Fish.TROUT.loot(50), Fish.SALMON.loot(50)),
				new BaitType(new int[]{309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 328, 329, 331}, "Bait", ROD, new Animation(622), 25, 60, Fish.PIKE.loot(50), Fish.CAVE_FISH.loot(50)),
				
				new BaitType(new int[]{319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 330, 332, 334}, "Net", NET, new Animation(622), 1, 10, Fish.SHRIMP.loot(50), Fish.ANCHOVIES.loot(50)), 
				new BaitType(new int[]{319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 330, 332, 334}, "Bait", ROD, new Animation(622), 5, 10, Fish.SARDINE.loot(50), Fish.HERRING.loot(50)), 
				
				
				//new BaitType(??, "Cage", new Animation(619), Fish.LOBSTER.loot(50)),
				
				new BaitType(new int[]{312, 333}, "Cage", CAGE, new Animation(619), 40, 90, Fish.LOBSTER.loot(50)),
				new BaitType(new int[]{312, 333}, "Harpoon", HARPOON, new Animation(618), 50, 100, Fish.SWORDFISH.loot(50), Fish.TUNA.loot(50)),
		};
	}
	
	public static BaitType getType(int npc, String option){
		for(BaitType t : types){
			for(int i : t.npcIds){
				if(i == npc){
					if(t.option.equalsIgnoreCase(option)){
						return t;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		NPC target = (NPC) args.get("target");
		String option = (String) args.get("option");
		
		BaitType t = getType(target.getId(), option);
		if(t == null){
			mob.sendMessage("That Fish isn't implemented. Ask a developer or an administrator to code it!");
			return;
		}
		
		mob.setFacing(Facing.face(target.getLocation()));
		
		if(t.level > mob.getSkills().getLevel(SkillType.FISHING, true)){
			mob.sendMessage("You need a fishing level of " + t.level + " for that!");
			return;
		}
		
		Container inv = null;
		if(mob instanceof InventoryHolder){
			inv = ((InventoryHolder) mob).getInventory();
			
			if(t.tool != null && inv.contains(t.tool) == false){
				mob.sendMessage("You need a " + t.tool.getName().toLowerCase() + " to fish there.");
				return;
			}
		}
		
		while((inv == null || inv.isFull() == false) && mob.getLocation().near(target.getLocation(), 1) && target.isVisible(mob)){
			mob.animate(t.animation, 10);
			Action.wait(3);
			
			if(inv != null){
				ItemStack loot = t.loots.next().getItemStack();
				if(loot != null){
					try{
						inv.add(loot);
						mob.sendMessage("You " + t.option.toLowerCase() + " a " + loot.getName() + ".");
						mob.getSkills().addExp(SkillType.FISHING, t.xp);
					}
					catch(ContainerException e){
						return;
					}
				}
			}
		}
		
	}
}
