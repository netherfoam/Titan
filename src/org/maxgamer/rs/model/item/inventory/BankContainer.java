package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author netherfoam
 */
public class BankContainer extends Container {
	public static final int SIZE = 516;
	public static final int TABS = 11;
	
	private ItemStack[] items = new ItemStack[SIZE];
	
	public BankContainer() {
		super(StackType.ALWAYS);
	}
	
	@Override
	protected void setItem(int slot, ItemStack item) {
		items[slot] = item;
	}
	
	@Override
	public ItemStack get(int slot) {
		return items[slot];
	}
	
	@Override
	public void shift(){
		super.shift();
	}
	
	@Override
	public int getSize() {
		return items.length;
	}
	
	@Override
	public void deserialize(ConfigSection s){
		// Since banks should be shifted to fill space, this is an easy sanity check
		super.deserialize(s);
		this.shift();
	}
	
}