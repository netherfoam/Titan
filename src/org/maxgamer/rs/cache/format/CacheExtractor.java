package org.maxgamer.rs.cache.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.maxgamer.rs.cache.Archive;
import org.maxgamer.rs.cache.Cache;
import org.maxgamer.rs.cache.CacheFile;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;

public class CacheExtractor{
	public static void main(String[] args) throws IOException{
		Cache c = new Cache();
		c.load(new File("cache"));
		CacheExtractor extractor = new CacheExtractor(c);
		extractor.add(new CS2Factory());
		extractor.add(new GameObjectFactory());
		
		extractor.run(new File("extract_types"));
	}
	
	private Cache cache;
	private ArrayList<FormatFactory<?>> formats = new ArrayList<FormatFactory<?>>();
	
	public CacheExtractor(Cache cache){
		this.cache = cache;
	}
	
	public void run(File folder){
		int failed = 0, success = 0;
		for(int idxNum = 1; idxNum < cache.getIDXCount(); idxNum++){
			ReferenceTable rt = cache.getReferenceTable(idxNum);
			if(rt == null) continue;
			
			File idxFolder = new File(folder, "idx"+ idxNum);
			idxFolder.mkdirs();
			
			for(Reference r : rt.getReferences()){
				CacheFile f;
				try{
					f = cache.getFile(idxNum, r.getId());
				}
				catch(Exception e){
					System.out.println("File in IDX " + idxNum + " failed to be fetched. (Encrypted?) File Id: " + r.getId() + ", " + e.getClass().getSimpleName());
					failed++;
					continue;
				}
				success++;
				
				if(r.getChildCount() > 1){
					/* We're extracting a folder */
					try{
						Archive a = Archive.decode(r, f);
						
						File archiveFolder = new File(idxFolder, "file" + r.getId());
						archiveFolder.mkdirs();
						
						for(Entry<Integer, ByteBuffer> e : a.getAll().entrySet()){
							
							File dest;
							ByteBuffer bb = e.getValue();
							bb.mark();
							FormatFactory<?> fac = getFormat(bb);
							if(fac != null){
								dest = new File(archiveFolder, "" + e.getKey() + "." + fac.getExtension());
							}
							else{
								dest = new File(archiveFolder, "" + e.getKey() + ".dat");
							}
							dest.createNewFile();
							bb.reset();
							
							FileOutputStream out = new FileOutputStream(dest);
							byte[] data = new byte[bb.remaining()];
							bb.get(data);
							out.write(data);
							out.close();
						}
					}
					catch(IOException e){
						continue;
					}
					
				}
				else{
					try{
						/* We have no child files, so we're a main file */
						File dest;
						ByteBuffer bb = f.getData();
						FormatFactory<?> fac = getFormat(bb);
						if(fac != null){
							dest = new File(idxFolder, "file" + r.getId() + "." + fac.getExtension());
						}
						else{
							dest = new File(idxFolder, "file" + r.getId() + ".dat");
						}
						dest.createNewFile();
						
						FileOutputStream out = new FileOutputStream(dest);
						byte[] data = new byte[bb.remaining()];
						bb.get(data);
						out.write(data);
						out.close();
					}
					catch(IOException e){
						e.printStackTrace();
						continue;
					}
				}
			}
		}
		System.out.println("Failed: " + failed + ", Succeeded: " + success);
		
	}
	
	private FormatFactory<?> getFormat(ByteBuffer bb){
		int start = bb.position();
		for(FormatFactory<?> factory : formats){
			bb.position(start);
			
			try{
				factory.decode(bb);
				if(bb.remaining() != 0) continue; //Didn't use all of the data
				bb.position(start);
				
				return factory; //This factory can read the file.
			}
			catch(Throwable t){
				continue;
			}
		}
		bb.position(start);
		
		return null;
	}
	
	public ArrayList<FormatFactory<?>> getFormats(){
		return new ArrayList<FormatFactory<?>>(formats);
	}
	
	public void add(FormatFactory<?> fac){
		formats.add(fac);
	}
	
	public void remove(FormatFactory<?> fac){
		formats.remove(fac);
	}
	
	public boolean isRegistered(FormatFactory<?> fac){
		return formats.contains(fac);
	}
}