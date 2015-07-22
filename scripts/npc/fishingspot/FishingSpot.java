/**
 * 
 */
package npc.fishingspot;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Only;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */

public class FishingSpot extends ActionHandler {
	private enum Fish {
	    SHRIMP(317, 10, 1),
	    SARDINE(327, 20, 5),
	    HERRING(345, 30, 10),
	    ANCHOVIES(321, 40, 15),
	    MACKEREL(353, 20, 16),
	    TROUT(335, 50, 20),
	    COD(341, 45, 23),
	    PIKE(349, 60, 25),
	    SALMON(331, 70, 30),
	    TUNA(359, 80, 35),
	    LOBSTER(377, 90, 40),
	    BASS(363, 100, 46),
	    SWORDFISH(371, 100, 50),
	    MONKFISH(7944, 120, 62),
	    SHARK(383, 110, 76),
	    SEA_TURTLE(395, 38, 79),
	    MANTA_RAY(389, 46, 81),
	    CAVE_FISH(15264, 300, 85);

	    private final int id;
	    private final int xp;
	    private final int level;

	    private Fish(int id, int xp, int level) {
	        this.id = id;
	        this.xp = xp;
	        this.level = level;
	    }
	}
	
	private enum Type {
	    NET_NET_AND_BAIT("Net", 316, new Animation(621), 303, -1, Fish.SHRIMP, Fish.ANCHOVIES),
	    BAIT_NET_AND_BAIT("Bait", 316, new Animation(622), 307, 313, Fish.SARDINE, Fish.HERRING),
	    LURE_LURE_AND_BAIT("Lure", 317, new Animation(622), 309, 314, Fish.TROUT, Fish.SALMON),
	    BAIT_LURE_AND_BAIT("Bait", 317, new Animation(622), 307, 313, Fish.PIKE, Fish.CAVE_FISH),
	    CAGE_CAGE_AND_HARPOON("Cage", 321, new Animation(619), 301, -1, Fish.LOBSTER),
	    HARPOON_CAGE_AND_HARPOON("Harpoon", 321, new Animation(618), 311, -1, Fish.TUNA, Fish.SWORDFISH, Fish.MONKFISH),
	    BIG_NET_NET_AND_HARPOON("Net", 322, new Animation(621), 305, -1, Fish.MACKEREL, Fish.COD, Fish.BASS),
	    HARPOON_NET_AND_HARPOON("Harpoon", 322, new Animation(618), 311, -1, Fish.SHARK);
	    
	    public static Type forNPC(int id, String option){
	    	//TODO: Small optimisation available here
	    	for(Type t : Type.values()){
	    		if(t.npcId == id && t.option.equalsIgnoreCase(option)){
	    			return t;
	    		}
	    	}
	    	return null;
	    }

	    private final int npcId;
	    private final int item;
	    private final int bait;
	    private final String option;
	    private final Animation animation;
	    private final Fish[] fish;

	    private Type(String option, int npcId, Animation animation, int item, int bait, Fish... fish) {
	        this.npcId = npcId;
	        this.item = item;
	        this.bait = bait;
	        this.fish = fish;
	        this.animation = animation;
	        this.option = option;
	    }
	}
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		NPC target = (NPC) args.get("target");
		String option = (String) args.get("option");
		
		Type t = Type.forNPC(target.getId(), option);
		if(t == null){
			if(mob instanceof Client){
				((Client) mob).sendMessage("Unimplemented fish type");
			}
			return;
		}
	}
}
