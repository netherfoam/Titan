package org.maxgamer.rs.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.maxgamer.rs.cache.reference.ReferenceTable;

/**
 * Represents an individual file in the cache. Eg map region files.
 * @author netherfoam
 */
public class CacheFile{
	/**
	 * Reads a raw file from the cache. This is in a certain format and may be compressed or encrypted partially.
	 * @param idx the IDX to read from. Used for checking purposes
	 * @param index the index channel from disk
	 * @param data the data channel from disk
	 * @param fileId the file's unique number inside the idx value
	 * @return the bytebuffer not null
	 * @throws IOException if there is an IO error
	 * @throws FileNotFoundException if the file is not found
	 */
	public static ByteBuffer getRaw(int idx, FileChannel index, FileChannel data, int fileId) throws IOException{
		int size;
		if((fileId + 1) * ReferenceTable.IDX_BLOCK_LEN > index.size()){
			/* End of file, this is a bad fileId requested */
			/* Note that this doesn't catch all no such file requests. */
			throw new FileNotFoundException();
		}
		
		/* Our temporary buffer. This is going to use a maximum of TOTAL_BLOCK_LEN bytes */
		ByteBuffer bb = ByteBuffer.allocate(ReferenceTable.TOTAL_BLOCK_LEN);
		
		/* We are reading 6 bytes (2x TriByte) here */
		bb.limit(ReferenceTable.IDX_BLOCK_LEN);
		if(index.read(bb, fileId * ReferenceTable.IDX_BLOCK_LEN) != ReferenceTable.IDX_BLOCK_LEN){
			/* TODO: Should this be thrown ? */
			throw new IOException("End of file channel. Could not read " + ReferenceTable.TOTAL_BLOCK_LEN + " bytes");
		}
		bb.flip();
		
		/* The size of the whole file in bytes */
		size = getTriByte(bb);
		
		/* Files are split into 'blocks' across the whole file. Each block is
		 * TOTAL_BLOCK_LEN bytes. Eg a single file can be spread across multiple
		 * blocks, which could be littered throughout the cache. This is the 
		 * location of the first block. This may be zero, in which case the file
		 * does not exist.
		 */
		int startBlock = getTriByte(bb);
		
		/* Error checking */
		if(size < 0 || size > 1000000){
			throw new IOException("Bad size. Given " + size);
		}
		
		if(startBlock == 0) {
			throw new FileNotFoundException();
		}
		else if(startBlock < 0 || startBlock > data.size() / ReferenceTable.TOTAL_BLOCK_LEN){
			throw new IOException("Bad block. Given " + startBlock + ", max " + (data.size() / ReferenceTable.TOTAL_BLOCK_LEN));
		}
		
		ByteBuffer payload = ByteBuffer.allocate(size);
		int chunk = 0;
		int block = startBlock;
		
		while(payload.remaining() > 0){
			/* The number of bytes we want to read */
			int blockSize = Math.min(ReferenceTable.BLOCK_LEN, payload.remaining());
			
			/* This also flips our buffer */
			bb.position(0);
			/* Reset the temporary buffer so that it can only store exactly the number of bytes we want */
			bb.limit(ReferenceTable.BLOCK_HEADER_LEN + blockSize);
			
			data.read(bb, block * ReferenceTable.TOTAL_BLOCK_LEN);
			bb.flip();
			
			/* This read is exactly BLOCK_HEADER_LEN bytes long */
			/* The id of the current file we're reading */
			int currentFile = bb.getShort() & 0xFFFF;
			/* The current chunk we're reading. This should be sequential, 0 to n */
			int currentChunk = bb.getShort() & 0xFFFF;
			
			/* The ID of the next block to read from */
			int nextBlock = getTriByte(bb);
			
			/* The IDX this file belongs to */
			int currentIndex = bb.get() & 0xFF;
			
			if(currentChunk != chunk){
				throw new IOException("Chunk inconsistency");
			}
			
			if(fileId != currentFile){
				throw new IOException("File inconsistency");
			}
			
			if(idx != currentIndex){
				throw new IOException("IDX inconsistency");
			}
		
			payload.put(bb);
			block = nextBlock;
			chunk++;
		}
		
		payload.flip();
		return payload;
	}
	
	public static CacheFile decode(int idx, ByteBuffer payload, int fileId, XTEAKey key) throws IOException{
		CacheFile f = new CacheFile();
		f.compression = RSCompression.forId(payload.get() & 0xFF);
		int length = payload.getInt(); /* Length of compressed data */
		int limit = payload.limit(); /* Preserve current limit */
		
		//+4 if compressed, else +0, since compressed files are prepended with an integer specifying decompressed length
		payload.limit(payload.position() + length + (f.compression == RSCompression.NONE ? 0 : 4)); /* Only read length bytes, the new limit is <= the old limit */
		
		/* The following will most likely throw an exception if the given key was invalid
		 * or not supplied.
		 */
		
		try{
			f.payload = f.compression.decode(payload, key);
		}
		catch(IOException e){
			throw e;
		}
		
		//payload.position(payload.limit());
		/* Restore previous limit */
		payload.limit(limit);
		
		/* The version is the last two bytes */
		if(payload.remaining() >= 2){
			f.version = payload.getShort();
		}
		else{
			f.version = -1; /* No version attached */
		}
		
		if(f.payload.position() != 0){
			System.out.println("Failed to decode properly, position() != 0, compression " + f.compression + ", version: " + f.version + ", pos: " + f.payload.position() + " / lim: " + f.payload.limit());
		}
		return f;
	}
	
	public static CacheFile decode(int idx, FileChannel index, FileChannel data, int fileId, XTEAKey key) throws IOException{
		ByteBuffer payload = CacheFile.getRaw(idx, index, data, fileId);
		return decode(idx, payload, fileId, key);
	}
	
	private RSCompression compression;
	private ByteBuffer payload;
	private int version;
	
	/**
	 * Constructs a new cache file, eg this is not for decoding.
	 * @param compression the compression type to use. 0, 1 or 2.
	 */
	public CacheFile(RSCompression compression){
		this.compression = compression;
	}
	
	protected CacheFile(){
		
	}
	
	public void setCompression(RSCompression compression){
		if(compression == null) throw new NullPointerException("Compression may not be null");
		this.compression = compression;
	}
	
	/**
	 * Not ready yet, this does not encrypt the file.
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer encode() throws IOException{
		ByteBuffer data = getData(); /* Read only copy */

		/* grab the data as a byte array for compression */
		byte[] bytes = new byte[data.limit()];
		data.mark();
		data.get(bytes);
		data.reset();

		/* compress the data */
		ByteBuffer compressed = compression.encode(data, null);

		/* calculate the size of the header and trailer and allocate a buffer */
		int header = 5;
		if(this.version != -1){
			header += 2;
		}
		ByteBuffer buf = ByteBuffer.allocate(header + compressed.remaining());

		/* write the header, with the optional uncompressed length */
		buf.put((byte) compression.getId());
		//buf.putInt(compressed.remaining());
		if(compression == RSCompression.NONE){
			buf.putInt(compressed.remaining());
		}
		else{
			buf.putInt(compressed.remaining() - 4); //first 4 bytes are length of decompressed data
		}
		buf.put(compressed);

		/* write the trailer with the optional version */
		if (this.version != -1) {
			buf.putShort((short) version);
		}

		/* flip the buffer and return it */
		buf.flip();
		return buf;
	}
	
	/**
	 * Sets the contents of this buffer. This copies all bytes from the current position
	 * of the buffer to the limiting position of the buffer into a new bytebuffer. The return
	 * result of getData() will be a read only copy of this new bytebuffer. The given buffer
	 * will not be modified.
	 * @param buffer the new data to set.
	 */
	public void setData(ByteBuffer buffer){
		if(buffer == null) throw new NullPointerException("Buffer may not be null");
		//Create a copy so we do not modify the original buffer.
		buffer = buffer.asReadOnlyBuffer();
		
		this.payload = ByteBuffer.allocate(buffer.remaining());
		while(buffer.remaining() > 0){
			this.payload.put(buffer.get());
		}
		this.payload.flip();
	}
	
	/**
	 * Fetches a read only bytebuffer containing the data from this file.
	 * This data is decompressed.
	 * @return the file's data
	 */
	public ByteBuffer getData(){
		return this.payload.asReadOnlyBuffer();
	}
	
	/**
	 * The number of bytes the file is long
	 * @return The number of bytes the file is long
	 */
	public int getSize(){
		return this.payload.limit();
	}
	
	public int getVersion(){
		return this.version;
	}
	
	private static int getTriByte(ByteBuffer buffer) {
        return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
    }

	public RSCompression getCompression() {
		return compression;
	}
	
}