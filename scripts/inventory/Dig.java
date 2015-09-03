package inventory;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, options={"Dig"})
public class Dig extends ActionHandler {

	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		mob.animate(830, 5);
		//TODO: add cases where this will actually be used: farming, clues, barrows,...
		mob.sendMessage("Nothing interesting happens.");
		Action.wait(1);
	}
}
