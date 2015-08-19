package item_on_object;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, names={"Raw shrimp", "Raw anchovies", "Raw lobster", "Raw swordfish", "Raw shark"}, options={"Fire", "Stove", "Cooking range", "Furnace"})
public class Cook extends ActionHandler{
	private static final Animation ANIMATION = new Animation(897);
	/*private static enum Food{
		SHRIMP(317, 315, 323, 1, 30),
		ANCHOVIES(321, 319, 323, 1, 30),
		SARDINE(327, 325, 323, 1, 40),
		HERRING(345, 347, 357, 5, 50),
		
		
		private int raw;
		private int cooked;
		private int burnt;
		private int level;
		private double exp;
		private Food(int raw, int cooked, int burnt, int level, double exp){
			this.raw = raw;
			this.cooked = cooked;
			this.burnt = burnt;
			this.level = level;
			this.exp = exp;
		}
	}*/
	
	private static enum Food{
		SHRIMP("Shrimp", 1, 30),
		ANCHOVIES("Anchovies", 1, 30),
		SARDINE("Sardine", 1, 40),
		HERRING("Herring", 5, 50),
		MACKEREL("Mackerel", 10, 60),
		TROUT("Trout", 15, 70),
		COD("Cod", 18, 75),
		PIKE("Pike", 20, 80),
		SALMON("Salmon", 25, 90),
		TUNA("Tuna", 30, 100),
		LOBSTER("Lobster", 40, 120),
		BASS("Bass", 43, 130),
		SWORDFISH("Swordfish", 45, 140),
		MONKFISH("Monkfish", 62, 150),
		SHARK("Shark", 80, 210),
		SEA_TURTLE("Sea Turtle", 82, 212),
		MANTA_RAY("Manta Ray", 91, 200),
		ROCKTAIL("Rocktail", 92, 225),
		;
		
		private String name;
		private int level;
		private double exp;
		
		//Inferred properties
		private ItemStack raw;
		private ItemStack cooked;
		private ItemStack burnt;
		
		private Food(String name, int level, double exp){
			this.name = name;
			this.level = level;
			this.exp = exp;
			
			//this.raw = ItemProto.getDefinition(id)
		}
	}
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		ItemStack item = (ItemStack) args.get("item");
		GameObject target = (GameObject) args.get("target");
		
		mob.animate(ANIMATION, 10);
		
	}
	
}