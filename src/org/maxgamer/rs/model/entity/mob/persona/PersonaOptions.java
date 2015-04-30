package org.maxgamer.rs.model.entity.mob.persona;

import java.util.Arrays;

import org.maxgamer.rs.interfaces.impl.primary.TradeInterface;
import org.maxgamer.rs.model.action.FriendFollow;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.GenericContainer;
import org.maxgamer.rs.model.item.inventory.Inventory;
import org.maxgamer.rs.model.item.inventory.StackType;
import org.maxgamer.rs.model.map.path.AStar;

/**
 * @author netherfoam
 */
public class PersonaOptions {
	/**
	 * When the player wishes to follow the target.
	 */
	public static final PersonaOption FOLLOW = new PersonaOption("Follow") {
		@Override
		public void run(Persona clicker, Persona target) {
			clicker.getActions().clear(); //End all actions which can be ended (Eg fighting, trading) except for ones that can't (Eg being stunned)
			
			FriendFollow f = new FriendFollow(clicker, target, 1, 12, new AStar(4));
			clicker.getActions().queue(f);
		}
	};
	
	/**
	 * When the player wishes to attack the target
	 */
	public static final PersonaOption ATTACK = new PersonaOption("Attack") {
		@Override
		public void run(Persona clicker, Persona target) {
			if (target.isDead() || target.isHidden()) return; //Can't attack dead/hidden players
			clicker.getCombat().setTarget(target);
		}
	};
	
	public static final PersonaOption INSPECT = new PersonaOption("Inspect") {
		@Override
		public void run(Persona c, Persona t) {
			if (c instanceof Player) {
				Player p = (Player) c;
				
				p.sendMessage("Inspection: " + t + " Health: " + t.getHealth() + "/" + t.getMaxHealth());
				p.sendMessage("In Combat: " + t.getCombat().isInCombat() + " Damage Taken: " + t.getCombat().getTotal(DamageType.values()));
				p.sendMessage("ActionQueue: " + t.getActions());
				p.sendMessage("Rights: " + t.getRights() + ", Location: (" + t.getLocation().x + ", " + t.getLocation().y + ")");
				p.sendMessage("Inventory " + t.getInventory().getTakenSlots() + "/" + t.getInventory().getSize());
			}
		}
	};
	
	/**
	 * When a player wishes to trade items.
	 */
	public static final PersonaOption TRADE = new PersonaOption("Trade with") {
		@Override
		public void run(final Persona player, final Persona partner) {
			//TODO: Check trade request
			if (player instanceof Player == false) {
				//TODO: Surely there's a way to do this?
				throw new IllegalArgumentException("Trade may not be performed by a non-player");
			}
			if (partner instanceof Player == false) {
				((Player) player).sendMessage("You can't trade with AI players.");
				return;
			}
			
			Container mine = new GenericContainer(Inventory.SIZE, StackType.NORMAL);
			Container yours = new GenericContainer(Inventory.SIZE, StackType.NORMAL);
			
			Player p1 = (Player) player;
			Player p2 = (Player) partner;
			
			TradeInterface myInterf = new TradeInterface(p1, mine, yours, p2);
			TradeInterface yourInterf = new TradeInterface(p2, yours, mine, p1);
			
			p1.getWindow().open(myInterf);
			p2.getWindow().open(yourInterf);
		}
	};
	
	/**
	 * Used for duel arenas, challenging a player to a duel.
	 */
	public static final PersonaOption CHALLENGE = new PersonaOption("Challenge") {
		@Override
		public void run(final Persona player, final Persona other) {
			//TODO
			/*
			 * player.turnTo(other); if (!World.getWorld().doPath(new
			 * DefaultPathFinder(), player, other.getLocation().getX(),
			 * other.getLocation().getY(), false, false).isRouteFound()) {
			 * player.sendMessage("I can't reach that!"); return; } else {
			 * Following.combatFollow(player, other); }
			 * 
			 * World.getWorld().submitAreaEvent(player, new
			 * CoordinateEvent(player, other.getLocation().getX(),
			 * other.getLocation().getY(), other.size(), other.size()) {
			 * 
			 * @Override public void execute() { if
			 * (other.getAttribute("didRequestDuel") == Boolean.TRUE && ((Short)
			 * other.getAttribute("duelWithIndex") == player.getIndex())) {
			 * player.addInterface(new DuelInterface(player, other));
			 * other.addInterface(new DuelInterface(other, player));
			 * other.removeAttribute("didRequestDuel");
			 * other.removeAttribute("duelWithIndex"); } else {
			 * player.addInterface(new DuelRequestInterface(player, other)); } }
			 * });
			 */
		}
	};
	
	/**
	 * Substituting this will remove the option instead of adding it.
	 */
	public static final PersonaOption NULL = new PersonaOption("null") {
		@Override
		public void run(Persona clicker, Persona target) {
		} //Best of luck clicking this one.
		
	};
	
	//Client shows up to 8 options, plus "Walk here".
	public static final byte MAX_OPTIONS = 8;
	
	protected Persona player;
	/** The options available for this player to click. */
	protected final PersonaOption[] options = new PersonaOption[MAX_OPTIONS];
	/** True for above 'Walk here', false for below it */
	protected final boolean[] tops = new boolean[MAX_OPTIONS];
	
	public PersonaOptions(Persona player) {
		if (player == null) {
			throw new NullPointerException("Persona may not be null");
		}
		this.player = player;
	}
	
	/**
	 * Returns true if the given option is currently available to the owner of
	 * these options
	 * @param option the option to search for, may be null
	 * @return true if it is contained, false if it isn't.
	 */
	public boolean contains(PersonaOption option) {
		for (int i = 0; i < options.length; i++) {
			if (options[i] == option) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Invoked when this player clicks an option.
	 * @param target The player they interacted with
	 * @param index The option number they clicked, 1-8.
	 */
	public void clickOption(Persona target, int index) {
		index--;
		PersonaOption selected = options[index];
		if (selected != null) {
			selected.run(player, target);
		}
	}
	
	/**
	 * Returns the option at the given index.
	 * @param position The position
	 * @return The option
	 */
	public PersonaOption get(int position) {
		return options[--position];
	}
	
	public PersonaOption get(String alias) {
		for (PersonaOption option : options) {
			if (option == null) continue;
			String s = option.getText();
			if (s == alias) return option; //They may have requested a 'null' string
			if (s == null) continue; //No alias.
			
			if (s.equalsIgnoreCase(alias)) return option;
		}
		
		return null;
	}
	
	public void add(PersonaOption option, boolean aboveWalk) {
		if (option == null) return; //Nope.
		
		//Check the option doesn't already exist.
		for (int i = 0; i < options.length; i++) {
			if (options[i] == option) {
				if (tops[i] == aboveWalk) {
					//That option is already there.
					return;
				}
				else {
					//That option is already there, but should be on top.
					this.set(i + 1, option, aboveWalk);
					return;
				}
				
			}
		}
		
		//By now, we know that the option is not already available.
		for (int i = 0; i < options.length; i++) {
			if (options[i] == null || options[i] == PersonaOptions.NULL) {
				this.set(i + 1, option, aboveWalk);
				return;
			}
		}
		
		throw new RuntimeException("PlayerOptions can have a maximum of eight values, plus \"Walk here\" only. Currently they are: " + Arrays.toString(options));
	}
	
	public void remove(PersonaOption option) {
		for (int i = 0; i < options.length; i++) {
			if (options[i] == option) {
				this.set(i + 1, PersonaOptions.NULL, false);
			}
		}
	}
	
	/**
	 * Sets a menu option for when a player right clicks on another player.
	 * @param position The position in the menu. Items are sorted by this
	 *        number, lowest at the top.
	 * @param option Contains the display text of this option, and the method to
	 *        run if it is clicked.
	 * @param aboveWalk If you would like the option to be above the walk
	 *        option, or below it.
	 */
	public void set(int position, PersonaOption option, boolean aboveWalk) {
		position--;
		
		if (option == null) {
			//Special case, this deletes it when we send it.
			option = PersonaOptions.NULL;
		}
		
		options[position] = option;
		tops[position] = aboveWalk;
	}
}