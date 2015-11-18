package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.PersonaModel;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.structure.ArgumentalRunnable;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 * @author Albert Beaupre
 */
public class EmotesInterface extends SideInterface {
	
	/**
	 * TODO correct delays
	 */
	public enum Emote {
		YES(new Animation(855), 2, 1, null),
		NO(new Animation(856), 3, 1, null),
		BOW(new Animation(858), 4, 1, null),
		ANGRY(new Animation(859), 5, 1, null),
		THINKING(new Animation(857), 6, 1, null),
		WAVE(new Animation(863), 7, 2, null),
		SHRUG(new Animation(2113), 8, 1, null),
		CHEER(new Animation(862), 9, 2, null),
		BECKON(new Animation(864), 10, 1, null),
		JOYJUMP(new Animation(2109), 11, 1, null),
		LAUGH(new Animation(861), 12, 1, null),
		YAWN(new Animation(2111), 13, 1, null),
		DANCE(new Animation(866), 14, 1, null),
		JIG(new Animation(2106), 15, 1, null),
		SPIN(new Animation(2107), 16, 1, null),
		HEADBANG(new Animation(2108), 17, 1, null),
		CRY(new Animation(860), 18, 1, null),
		BLOW_KISS(new Animation(1368), 19, 1, null),
		PANIC(new Animation(2105), 20, 1, null),
		RASPBERRY(new Animation(2110), 21, 1, null),
		CLAP(new Animation(865), 22, 1, null),
		SALUTE(new Animation(2112), 23, 1, null),
		GOBLIN_BOW(new Animation(2127), 24, 1, null),
		GOBLIN_DANCE(new Animation(2128), 25, 1, null),
		GLASS_BOX(new Animation(1131), 26, 1, null),
		CLIMB_ROPE(new Animation(1130), 27, 1, null),
		LEAN(new Animation(1129), 28, 1, null),
		GLASS_WALL(new Animation(1128), 29, 1, null),
		SEAL_OF_APPROVAL(new Animation(15104), 52, 9, new ArgumentalRunnable() {
			@Override
			public void run(Object... args) {
				Mob m = (Mob) args[0];

				m.graphics(1287);
				new Tickable() {
					int ticked = 0;

					@Override
					public void tick() {
						if (m instanceof Persona) {
							PersonaModel model = (PersonaModel) m.getModel();
							if (ticked == 0) {
								model.setNpcModelId(13255 + Erratic.nextInt(0, 6));
								model.setChanged(true);
							} else if (ticked == 2) {
								m.animate(15108);
							} else if (ticked == 5) {
								m.animate(15105);
								m.graphics(1287);
								model.setNpcModelId(-1);
								model.setChanged(true);
							}
						}
						if (ticked++ < 6)
							queue(1);
					}
				}.queue(2);
			}
		});

		private final Animation animation;
		private final int buttonId, ticks;
		private final ArgumentalRunnable additionalAction;

		Emote(Animation animation, int buttonId, int ticks, ArgumentalRunnable additionalAction) {
			this.animation = animation;
			this.buttonId = buttonId;
			this.ticks = ticks;
			this.additionalAction = additionalAction;
		}

		public Animation getAnimation() {
			return animation;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getTicks() {
			return ticks;
		}

		public ArgumentalRunnable getAdditionalAction() {
			return additionalAction;
		}

	}

	public final class EmoteAction extends Action {

		private final Emote emote;

		public EmoteAction(Mob mob, Emote emote) {
			super(mob);
			this.emote = emote;
		}

		@Override
		protected void run() throws SuspendExecution {
			mob.animate(emote.getAnimation(), 0);
			if (emote.getAdditionalAction() != null)
				emote.getAdditionalAction().run(mob);
			wait(emote.getTicks());
		}

		@Override
		protected void onCancel() {

		}

		@Override
		protected boolean isCancellable() {
			return false;
		}

	}

	public EmotesInterface(Player p) {
		super(p, (short) 100);
		setChildId(464);
		player.getProtocol().sendConfig(1085, 249852);// Zombie hand
		player.getProtocol().sendConfig(465, -1);// Goblin bow and salute
		player.getProtocol().sendConfig(802, -1);// Idea, stomp, flap, slap head
		player.getProtocol().sendConfig(313, -1);// Glass wall, glass box, climb rope, lean, scared, zombie dance, zombie walk, bunny-hop, skillcape,
													// snowman dance, air quitar, safety first, explore, trick, freeze and melt, give thanks.
		player.getProtocol().sendConfig(2033, 1043648799);// Seal of approval
		player.getProtocol().sendConfig(1921, -893736236);// Puppet master
		player.getProtocol().sendConfig(1404, 123728213);// Around the world in egty days
		player.getProtocol().sendConfig(1842, -1);// Faint
		player.getProtocol().sendConfig(1597, -1);// Dramatic point
		player.getProtocol().sendConfig(1958, 418);// Taskmaster emote (value=total amount of tasks)
	}

	@Override
	public boolean isMobile() {
		return true;
	}

	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		Player m = getPlayer();

		if (option == 0) {
			if (!m.getActions().isEmpty() && m.getActions().getList().get(0) instanceof EmoteAction) {
				m.sendMessage("You're already doing an emote!");
				return;
			}

			m.getActions().clear();
			for (Emote emote : Emote.values()) {
				if (emote.getButtonId() == buttonId) {
					m.getActions().queue(new EmoteAction(m, emote));
					break;
				}
			}
		}
	}
}
