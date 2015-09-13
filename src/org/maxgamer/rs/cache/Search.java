package org.maxgamer.rs.cache;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.maxgamer.rs.cache.reference.ChildReference;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;

public class Search{
	private static Cache cache;
	public static void main(String[] args) throws IOException{
		cache = new Cache();
		cache.load(new File("cache"));
		
		int maxSize = 0;
		String identifier = "";
		for(int idx = 0; idx < cache.getIDXCount(); idx++){
			ReferenceTable table = cache.getReferenceTable(idx);
			if(table == null) continue;
			for(Reference ref : table.getReferences()){
				try{
					CacheFile file = cache.getFile(idx, ref.getId());
					int size = file.getSize();
					if(size > maxSize){
						maxSize = size;
						identifier = "IDX " + idx + ", FileId: " + ref.getId();
					}
				}
				catch(IOException e){};
			}
			System.out.println("Finished searching IDX " + idx);
			System.out.println("Biggest file: " + identifier + " @" + maxSize);
		}
		System.out.println("Overall Biggest file: " + identifier + " @" + maxSize);
		
		//search("Scorpion Catcher");
	}
	
	public static void search(String term) throws IOException{
		for(int idx = 0; idx < cache.getIDXCount(); idx++){
			ReferenceTable table = cache.getReferenceTable(idx);
			for(Reference ref : table.getReferences()){
				if(ref.getChildCount() > 1){
					//Archive
					Archive archive = cache.getArchive(idx, ref.getId());
					for(ChildReference child : ref.getChildren()){
						ByteBuffer bb = archive.get(child.getId());
						search(term, "idx " + idx + ", archive " + ref.getId() + ", child " + child.getId(), bb);
					}
				}
				else{
					//Standard file
					ByteBuffer bb = cache.getFile(idx, ref.getId()).getData();
					search(term, "idx " + idx + ", file " + ref.getId(), bb);
				}
			}
			System.out.println("Finished searching IDX " + idx);
		}
	}
	
	public static void search(String term, String name, ByteBuffer bb){
		StringBuilder sb = new StringBuilder(bb.remaining());
		while(bb.hasRemaining()) sb.append((char) bb.get());
		String s = sb.toString();
		int index = s.indexOf(term);
		if(index >= 0){
			System.out.println(name + " contained search at index " + index + "!");
			System.out.println(s);
		}
	}
}