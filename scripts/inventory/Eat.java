package inventory;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, options={"Eat"})
public class Eat extends ActionHandler{
	//IDs of food - Cabbage, potato, lobster, swordfish, shark
	static int[] ids = new int[]{1965, 1942, 379, 373, 385};
	//Heal amounts of foods as above
	static int[] hps = new int[]{30,   30,   120, 140, 200};
	
	//The food the player ate
	int food = -1;
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution{
		ItemStack item = (ItemStack) args.get("item");
		int slot = (Integer) args.get("slot");
		
		//Find the food
		for(int i = 0; i < ids.length; i++){
			if(ids[i] == item.getId()){
				food = i;
				//Remove the food
				//p.getInventory().remove(slot, item);
				//Do an animation action, we continue when it's finished
				//p.getActions().insertBefore(self, new AnimateAction(p, 829, false));
				mob.animate(829, 5);
				if(mob instanceof Client){
					((Client) mob).getProtocol().sendSound(2393, 255, 255);
				}
				
				Action.wait(2);
				
				//We are guaranteed to have a valid food here. Attempt to heal the player.
				mob.setHealth(Math.min(mob.getHealth() + hps[food], mob.getMaxHealth()));
				
				if(mob instanceof InventoryHolder){
					((InventoryHolder) mob).getInventory().remove(slot, item);
				}
				
				return;
			}
		}
		
		mob.sendMessage("That food has not been implemented yet, sorry!");
	}
}