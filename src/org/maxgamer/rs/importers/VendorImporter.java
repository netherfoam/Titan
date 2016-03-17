package org.maxgamer.rs.importers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class VendorImporter{
	public static void main(String[] args) throws IOException {
		new VendorImporter().run();
	}
	
	public void run() throws IOException {
		PrintStream out = new PrintStream("out.sql");
		Scanner sc = new Scanner(new File("vendors.txt"));
		sc.nextLine(); // Skip
		
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			if(line.isEmpty()) continue;
			
			String[] parts = line.split(" - ");
			
			String[] kmg = parts[0].split(" ");
			int id = Integer.parseInt(kmg[0]);
			int currency = Integer.parseInt(kmg[1]);
			@SuppressWarnings("unused")
			boolean general = Boolean.parseBoolean(kmg[2]);
			
			String name = parts[1];
			for(int i = 2; i < parts.length - 1; i++) name += parts[i];
			
			out.println("INSERT INTO Vendor (id, flags, name, currency) VALUES (" + id + ", " + 1 + ", '" + name.replaceAll("'", "\\\\'") + "', " + currency + ");");
			
			String[] contents = parts[parts.length - 1].split(" ");
			
			int i = 0;
			while(i < contents.length){
				int itemId = Integer.parseInt(contents[i++]);
				int quantity = Integer.parseInt(contents[i++]);
				
				out.println("INSERT INTO VendorItem (vendor_id, item_id, amount) VALUES (" + id + ", " + itemId + ", " + quantity + ");");
			}
		}
		out.close();
		sc.close();
	}
}