package inventory;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, options={"Bury"})
public class Bury extends ActionHandler{
	//IDs of bones -              normal - burnt - wolf - monkey - bat - big - jogre - zogre - shaikahan - baby - wyvern - dragon - fayrg - raurg - dagannoth - ourg - frost-dragon
	private static final int[] bones = new int[]{526,    528,    2859,  3183,    530,  532,  3125,   4812,   3123,       534,   6812,    536,     4830,   4832,   6729,       4834,  18830};
	//Experience gained from the bones
	private static final double[] xp = new double[]{10,  10,     10,    12.5,    12.5, 20,   20,     25,     30,         35,    40,      50,      52.5,   55,     65,         75,    85};
	
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution{
		int slot = (Integer) args.get("slot");
		ItemStack item = (ItemStack) args.get("item");
		//Find the bone
		for(int i = 0; i < bones.length; i++){
			if(bones[i] == item.getId()){
				//Remove the bone
				if(mob instanceof InventoryHolder){
					((InventoryHolder) mob).getInventory().remove(slot, item);
				}
				
				//Do an animation action, we continue when it's finished
				//p.getActions().insertBefore(self, new AnimateAction(p, 827, false));
				mob.animate(827, 10);
				if(mob instanceof Client){
					((Client) mob).getProtocol().sendSound(2738, 0, 1);
				}
				//We've done nothing important here, so yield and let the animation run
				//We are guaranteed to have a valid bone here. Now awarding experience for burying the bone.
				Action.wait(4);
				mob.getSkills().addExp(SkillType.PRAYER, xp[i]);
				return;
			}
		}
		//Not implemented
		if(mob instanceof Client){
			((Client) mob).sendMessage("That bone has not been implemented yet, sorry!");
		}
	}
}