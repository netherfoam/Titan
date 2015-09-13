package org.maxgamer.rs.structure.areagrid;

public class Cube implements MBR {
	private int[] coords;
	private int[] dimensions;
	
	public Cube(int[] coords, int[] dimensions) {
		if (coords.length != dimensions.length) {
			throw new RuntimeException("Cannot have a MBR which has " + coords.length + " coords but " + dimensions.length + " dimensions!");
		}
		
		this.coords = coords;
		this.dimensions = dimensions;
	}
	
	@Override
	public int getDimension(int axis) {
		return this.dimensions[axis];
	}
	
	@Override
	public int getDimensions() {
		return this.dimensions.length;
	}
	
	@Override
	public int getMin(int axis) {
		return this.coords[axis];
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Cube:");
		
		for (int i = 0; i < this.getDimensions(); i++) {
			sb.append(" (" + this.coords[i] + "-" + (this.coords[i] + this.dimensions[i]) + ")");
		}
		return sb.toString();
	}
}