package org.maxgamer.rs.structure;

/**
 * write bits-at-a-time where the number of bits is between 1 and 32
 * Client programs must call <code>flush</code> or
 * <code>close</code> when finished writing or not all bits will be written.
 * <P>
 * Updated for version 2.0 to extend java.io.OutputStream
 * @author Owen Astrachan
 * @version 1.0, July 2000
 * @version 2.0, October 2004
 * 
 * Original available at http://www.cs.duke.edu/csed/poop/huff/spring05/code/BitOutputStream.java
 */

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Owen Astrachan
 */
public class BitOutputStream extends OutputStream {
	private OutputStream out;
	private int buffer;
	private int bitsRemaining;
	
	private static final int BIT_MASK[] = { 0x00, 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F, 0xFF, 0x1FF, 0x3FF, 0x7FF, 0xFFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF, 0x1FFFF, 0x3FFFF, 0x7FFFF, 0xFFFFF, 0x1FFFFF, 0x3FFFFF, 0x7FFFFF, 0xFFFFFF, 0x1FFFFFF, 0x3FFFFFF, 0x7FFFFFF, 0xFFFFFFF, 0x1FFFFFFF, 0x3FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF };
	
	private static final int BITS_PER_BYTE = 8;
	
	/**
	 * Required by OutputStream subclasses, write the low 8-bits to the
	 * underlying outputstream
	 */
	@Override
	public void write(int b) throws IOException {
		this.out.write(b);
	}
	
	public BitOutputStream(OutputStream out) {
		this.out = out;
		this.initialize();
	}
	
	private void initialize() {
		this.buffer = 0;
		this.bitsRemaining = BITS_PER_BYTE;
	}
	
	/**
	 * Construct a bit-at-a-time output stream with specified file name
	 * 
	 * @param filename is the name of the file being written
	 */
	public BitOutputStream(String filename) {
		try {
			this.out = new BufferedOutputStream(new FileOutputStream(filename));
		}
		catch (FileNotFoundException fnf) {
			System.err.println("could not create " + filename + " " + fnf);
		}
		catch (SecurityException se) {
			System.err.println("security exception on write " + se);
		}
		this.initialize();
	}
	
	/**
	 * Flushes bits not yet written, must be called by client programs if
	 * <code>close</code> isn't called.
	 * 
	 */
	@Override
	public void flush() {
		if (this.bitsRemaining != BITS_PER_BYTE) {
			try {
				this.write((this.buffer << this.bitsRemaining));
			}
			catch (java.io.IOException ioe) {
				System.err.println("error writing bits on flush " + ioe);
			}
			this.buffer = 0;
			this.bitsRemaining = BITS_PER_BYTE;
		}
		
		try {
			this.out.flush();
		}
		catch (java.io.IOException ioe) {
			System.err.println("error on flush " + ioe);
		}
	}
	
	/**
	 * releases system resources associated with file and flushes bits not yet
	 * written. Either this function or flush must be called or not all bits
	 * will be written
	 * 
	 */
	@Override
	public void close() {
		this.flush();
		try {
			this.out.close();
		}
		catch (IOException ioe) {
			System.err.println("error closing BitOutputStream " + ioe);
		}
	}
	
	/**
	 * write bits to file
	 * 
	 * @param howManyBits is number of bits to write (1-32)
	 * @param value is source of bits, rightmost bits are written
	 */
	
	public void write(int howManyBits, int value) {
		value &= BIT_MASK[howManyBits]; // only right most bits valid
		
		while (howManyBits >= this.bitsRemaining) {
			this.buffer = (this.buffer << this.bitsRemaining) | (value >> (howManyBits - this.bitsRemaining));
			try {
				this.write(this.buffer);
			}
			catch (java.io.IOException ioe) {
				System.err.println("error writing bits " + ioe);
			}
			
			value &= BIT_MASK[howManyBits - this.bitsRemaining];
			howManyBits -= this.bitsRemaining;
			this.bitsRemaining = BITS_PER_BYTE;
			this.buffer = 0;
		}
		
		if (howManyBits > 0) {
			this.buffer = (this.buffer << howManyBits) | value;
			this.bitsRemaining -= howManyBits;
		}
	}
}