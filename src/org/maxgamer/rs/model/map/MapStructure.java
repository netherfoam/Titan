package org.maxgamer.rs.model.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.maxgamer.rs.lib.NotImplementedException;

public class MapStructure {
	public static final String EXTENSION = ".structure";
	
	private static final int RECORD_SIZE = 12; //(byte: cx, byte: cy, byte: cz) x 4
	
	private static final int TYPE_STANDARD = 0;
	private static final int TYPE_DYNAMIC = 1;
	@SuppressWarnings("unused")
	private static final int TYPE_SUBSTANDARD = 2;
	
	public static void save(File folder, WorldMap map) throws IOException{
		File f = new File(folder, map.getName() + EXTENSION);
		
		File parent = f.getAbsoluteFile().getParentFile();
		if(parent.isDirectory() == false){
			parent.mkdirs();
		}
		
		if(f.exists()){
			f.delete();
			f.createNewFile();
		}
		
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		FileChannel channel = raf.getChannel();
		
		if(map instanceof StandardMap){
			ByteBuffer bb = ByteBuffer.allocate(1);
			bb.put((byte) TYPE_STANDARD);
			bb.flip();
			channel.write(bb, 0);
		}
		else if(map instanceof DynamicMap){
			int width = map.width() >> 3;
			int height = map.height() >> 3;
			
			ByteBuffer bb = ByteBuffer.allocate(3);
			bb.put((byte) TYPE_STANDARD);
			bb.put((byte) (width - 1));
			bb.put((byte) (height - 1));
			bb.flip();
			
			channel.write(bb, 0);
			
			bb = ByteBuffer.allocate(RECORD_SIZE);
			
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					
					for(int k = 0; k < 4; k++){
						Chunk c = map.getChunk(i, j, k);
						
						if(c == null){
							bb.put((byte) 0xFF);
							bb.put((byte) 0xFF);
							bb.put((byte) 0xFF);
						}
						else{
							bb.put((byte) c.getCacheX());
							bb.put((byte) c.getCacheY());
							bb.put((byte) c.getCacheZ());
						}
					}
					
					bb.flip();
					channel.write(bb, 3 + (i * width + j) * RECORD_SIZE);
				}
			}
		}
		else{
			throw new NotImplementedException();
		}
		
		channel.close();
		raf.close();
	}
	
	public static MapStructure load(File parent, String name) throws FileNotFoundException{
		return new MapStructure(new File(parent, name + EXTENSION));
	}
	
	private File file;
	private RandomAccessFile raf;
	private FileChannel channel;
	
	public MapStructure(File f) throws FileNotFoundException{
		this.file = f;
		this.raf = new RandomAccessFile(f, "rw");
		this.channel = this.raf.getChannel();
	}
	
	public int type() throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		this.channel.read(buffer, 0);
		buffer.flip();
		int type = buffer.get() & 0xFF;
		return type;
	}
	
	public WorldMap read() throws IOException{
		WorldMap map;
		
		int type = type();
		
		String name = this.file.getName().substring(0, this.file.getName().lastIndexOf('.'));
		
		if(type == TYPE_STANDARD){
			map = new StandardMap(name);
		}
		else if(type == TYPE_DYNAMIC){
			ByteBuffer buffer = ByteBuffer.allocate(2);
			this.channel.read(buffer, 1);
			buffer.flip();
			
			int width = (buffer.get() & 0xFF) + 1;
			int height = (buffer.get() & 0xFF) + 1;
			
			Chunk[][][] chunks = new Chunk[width][height][4];
			for(int i = 0; i < chunks.length; i++){
				for(int j = 0; j < chunks[i].length; j++){
					ByteBuffer bb = ByteBuffer.allocate(RECORD_SIZE);
					this.channel.read(bb, 3 + (i * chunks.length + j) * RECORD_SIZE);
					bb.flip();
					
					for(int k = 0; k < chunks[i][j].length; k++){
						int cx = bb.get() & 0xFF;
						int cy = bb.get() & 0xFF;
						int cz = bb.get() & 0xFF;
						
						if(cx == 0xFF && cy == 0xFF && cz == 0xFF){
							//Null chunk here
							continue;
						}
						
						assert cz >= 0 && cz < 4 : "Z axis must be 0-3 inclusive, given " + cz;
						
						chunks[i][j][k] = new Chunk(cx, cy, cz);
					}
				}
			}
			map = new DynamicMap(name, chunks);
		}
		else { /* TODO: Substandardmap */
			throw new NotImplementedException();
		}
		
		return map;
	}
}
