package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class EmotesInterface extends SideInterface {
	/**
	 * Different animation constants.
	 */
	public final static Animation YES_EMOTE = new Animation(855);
	public final static Animation NO_EMOTE = new Animation(856);
	public final static Animation THINKING = new Animation(857);
	public final static Animation BOW = new Animation(858);
	public final static Animation ANGRY = new Animation(859);
	public final static Animation CRY = new Animation(860);
	public final static Animation LAUGH = new Animation(861);
	public final static Animation CHEER = new Animation(862);
	public final static Animation WAVE = new Animation(863);
	public final static Animation BECKON = new Animation(864);
	public final static Animation CLAP = new Animation(865);
	public final static Animation DANCE = new Animation(866);
	public final static Animation PANIC = new Animation(2105);
	public final static Animation JIG = new Animation(2106);
	public final static Animation SPIN = new Animation(2107);
	public final static Animation HEADBANG = new Animation(2108);
	public final static Animation JOYJUMP = new Animation(2109);
	public final static Animation RASPBERRY = new Animation(2110);
	public final static Animation YAWN = new Animation(2111);
	public final static Animation SALUTE = new Animation(2112);
	public final static Animation SHRUG = new Animation(2113);
	public final static Animation BLOW_KISS = new Animation(1368);
	public final static Animation GLASS_WALL = new Animation(1128);
	public final static Animation LEAN = new Animation(1129);
	public final static Animation CLIMB_ROPE = new Animation(1130);
	public final static Animation GLASS_BOX = new Animation(1131);
	public final static Animation GOBLIN_BOW = new Animation(2127);
	public final static Animation GOBLIN_DANCE = new Animation(2128);
	
	public EmotesInterface(Player p) {
		super(p, (short) 100);
		setChildId(464);
		player.getProtocol().sendConfig(1085, 249852);//Zombie hand
		player.getProtocol().sendConfig(465, -1);//Goblin bow and salute
		player.getProtocol().sendConfig(802, -1);//Idea, stomp, flap, slap head
		player.getProtocol().sendConfig(313, -1);//Glass wall, glass box, climb rope, lean, scared, zombie dance, zombie walk, bunny-hop, skillcape, snowman dance, air quitar, safety first, explore, trick, freeze and melt, give thanks.
		player.getProtocol().sendConfig(2033, 1043648799);//Seal of approval
		player.getProtocol().sendConfig(1921, -893736236);//Puppet master
		player.getProtocol().sendConfig(1404, 123728213);//Around the world in egty days
		player.getProtocol().sendConfig(1842, -1);//Faint
		player.getProtocol().sendConfig(1597, -1);//Dramatic point
		player.getProtocol().sendConfig(1958, 418);//Taskmaster emote (value=total amount of tasks) 
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}
	
}
