package gameobject;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;

import co.paralleluniverse.fibers.SuspendExecution;

public class Pick extends ActionHandler {
	public void run(Mob p, Map<String, Object> args) throws SuspendExecution {
		GameObject g = (GameObject) args.get("target");
		p.animate(827, 10);
		Action.wait(3);
		
		try {
			ItemStack item;
			String name = g.getName().toLowerCase();
			if (name.equals("cabbage")) item = ItemStack.create(1965);
			else if (name.equals("potato")) item = ItemStack.create(1942);
			else if (name.equals("wheat")) item = ItemStack.create(1947);
			else if (name.equals("flax")) item = ItemStack.create(1779);
			else if (name.equals("nettles")) item = ItemStack.create(4241);
			else {
				return;
			}
			if(p instanceof InventoryHolder){
				((InventoryHolder) p).getInventory().add(item);
			}
			
			g.hide(20); //Hide the plant for 20s
		}
		catch (ContainerException e) {
			if (p instanceof Client) {
				((Client) p).sendMessage("You need more room to pick that.");
			}
		}
	}
}