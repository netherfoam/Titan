package org.maxgamer.rs.structure.areagrid;

public class MBRUtil{
	public static boolean isOverlap(MBR m1, MBR m2){
		assertDims(m1, m2);
		
		//Ensure we have an overlap
		for(int i = 0; i < m1.getDimensions(); i++){
			if(m1.getMin(i) + m1.getDimension(i) < m2.getMin(i)) return false; //m1's biggest edge is less than m2's smallest edge.
			if(m2.getMin(i) + m2.getDimension(i) < m1.getMin(i)) return false; //m2's biggest edge is less than m1's smallest edge.
		}
		
		return true;
	}
	
	public static MBR getOverlap(MBR m1, MBR m2){
		assertDims(m1, m2);
		//The minimum coordinate of the overlapping MBR
		int[] mins = new int[m1.getDimensions()];
		//The length/widths of the overlapping MBR
		int[] dims = new int[mins.length];
		
		for(int i = 0; i < m1.getDimensions(); i++){
			if(m1.getMin(i) + m1.getDimension(i) < m2.getMin(i) || m2.getMin(i) + m2.getDimension(i) < m1.getMin(i)){
				//No overlap
				throw new IllegalArgumentException("The given MBR's do not overlap");
			}
			
			if(m1.getMin(i) < m2.getMin(i)){
				//m1 is the smaller, we use m2.
				mins[i] = m2.getMin(i);
				
				
				int max = Math.min(m1.getMin(i) + m1.getDimension(i), m2.getMin(i) + m2.getDimension(i));
				dims[i] = max - mins[i];
			}
			else{
				//m2 is the smaller, we use m1.
				mins[i] = m1.getMin(i);
				
				int max = Math.min(m1.getMin(i) + m1.getDimension(i), m2.getMin(i) + m2.getDimension(i));
				dims[i] = max - mins[i];
			}
		}
		return new Cube(mins, dims);
	}
	
	public static boolean isEqual(MBR m1, MBR m2, int numDims){
		for(int axis = 0; axis < numDims; axis++){
			if(m1.getDimension(axis) != m2.getDimension(axis)) return false;
			if(m1.getMin(axis) != m2.getMin(axis)) return false;
		}
		return true;
	}
	
	public static boolean isEqual(MBR m1, MBR m2){
		if(m1 == m2) return true;
		if(m1.getDimensions() != m2.getDimensions()) return false;
		
		for(int axis = 0; axis < m1.getDimensions(); axis++){
			if(m1.getDimension(axis) != m2.getDimension(axis)) return false;
			if(m1.getMin(axis) != m2.getMin(axis)) return false;
		}
		return true;
	}
	
	private static void assertDims(MBR... mbr){
		int dims = mbr[0].getDimensions();
		for(int i = 1; i < mbr.length; i++){
			if(mbr[i].getDimensions() != dims) throw new IllegalArgumentException("MBR's must have the same number of dimensions!");
		}
	}

	public static String toString(MBR mbr) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < mbr.getDimensions(); i++){
			sb.append(mbr.getMin(i) + ",");
		}
		sb.append(" .. ");
		for(int i = 0; i < mbr.getDimensions(); i++){
			sb.append(mbr.getDimension(i) + "x");
		}
		return sb.toString();
	}
}