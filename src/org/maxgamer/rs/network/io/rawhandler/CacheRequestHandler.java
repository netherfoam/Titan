package org.maxgamer.rs.network.io.rawhandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

import com.mchange.v3.filecache.FileNotCachedException;

/**
 * @author netherfoam
 */
public class CacheRequestHandler extends RawHandler {
	private HashMap<Integer, Integer> requests = new HashMap<Integer, Integer>(2048); //HashSet of all files requested
	
	public CacheRequestHandler(Session s) {
		super(s);
	}
	
	int running = 0;
	
	@Override
	public void handle(RSByteBuffer b) {
		while (b.available() >= 4) {
			//We read the data in first. This data is guaranteed to contain 4 bytes.
			//The first byte is the opcode, the rest can vary but there must be 3 bytes
			//following.
			byte[] data = new byte[4];
			b.read(data);
			//InputStreamWrapper in = new InputStreamWrapper(data);
			
			final int opcode = data[0] & 0xFF; //in.readByte() & 0xFF;
			switch (opcode) {
				case 0: //Standard file request
				case 1: //Priority file request (index file)
					
					//TODO: Check this works.
					final int idx = data[1] & 0xFF; //in.readByte() & 0xFF;
					final int file = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF); //in.readShort() & 0xFFFF;
					
					int id = (idx << 24) | file;
					Integer old = requests.get(id);
					Integer count;
					if (old == null) count = Integer.valueOf(1);
					else count = Integer.valueOf(old + 1);
					requests.put(id, count);
					
					if (count > 1) {
						//TODO: Protect the server against this kind of malicious request!
						//This is legitimately required for some files, and the client won't continue
						//without them.
						
						//Some of these files can legitimately be requested twice and must be sent twice otherwise the client freezes.
						//But no files are requested 3 times.
						//Log.debug(getSession() + " requested file from cache (" + idx + ", " + file + ") but they have already requested that file.");
						//getSession().close();
						//in.close();
						//in.close();
						//return;
					}
					
					Runnable r = new Runnable() {
						@Override
						public void run() {
							synchronized (CacheRequestHandler.this) {
								running++;
								//Log.debug("ASync start count " + running);
							}
							try {
								//Log.debug("Sending file " + idx + ", " + file + ", op " + opcode);
								ByteBuffer response = Core.getCache().createResponse(idx, file, opcode);
								//ByteBuffer response = Core.getCache().getUpdateServer().update(idx, file, opcode);
								getSession().write(response);
							}
							catch (FileNotCachedException e) {
								Log.debug(getSession() + " requested file " + idx + ", " + file + " but that file was not found.");
								getSession().close(false);
							}
							catch (IOException e) {
								//Client closed their connection
								//This frequently happens when players are still
								//streaming the cache.
								getSession().close(false);
								e.printStackTrace();
								Log.debug(getSession() + " requested file " + idx + ", " + file + " but there was an IO error.");
							}
							catch (Exception e) {
								e.printStackTrace();
								Log.warning("Failed to generate cache response for file request IDX " + idx + ", FileID " + file + ", Opcode: " + opcode);
								getSession().close(false);
							}
							synchronized (CacheRequestHandler.this) {
								running--;
								//Log.debug("ASync Stop count " + running);
							}
						}
					};
					
					if (opcode == 0) {
						//Log.debug("ASync Start...");
						Core.submit(r, true);
						//Log.debug("ASync Stop...");
					}
					else {
						//Priority. Do it now.
						//Log.debug("Start");
						r.run();
						//Log.debug("Stop");
					}
					break;
				
				case 2: //The client is connected
					break;
				
				case 3: //the client is logged out
					break;
				
				case 4: //A new encryption byte is being used
					Log.info("Connection encryption byte set");
					/* byte encryptionByte = *///in.readByte();
					//The other 2 bytes are padding
					//getSession().setEncryption(encryptionByte);
					break;
				
				case 6: //Connection is being initiated
					break;
				
				case 7: //connection should be closed
					getSession().close(true);
					break;
				
				default:
					break;
			}
		}
	}
}