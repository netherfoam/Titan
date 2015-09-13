package item_on_object;

import java.util.Map;

import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemProto;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, names={"Raw shrimp", "Raw anchovies", "Raw lobster", "Raw swordfish", "Raw shark"})
public class Cook extends ActionHandler{
	private static final Animation ANIMATION = new Animation(897);
	
	private static enum Food{
		SHRIMP("Shrimps", "Raw shrimps", "Burnt shrimp", 1, 30),
		ANCHOVIES("Anchovies", "Raw anchovies", "Burnt shrimp", 1, 30),
		SARDINE("Sardine", "Raw sardine", "Burnt shrimp", 1, 40),
		HERRING("Herring", "Raw herring", "Burnt shrimp", 5, 50),
		MACKEREL("Mackerel", "Raw mackerel", "Burnt shrimp", 10, 60),
		TROUT("Trout", "Raw trout", "Burnt shrimp", 15, 70),
		COD("Cod", "Raw cod", "Burnt shrimp", 18, 75),
		PIKE("Pike", "Raw pike", "Burnt shrimp", 20, 80),
		SALMON("Salmon", "Raw salmon", "Burnt shrimp", 25, 90),
		TUNA("Tuna", "Raw tuna", "Burnt shrimp", 30, 100),
		LOBSTER("Lobster", "Raw lobster", "Burnt lobster", 40, 120),
		BASS("Bass", "Raw bass", "Burnt shrimp", 43, 130),
		SWORDFISH("Swordfish", "Raw swordfish", "Burnt swordfish", 45, 140),
		MONKFISH("Monkfish", "Raw monkfish", "Burnt monkfish", 62, 150),
		SHARK("Shark", "Raw shark", "Burnt shark", 80, 210),
		SEA_TURTLE("Sea turtle", "Raw sea turtle", "Burnt sea turtle", 82, 212),
		MANTA_RAY("Manta ray", "Raw manta ray", "Burnt manta ray", 91, 200),
		ROCKTAIL("Rocktail", "Raw rocktail", "Burnt shrimp", 92, 225),
		;
		
		public static Food get(ItemStack raw){
			for(Food f : Food.values()){
				if(f.raw.matches(raw)){
					return f;
				}
			}
			return null;
		}
		
		//private String name;
		private int level;
		private double exp;
		
		//Inferred properties
		private ItemStack raw;
		private ItemStack cooked;
		private ItemStack burnt;
		
		private Food(String cookedName, String raw, String burnt, int level, double exp){
			this.level = level;
			this.exp = exp;
			
			try{
				this.cooked = ItemStack.create(ItemProto.forName(cookedName).getId());
				this.raw = ItemStack.create(ItemProto.forName(raw).getId());
				this.burnt = ItemStack.create(ItemProto.forName(burnt).getId());
			}
			catch(NullPointerException e){
				e.printStackTrace();
				Log.warning("Failed to load item with name " + cookedName + " from database for cooking.");
			}
		}
	}
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		ItemStack item = (ItemStack) args.get("item");
		GameObject object = (GameObject) args.get("object");
		
		String name = object.getName().toLowerCase();
		if(name.contains("range") == false && name.contains("stove") == false && name.contains("oven") == false && name.contains("fire") == false && name.contains("furnace") == false){
			return;
		}
		
		Food f = Food.get(item);
		if(f == null){
			mob.sendMessage("That food isn't handled yet!");
			return;
		}
		
		mob.animate(ANIMATION, 8);
		Action.wait(4);
		if(mob instanceof InventoryHolder){
			Container inv = ((InventoryHolder) mob).getInventory();
			try{
				inv.remove(item);
			}
			catch(ContainerException e){
				return;
			}
			
			int lvl = mob.getSkills().getLevel(SkillType.COOKING, true);
			mob.getSkills().addExp(SkillType.COOKING, f.exp);
			if(f.level + 10 > lvl){
				if(f.level + Erratic.nextInt(10) > lvl){
					inv.add(f.burnt);
					return;
				}
			}
			
			inv.add(f.cooked);
		}
	}
	
}