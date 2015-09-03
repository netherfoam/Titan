package inventory;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerState;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, options={"Clean"})
public class Clean extends ActionHandler{

	private static enum Herb {
		GUAM(199, 2.5, 3, 249),
		MARRENTILL(201, 3.8, 5, 251),
		TARROMIN(203, 5, 11, 253),
		HARRALANDER(205, 6.3, 20, 255),
		RANARR(207, 7.5, 25, 257),
		TOADFLAX(3049, 8, 30, 2998),
		SPIRIT_WEED(12174, 7.8, 35, 12172),
		IRIT(209, 8.8, 40, 259),
		WERGALI(14836, 9.5, 41, 14854),
		AVANTOE(211, 10, 48, 261),
		KWUARM(213, 11.3, 54, 263),
		SNAPDRAGON(3051, 11.8, 59, 3000),
		CADANTINE(215, 12.5, 65, 265),
		LANTADYME(2485, 13.1, 67, 2481),
		DWARF_WEED(217, 13.8, 70, 267),
		TORSTOL(219, 15, 75, 269);

		private int herbId, levelRequired, newHerbId;
		private double experience;

		private Herb(int herbId, double experience, int levelRequired, int newHerbId) {
			this.herbId = herbId;
			this.experience = experience; 
			this.levelRequired = levelRequired;
			this.newHerbId = newHerbId;
		}

		public int getIdentifier() {
			return herbId;
		}

		public int getNewIdentifier() {
			return newHerbId;
		}

		public int getRequiredLevel() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}
	}
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		ItemStack item = (ItemStack) args.get("item");
		
		if (mob instanceof InventoryHolder) {
			ContainerState i = ((InventoryHolder) mob).getInventory().getState();
			int slot = (int) args.get("slot");

			for (Herb herb : Herb.values()) {
				if (herb.getIdentifier() == item.getId()) {
					if (herb.getRequiredLevel() > mob.getSkills().getLevel(SkillType.HERBLORE)) {
						mob.sendMessage("You need a herblore level of "+herb.getRequiredLevel()+ " in order to clean this herb.");
						return;
					}
					i.remove(slot, item);
					i.add(slot, ItemStack.create(herb.getNewIdentifier(), item.getAmount(), item.getHealth()));
					i.apply();
					
					//Action.wait(1);//TODO: Maybe we should add it? On RS there no delay when cleaning herbs manually. 
					mob.sendMessage("You succesfully clean the herb.");
					mob.getSkills().addExp(SkillType.HERBLORE, herb.getExperience());
					return;
				}
			}
			
			if (mob instanceof Client) 
				((Client)mob).sendMessage("That Herb is not implemented yet, sorry!");
		}
	}
}
