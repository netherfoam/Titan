package ground;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

@Script(type=GroundItemStack.class, options={"Take"})
public class Take extends ActionHandler {
	
	public void run(Mob m, Map<String, Object> args) {
		GroundItemStack ground = (GroundItemStack) args.get("item");
		if (ground.isDestroyed()) return; //Dead item, end of task
		
		if (ground.getLocation().equals(m.getLocation())) {
			ground.destroy();
			try {
				if(m instanceof InventoryHolder){
					((InventoryHolder) m).getInventory().add(ground.getItem());
				}
			}
			catch (ContainerException e) {
				if (m instanceof Client) {
					((Client) m).sendMessage("You need more space to pick that up.");
				}
			}
		}
		yield();
	}
}