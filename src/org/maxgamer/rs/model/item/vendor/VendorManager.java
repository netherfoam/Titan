package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.item.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class VendorManager{
	private HashMap<String, VendorContainer> names = new HashMap<String, VendorContainer>();
	private HashMap<Integer, VendorContainer> ids = new HashMap<Integer, VendorContainer>();
	
	public VendorManager(){
		
	}
	
	public void init() throws SQLException {
		PreparedStatement ps = Core.getWorldDatabase().getConnection().prepareStatement("SELECT * FROM Vendor");
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			try{
				PreparedStatement psi = Core.getWorldDatabase().getConnection().prepareStatement("SELECT * FROM VendorItem WHERE vendor_id = ?");
				psi.setInt(1, rs.getInt("id"));
				ResultSet rsi = psi.executeQuery();
				
				ArrayList<ItemStack> sales = new ArrayList<ItemStack>();
				while(rsi.next()){
					ItemStack item = ItemStack.create(rsi.getInt("item_id"), rsi.getInt("amount"));
					if(item == null){
						item = ItemStack.createEmpty(rsi.getInt("item_id"), 0);
					}
					sales.add(item);
				}
				
				VendorContainer contents = new VendorContainer(rs.getString("name"), rs.getInt("flags"), sales.toArray(new ItemStack[sales.size()]), rs.getInt("currency"));
				names.put(contents.getName(), contents);
				ids.put(rs.getInt("id"), contents);
				rsi.close();
				psi.close();
			}
			catch(Exception e){
				// Such as SQL exception
				if(e instanceof RuntimeException == false) throw e;
				
				e.printStackTrace();
			}
		}
		rs.close();
		ps.close();
	}
	
	public void clear(){
		names.clear();
		ids.clear();
	}
	
	public VendorContainer get(String name){
		return names.get(name);
	}
	
	public VendorContainer get(int id){
		return ids.get(id);
	}
	
	public void set(int id, VendorContainer container){
		this.names.put(container.getName(), container);
		this.ids.put(id, container);
	}
}