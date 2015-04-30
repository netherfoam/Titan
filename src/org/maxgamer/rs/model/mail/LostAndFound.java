package org.maxgamer.rs.model.mail;

import java.util.Iterator;
import java.util.LinkedList;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.structure.configs.ConfigSection;

/**
 * A class that allows to store items which are "Lost" in the server somewhere
 * due to a peculiar bug. This class stores whatever items were guaranteed to
 * have not been given to the player when they should have, and returns them to
 * the player.
 * @author netherfoam
 */
public class LostAndFound implements YMLSerializable {
	/**
	 * The Persona who owns this Lost&Found
	 */
	private Persona owner;
	
	/**
	 * The list of items in order of when they were lost. Eg, oldest lost items
	 * will be at the start of the list, while most recently lost items will be
	 * at the end of the list.
	 */
	private LinkedList<ItemStack> mail = new LinkedList<>();
	
	private Tickable replacer = null;
	
	/**
	 * Constructs a new Lost&Found for the given owner
	 * @param owner
	 */
	public LostAndFound(Persona owner) {
		this.owner = owner;
	}
	
	/**
	 * The owner of this lost and found
	 * @return
	 */
	public Persona getOwner() {
		return owner;
	}
	
	/**
	 * Returns true if this Lost&Found has no items left lost
	 * @return true if this Lost&Found has no items left lost
	 */
	public boolean isEmpty() {
		return mail.isEmpty();
	}
	
	/**
	 * Adds all of the items to this Lost&Found. If any of the items are null,
	 * they are discarded.
	 * @param items the items to add to this Lost&Found
	 */
	public void add(ItemStack... items) {
		if (items == null) {
			throw new NullPointerException("May not add NULL ItemStack[] to Lost&Found");
		}
		
		if (replacer == null) {
			//Attempt to restore any items from the Lost&Found.
			replacer = new Tickable() {
				@Override
				public void tick() {
					if (owner.isDestroyed()) {
						return;
					}
					
					if (isEmpty() == false) {
						find();
						if (isEmpty() == false) {
							this.queue(100);
							return;
						}
					}
					
					replacer = null; //Cancelled.
				}
			};
			replacer.queue(100);
		}
		
		for (ItemStack stack : items) {
			if (stack == null) continue;
			mail.add(stack);
		}
	}
	
	/**
	 * Attempts to return the items in this Lost&Found to the owner's bank. If
	 * successful, it will notify the player.
	 * @return true if there are items still in the lost and found, false if
	 *         this lost and found is now empty.
	 */
	public boolean find() {
		if (mail.isEmpty()) {
			return false;
		}
		
		Iterator<ItemStack> mit = mail.iterator();
		
		while (mit.hasNext()) {
			ItemStack item = mit.next();
			
			try {
				getOwner().getBank().add(item);
				mit.remove(); //Item added successfully
				if (getOwner() instanceof Player) {
					Player p = (Player) getOwner();
					p.sendMessage("Lost & Found: Returned " + item.getName() + " x" + item.getAmount() + " to your bank. Apologies for the inconvenience!");
				}
			}
			catch (ContainerException e) {
				//Item failed to be added, the owner hasn't got the space for it.
			}
		}
		
		if (mail.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public ConfigSection serialize() {
		ConfigSection s = new ConfigSection();
		s.set("size", mail.size());
		for (int i = 0; i < mail.size(); i++) {
			s.set("" + i, mail.get(i).serialize());
		}
		
		return s;
	}
	
	@Override
	public void deserialize(ConfigSection map) {
		int size = map.getInt("size", 0);
		for (int i = 0; i < size; i++) {
			ConfigSection cs = map.getSection("" + i, null);
			if (cs == null) {
				continue;
			}
			
			ItemStack item = ItemStack.create(cs);
			if (item == null) {
				continue;
			}
			
			mail.add(item);
		}
	}
}