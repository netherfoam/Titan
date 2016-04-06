package org.maxgamer.rs.importers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.maxgamer.rs.io.CommentScanner;

public class SpawnsListTxtImport{
	public static void main(String[] args) throws IOException{
		CommentScanner sc = new CommentScanner(new File("data/spawnsList.txt"));
		
		FileOutputStream out = new FileOutputStream(new File("spawnsList.sql"));
		PrintStream ps = new PrintStream(out);
		
		while(sc.hasNextLine()){
			String line = sc.readLine().trim();
			if(line.isEmpty()) continue;
			
			String[] sectors = line.split(" - ");
			if(sectors.length != 2){
				System.out.println("Unrecognised line:");
				System.out.println(line);
			}
			
			int npc_id = Integer.parseInt(sectors[0].trim());
			String[] loc = sectors[1].split(" ");
			int x = Integer.parseInt(loc[0]);
			int y = Integer.parseInt(loc[1]);
			int z = Integer.parseInt(loc[2]);
			
			ps.println("INSERT INTO NPCSpawn (npc_id, x, y, z, map) VALUES (" + npc_id + ", " + x + ", " + y + ", " + z + ", 'mainland');");
		}
		ps.close();
	}
}