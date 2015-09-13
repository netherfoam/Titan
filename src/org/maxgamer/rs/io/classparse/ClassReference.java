package org.maxgamer.rs.io.classparse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ClassReference{
/*	public static void main(String[] args) throws IOException{
		RandomAccessFile source = new RandomAccessFile("bin/org/maxgamer/io/classparse/Simple.class", "r"); 
		FileChannel channel = source.getChannel();
		ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
		channel.read(bb);
		channel.close();
		source.close();
		
		bb.flip();
		
		System.out.println(ClassReference.decode(bb).describe());
	}
	*/
	
	public static ClassReference decode(byte[] data) throws IOException{
		return decode(ByteBuffer.wrap(data));
	}
	
	public static ClassReference decode(ByteBuffer bb) throws IOException{
		//See https://en.wikipedia.org/wiki/Java_class_file
		ClassReference cr = new ClassReference();
		bb.order(ByteOrder.BIG_ENDIAN);
		
		cr.magic = bb.getInt();
		cr.version_minor = bb.getShort();
		cr.version_major = bb.getShort();
		cr.constants = new Object[bb.getShort() - 1];
		
		for(int i = 0; i < cr.constants.length; i++){
			int tag = bb.get() & 0xFF;
			Object param = null;
			
			switch(tag){
				case 1:
					byte[] bytes = new byte[bb.getShort() & 0xFFFF];
					bb.get(bytes);
					param = new String(bytes, "UTF-8"); //Format isn't actually UTF-8, it is a slight modification
					break;
				case 3:
					param = bb.getInt();
					break;
				case 4:
					param = Float.intBitsToFloat(bb.getInt());
					break;
				case 5:
					param = bb.getLong();
					i++; //Skip, due to design spec.
					break;
				case 6:
					param = Double.longBitsToDouble(bb.getLong());
					i++; //Skip, due to design spec.
					break;
				case 7:
					param = bb.getShort();
					break;
				case 8:
					param = bb.getShort();
					break;
				case 9:
					param = bb.getInt();
					break;
				case 10:
					param = bb.getInt();
					break;
				case 11:
					param = bb.getInt();
					break;
				case 12:
					param = bb.getInt();
					break;
				case 15:
					param = (bb.getShort() << 8) | bb.get();
					break;
				case 16:
					param = bb.getShort();
					break;
				case 18:
					param = bb.getInt();
					break;
				default:
					throw new IOException("Unable to parse Class file, bad tag at index " + bb.position());
			}
			cr.constants[i] = param;
		}
		cr.flags = bb.getShort();
		cr.this_class = bb.getShort();
		cr.super_class = bb.getShort();
		return cr;
	}
	
	/**
	 * Magic constant value
	 */
	private int magic;
	
	/**
	 * File minor version
	 */
	private short version_minor;
	
	/**
	 * File major version
	 */
	private short version_major;
	
	/**
	 * Constant table contains strings, longs,
	 * doubles, floats and ints.
	 */
	private Object[] constants;
	
	/**
	 * Flags indicating interfaces, abstraction, public, final
	 * super, or other information
	 */
	private int flags;
	
	/**
	 * index of the fully qualified name of this class using
	 * '/' instead of '.', inside the constant array. Eg,
	 * constants[this_class] == "my/package/MyClass"
	 */
	private int this_class;
	
	/**
	 * index of the fully qualified superclass of this class using
	 * '/' instead of '.', inside the constant array. Eg,
	 * constants[super_class] == "my/package/MySuperClass"
	 */
	private int super_class;
	
	public String describe(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Magic: " + magic + "\n");
		sb.append("Version: " + version_major + ":" + version_minor + "\n");
		sb.append("Constants(" + constants.length + "): " + Arrays.toString(constants) + "\n");
		sb.append("this_class: " + this_class + "\n");
		sb.append("super_class: " + super_class + "\n");
		sb.append("Class: " + constants[this_class] + "\n");
		sb.append("Super: " + constants[super_class] + "\n");
		sb.append("Flags: " + flags);
		
		return sb.toString();
	}
	
	public String getClassName(){
		return ((String) (constants[this_class])).replace('/', '.');
	}
	
	public String getSuperClass(){
		return ((String) (constants[super_class])).replace('/', '.');
	}
}