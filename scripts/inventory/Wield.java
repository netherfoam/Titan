package inventory;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

@Script(type=ItemStack.class, options={"Wield", "Wear"})
public class Wield extends ActionHandler {
	
	public void run(Mob mob, Map<String, Object> args) {
		ItemStack item = (ItemStack) args.get("item");
		int slot = (Integer) args.get("slot");
		
		if (item.getWeapon() == null) {
			if(mob instanceof Client){
				((Client) mob).getCheats().log(10, "Attempted to wear an item which has no equipment slot.");
			}
			return;
		}
		
		Container equip = mob.getEquipment();
		//Container inv// = //mob.getInventory();
		Container inv = null;
		if(mob instanceof InventoryHolder){
			inv = ((InventoryHolder) mob).getInventory();
		}
		
		WieldType target = item.getWeapon().getSlot();
		ItemStack old = equip.get(target.getSlot());
		
		if (old != null) {
			if (old.matches(item)) {
				if (old.getAmount() < old.getStackSize()) {
					//Add the stacks together 
					
					if (old.getAmount() + item.getAmount() > old.getStackSize()) {
						//Not all of the items can be added to the slot, but some can->
						long swap = old.getStackSize() - old.getAmount();
						old = old.setAmount(old.getStackSize());
						item = item.setAmount(item.getAmount() - swap);
						
						if(inv != null) inv.set(slot, item);
						equip.set(target.getSlot(), old);
					}
					else {
						//All of the items can be added to the slot->
						inv.set(slot, null);
						equip.set(target.getSlot(), old.setAmount(old.getAmount() + item.getAmount()));
					}
				}
				else {
					//We're already at the max stack size->
					//Nothing will be accomplished by equipping this->
					yield();
					return;
				}
			}
			else {
				//The two items do not match-> Remove the old one, equip the new one->
				inv.set(slot, old);
				equip.set(target.getSlot(), item);
			}
		}
		else {
			//There is currently no other item equipped in the slot->
			inv.set(slot, null);
			equip.set(target.getSlot(), item);
		}
		yield();
		return;
	}
}