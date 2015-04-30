package org.maxgamer.rs.cache.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.maxgamer.structure.ReflectUtil;

/**
 * @author netherfoam
 */
public class CS2 {
	/**
	 * Unit test
	 */
	public static void main(String[] args) throws IOException {
		File idx12 = new File("extract", "idx12");
		File[] files = new File[] { new File(idx12, "file11.dat") }; //idx12.listFiles();
		int success = 0, failure = 0;
		int progress = 0;
		for (int i = 0; i < files.length; i++) {
			if (i * 80 / files.length > progress) {
				//Progress bar
				System.out.print("|");
			}
			
			File f = files[i];
			FileInputStream in = new FileInputStream(f);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();
			
			ByteBuffer bb = ByteBuffer.wrap(data);
			try {
				CS2.decode(11, bb);
			}
			catch (Exception e) {
				System.out.println("Error parsing " + f.getName());
				e.printStackTrace();
				failure++;
			}
		}
		
		System.out.println("Success: " + success + ", Failure: " + failure);
	}
	
	/**
	 * Decodes the given ClientScript buffer into a file. Throws IOException or
	 * {@link BufferUnderflowException} if the script could not be interpreted.
	 * Not all client scripts are currently interpretable.
	 * @param bb the buffer to read the data from, this will be modified.
	 *        Position 0 must be the start of the script, Limit must be the end
	 *        of the script.
	 * @return The decoded script, not null.
	 * @throws IOException If there was an error reading the script.
	 */
	public static CS2 decode(int id, ByteBuffer bb) throws IOException {
		CS2 cs2 = new CS2(id);
		
		//Length of the footer (Metadata)
		int footerLength = bb.getShort(bb.limit() - 2) & 0xFFFF;
		
		//Begin reading the footer
		bb.position(bb.limit() - 2 - footerLength - 12);
		
		//Number of instructions
		cs2.instructions = new CS2Op[bb.getInt()];
		
		//Number of int args required by script
		cs2.argInts = bb.getShort();
		//Number of String args required by script
		cs2.argStrings = bb.getShort();
		
		//Number of int variables declared by script
		cs2.numInts = bb.getShort();
		//Number of String variables declared by script
		cs2.numStrings = bb.getShort();
		
		if (footerLength > 0) {
			//I actually have no idea what these are. They are not
			//required for most small scripts.
			int resourceLength = bb.get() & 0xFF;
			
			cs2.nodelistKey = new int[resourceLength][];
			cs2.nodelistValue = new int[resourceLength][];
			
			for (int i = 0; i < resourceLength; i++) {
				short listLength = bb.getShort();
				cs2.nodelistKey[i] = new int[listLength];
				cs2.nodelistValue[i] = new int[listLength];
				
				for (int j = 0; j < listLength; j++) {
					cs2.nodelistKey[i][j] = bb.getInt();
					cs2.nodelistValue[i][j] = bb.getInt();
				}
			}
		}
		else {
			//No resources required.
			cs2.nodelistKey = new int[0][0];
			cs2.nodelistValue = new int[0][0];
		}
		
		//Start of file
		bb.position(0);
		
		//Script name, null terminated
		StringBuilder sb = new StringBuilder(8);
		char c;
		while ((c = (char) (bb.get())) != 0) {
			sb.append(c);
		}
		cs2.name = sb.toString();
		
		//Now begin reading our bytecode instructions
		for (int i = 0; i < cs2.instructions.length; i++) {
			cs2.instructions[i] = CS2Op.decode(bb);
		}
		
		/*
		 * //If we were successful, these should be equal. If our file is
		 * corrupt, these will probably be different. if(bb.position() !=
		 * bb.limit() - 2 - footerLength - 12){ throw new
		 * IOException("Bad script, locations don't match up. Position(): " +
		 * bb.position() + ", should be at " + (bb.limit() - 2 - footerLength -
		 * 12) + ", description: " + cs2); }
		 */
		
		return cs2;
	}
	
	/**
	 * The unique ID of this script. This may be -1
	 */
	protected int id;
	
	/**
	 * The name of this script, may be empty
	 */
	protected String name;
	
	/**
	 * The opcodes and operands contained within this script. These are in
	 * sequential order.
	 */
	protected CS2Op[] instructions;
	
	/**
	 * The number of arguments which are integers
	 */
	protected short argInts;
	
	/**
	 * The number of arguments which are strings
	 */
	protected short argStrings;
	
	/**
	 * The number of variables created by this script which are integers
	 */
	protected short numInts; //Number of int variables in script
	
	/**
	 * The number of variables created by this script which are Strings
	 */
	protected short numStrings; //Number of strings in the script
	
	/**
	 * The keys to the resource table for this script
	 */
	protected int[][] nodelistKey;
	
	/**
	 * The values for the resource table for this script
	 */
	protected int[][] nodelistValue;
	
	/**
	 * A blank constructor
	 */
	protected CS2(int id) {
		this.id = id;
	}
	
	/**
	 * The unique ID of this script. This may be -1 if it is a script that was
	 * not loaded from the cache
	 * @return the ID of the script
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * The name of the script, this should not be null but is often empty.
	 * @return the name of the script
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The number of instructions in this script
	 * @return The number of instructions in this script
	 */
	public int getSize() {
		return instructions.length;
	}
	
	/**
	 * Fetches an instruction by index
	 * @param index the instruction to fetch
	 * @return the instruction, not null
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid
	 */
	public CS2Op getInstruction(int index) {
		return instructions[index];
	}
	
	/**
	 * The number of args that are ints when invoking this script
	 * @return The number of args that are ints when invoking this script
	 */
	public int getIntArgCount() {
		return argInts;
	}
	
	/**
	 * The number of args that are Strings when invoking this script
	 * @return The number of args that are Strings when invoking this script
	 */
	public int getStringArgCount() {
		return argStrings;
	}
	
	/**
	 * The number of int variables this script requires to execute. This is not
	 * the args, this is actual declaration of variables.
	 * @return The number of int variables this script requires to execute
	 */
	public int getIntVarCount() {
		return numInts;
	}
	
	/**
	 * The number of String variables this script requires to execute. This is
	 * not the args, this is actual declaration of variables.
	 * @return The number of String variables this script requires to execute
	 */
	public int getStringVarCount() {
		return numStrings;
	}
	
	/**
	 * Checks the given arguments are valid arguments for this script. This does
	 * not run the script, it only checks the types are valid. That is, all args
	 * should be either a String or an Integer, and the number of Strings should
	 * equal CS2.getStringArgCount(), while the number of Integers should equal
	 * CS2.getIntegerArgCount(). If this is satisfied, this method returns true.
	 * @param args the args to validate the types of
	 * @return true if the args are valid, false if they are not.
	 * @throws IllegalArgumentException if the argument is not a valid object
	 *         (Not a String/Integer or is null)
	 */
	public boolean isValidInvokation(Object... args) {
		int s = 0;
		int i = 0;
		
		if (args != null) {
			for (Object o : args) {
				if (o == null) throw new IllegalArgumentException("Bad argument given: " + o);
				else if (o instanceof String) s++;
				else if (o instanceof Number) i++;
				else throw new IllegalArgumentException("Bad argument given: " + o);
			}
		}
		
		return s == argStrings && i == argInts;
	}
	
	/**
	 * Encodes this ClientScript and all of its instructions into a ByteBuffer.
	 * @return the ByteBuffer, not null and ready to be read from
	 */
	public ByteBuffer encode() {
		//First we need to find the size of all the instructions as bytecode
		ByteBuffer[] bytecode = new ByteBuffer[this.instructions.length];
		
		for (int i = 0; i < bytecode.length; i++) {
			bytecode[i] = this.instructions[i].encode();
		}
		
		int instructionSize = 0;
		for (ByteBuffer bb : bytecode) {
			instructionSize += bb.remaining();
		}
		
		//Now we can allocate a buffer of the correct size
		ByteBuffer bb = ByteBuffer.allocate(15 + nodelistKey.length * 8 + name.length() + 1 + instructionSize);
		
		//Write name, null terminated
		for (char c : name.toCharArray()) {
			bb.put((byte) c);
		}
		bb.put((byte) 0);
		
		//Write all of the instructions
		for (ByteBuffer op : bytecode) {
			bb.put(op);
		}
		
		//Now we start writing our footer
		bb.putInt(bytecode.length); //Number of instructions
		
		bb.putShort(argInts);
		bb.putShort(argStrings);
		
		bb.putShort(numInts);
		bb.putShort(numStrings);
		
		//We need to remember how many bytes we've written
		int nodeStart = bb.position();
		
		if (nodelistKey != null && nodelistKey.length > 0) {
			//Write our nodelist out
			bb.put((byte) nodelistKey.length);
			
			for (int i = 0; i < nodelistKey.length; i++) {
				bb.putShort((short) nodelistKey[i].length);
				
				for (int j = 0; j < nodelistKey[i].length; j++) {
					bb.putInt(nodelistKey[i][j]);
					bb.putInt(nodelistKey[i][j]);
				}
			}
		}
		else {
			//Technically the format allows us to skip this, but it is included anyway.
			//This is included for the sake of compatibility.
			bb.put((byte) 0);
		}
		
		//The number of bytes that the nodelist is. May be 0.
		bb.putShort((short) (bb.position() - nodeStart));
		
		bb.flip();
		return bb;
	}
	
	/**
	 * Represents a CS2 instruction opcode. This holds an opcode and an operand.
	 * The operand may be a byte, integer or String.
	 * @author netherfoam
	 */
	public static class CS2Op {
		/**
		 * The opcode. This defines the procedure this op undertakes. This is
		 * two bytes.
		 */
		protected int code;
		
		/**
		 * The operand. This is similar to an argument, except you only get one.
		 */
		protected Object operand;
		
		/**
		 * Decodes a CS2 instruction from the given {@link ByteBuffer}
		 * @param bb the {@link ByteBuffer} to decode from
		 * @return the CS2Op
		 * @throws BufferUnderflowException if the op could not be read.
		 */
		public static CS2Op decode(ByteBuffer bb) {
			CS2Op op = new CS2Op();
			op.code = bb.getShort() & 0xFFFF;
			
			if (op.code == 3) {
				StringBuilder sb = new StringBuilder(8);
				char c; //Null terminated String.
				while ((c = (char) (bb.get())) != 0) {
					sb.append(c);
				}
				op.operand = sb.toString();
			}
			else if (op.code == 21 || op.code == 38 || op.code == 39 || op.code > 100) {
				op.operand = bb.get() & 0xFF; //Unsigned byte
			}
			else {
				op.operand = bb.getInt(); //Signed integer
			}
			
			return op;
		}
		
		/**
		 * Encodes this CS2 instruction so that it may be decoded from the given
		 * buffer later.
		 * @param bb the buffer to write to
		 * @throws NullPointerException if the {@link ByteBuffer} is null
		 */
		public ByteBuffer encode() {
			ByteBuffer bb;
			
			if (this.code == 3) {
				String s = (String) operand;
				bb = ByteBuffer.allocate(2 + s.length() + 1);
				bb.putShort((short) this.code);
				for (int i = 0; i < s.length(); i++) {
					bb.put((byte) s.charAt(i));
				}
				bb.put((byte) 0);
			}
			else if (this.code == 21 || this.code == 38 || this.code == 39 || this.code > 100) {
				bb = ByteBuffer.allocate(3);
				bb.putShort((short) this.code);
				bb.put(((Number) operand).byteValue());
			}
			else {
				bb = ByteBuffer.allocate(6);
				bb.putShort((short) this.code);
				bb.putInt(((Number) operand).intValue());
			}
			bb.flip();
			return bb;
		}
		
		public String toString() {
			return "Op " + code + ", Arg " + operand;
		}
	}
	
	public String toString() {
		return ReflectUtil.describe(this);
	}
}