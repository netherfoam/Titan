package org.maxgamer.rs.lib;

/**
 * @author netherfoam
 */
public class Calc {
	public static boolean isBetween(int number, int min, int max) {
		return number >= min && number <= max;
	}
	
	public static double betweend(double min, double max, double val) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static long betweenl(long min, long max, long val) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static int betweeni(int min, int max, int val) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static long minl(long... longs) {
		long min = longs[0];
		for (int i = 1; i < longs.length; i++) {
			if (longs[i] < min) min = longs[i];
		}
		return min;
	}
	
	public static int mini(int... ints) {
		int min = ints[0];
		for (int i = 1; i < ints.length; i++) {
			if (ints[i] < min) min = ints[i];
		}
		return min;
	}
	
	public static double mind(double... doubles) {
		double min = doubles[0];
		for (int i = 1; i < doubles.length; i++) {
			if (doubles[i] < min) min = doubles[i];
		}
		return min;
	}
	
	public static int maxi(int... ints) {
		int max = ints[0];
		for (int i = 1; i < ints.length; i++) {
			if (ints[i] > max) max = ints[i];
		}
		return max;
	}
	
	public static long maxl(long... longs) {
		long max = longs[0];
		for (int i = 1; i < longs.length; i++) {
			if (longs[i] > max) max = longs[i];
		}
		return max;
	}
	
	public static double maxd(double... doubles) {
		double max = doubles[0];
		for (int i = 1; i < doubles.length; i++) {
			if (doubles[i] > max) max = doubles[i];
		}
		return max;
	}
}