package org.maxgamer.structure.areagrid;

public class AreaGridTest{
	public static void main(String[] args) {
		//Unit test. This adds 3x3 Cubes to a new AreaGrid, except for the middle one and then queries for anything
		//overlapping the middle grid. If any overlaps are detected, there is an error and the test has failed.
		AreaGrid<MBR> g = new AreaGrid<>(64, 64, 8);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 1 && j == 1) {
					//We don't want to put anything in the center! :)
					continue;
				}
				
				Cube c = new Cube(new int[] { i * 8, j * 8 }, new int[] { 8, 8 });
				if (g.get(c, 0).isEmpty() == false) {
					throw new RuntimeException("Overlap between " + g.get(c, 1) + " and " + c);
				}
				g.put(c);
			}
		}
		Cube c = new Cube(new int[] { 8, 8 }, new int[] { 8, 8 });
		System.out.println("There should be no overlaps: " + g.get(c, 0));
		c = new Cube(new int[] { 1, 1 }, new int[] { 1, 1 });
		System.out.println("There should be 1 overlap intended: " + g.get(c, 0));
	}
}