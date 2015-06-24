/**
 * An example Attack scripted action. This action has its boolean run(Persona, NPC) method called
 * when a player clicks the 'Attack' option on *any* NPC. To create it for only a single named NPC,
 * you could move this file into a folder called "Wizard" for example (scripts/npc_action/Wizard/Attack.java)
 * 
 * Note that the caller sets a variable 'self' which refers to the Java Action. "This" refers to the script here,
 * but "self" refers to the action, for example if you need to yield or queue an action.
 * 
 * We should return true for finished or false for repeat. If repeating, this will be called again. This
 * is standard action behaviour, the same as a Java coded action.
 * 
 * In NPC option scripts, the player does not need to be next to the NPC, so pathfinding is up to the user.
 */
package npc_action;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;

public class Attack extends ActionHandler {
	public void run(Mob mob, Map<String, Object> args) {
		NPC target = (NPC) args.get("target");
		
		if (target.isDead()) return; //Can't attack dead targets.
		if (target.isAttackable(mob) == false) {
			//This generally shouldn't occur, but for future proofing we
			//use this check. isAttackable, for NPC's, currently just checks
			//that the target has the option "Attack", and this script can
			//only be invoked if the target has the option "Attack".
			if(mob instanceof Client){
				((Client) mob).sendMessage("You can't attack that target");
			}
			return;
		}
		mob.getCombat().setTarget(target);
		yield();
	}
}