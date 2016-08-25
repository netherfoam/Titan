package org.maxgamer.rs.model.map.path;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author netherfoam
 */
public class Path implements Iterable<Direction>{
	private boolean fail;
	private ArrayList<Direction> directions;
	
	public Path() {
		this(new ArrayList<Direction>(25));
	}
	
	public Path(ArrayList<Direction> directions) {
		this.directions = directions;
		
		for (Direction d : directions) {
			if (d == null) throw new NullPointerException("Direction may not be null in a path!");
		}
	}
	
	/**
	 * Adds the given point to the start of the path, eg. this element will be
	 * removed when you call the next() method
	 * @param d The position
	 */
	public void addFirst(Direction d) {
		if (d == null) throw new NullPointerException("Direction may not be null in a path!");
		directions.add(d);
	}
	
	/**
	 * Adds the given point to the end of the path, eg. this element will be
	 * removed when you call the next() method for the last time
	 * @param d The position
	 */
	public void addLast(Direction d) {
		if (d == null) throw new NullPointerException("Direction may not be null in a path!");
		directions.add(0, d);
	}
	
	/**
	 * Removes the direction at the end of the queue, eg. Makes this path stop
	 * one tile short of its destination (Given that it succeeded)
	 * @return the direction that was removed.
	 */
	public Direction removeLast() {
		return directions.remove(0);
	}
	
	/**
	 * Retrieves and removes the first point in this path to visit.
	 * @return The point
	 */
	public Direction next() {
		return directions.remove(directions.size() - 1);
	}
	
	/**
	 * Retrieves and removes the first point in this path to visit.
	 * @return The point
	 */
	public Direction peek() {
		return directions.get(directions.size() - 1);
	}
	
	/**
	 * Returns true if this path has had all of its steps removed.
	 * @return true if this path has had all of its steps removed.
	 */
	public boolean isEmpty() {
		return directions.isEmpty();
	}
	
	/**
	 * A non-functional setter for the return result of {@link Path#hasFailed()}
	 * .
	 * @param fail whether the path failed or not.
	 */
	public void setFailed(boolean fail) {
		this.fail = fail;
	}
	
	/**
	 * Returns true if this path will never reach its intended destination. Even
	 * if this returns true, then the path may still contain some valid
	 * information on how to reach the target position.
	 * @return true if the path failed, false if it didn't.
	 */
	public boolean hasFailed() {
		return fail;
	}
	
	public int size() {
		return directions.size();
	}
	
	public Path clone() {
		Path path = new Path();
		path.directions = new ArrayList<Direction>(this.directions);
		path.fail = this.fail;
		return path;
	}
	
	public Direction get(int index) {
		//We pretend we're a backwards queue, so we should return in reverse order
		return directions.get(directions.size() - 1 - index);
	}

	@Override
	public Iterator<Direction> iterator() {
		return new Iterator<Direction>(){
			int i = directions.size() - 1;
			
			@Override
			public boolean hasNext() {
				return i >= 0;
			}

			@Override
			public Direction next() {
				return directions.get(i--);
			}

			@Override
			public void remove() {
				directions.remove(i+1);
			}
		};
	}
}