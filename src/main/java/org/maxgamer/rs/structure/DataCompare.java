package org.maxgamer.rs.structure;

import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class DataCompare {
	public static boolean diff(ByteBuffer a, ByteBuffer b) {
		int errors = 0;
		if (a.remaining() != b.remaining()) {
			System.out.println("A Remaining: " + a.remaining() + ", but B remaining: " + b.remaining());
			errors++;
		}
		
		int pos = 0;
		while (a.remaining() > 0 && b.remaining() > 0) {
			byte i = a.get();
			byte j = b.get();
			
			if (i != j) {
				System.out.println("a[" + pos + "] = " + i + ", but b[" + pos + "] = " + j);
				errors++;
				if (errors > 50) {
					System.out.println("Too many errors.");
					return true;
				}
			}
			pos++;
		}
		
		if (a.remaining() > 0) {
			System.out.println("Data available at the end of A: ");
			while (a.remaining() > 0) {
				System.out.println(a.get());
				errors++;
				if (errors > 50) {
					System.out.println("Too many errors.");
					return true;
				}
			}
		}
		
		if (b.remaining() > 0) {
			System.out.println("Data available at the end of B: ");
			while (b.remaining() > 0) {
				System.out.println(b.get());
				errors++;
				if (errors > 50) {
					System.out.println("Too many errors.");
					return true;
				}
			}
		}
		return errors > 0;
	}
}