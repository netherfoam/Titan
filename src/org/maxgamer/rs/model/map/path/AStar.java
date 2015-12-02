package org.maxgamer.rs.model.map.path;

import java.util.PriorityQueue;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.structure.timings.StopWatch;

/**
 * @author netherfoam
 */
public class AStar implements PathFinder {
	private static class Node implements Comparable<Node> {
		private int currentCost;
		private Direction dir;
		private Location location;

		private Node parent;
		private int totalCost;

		public Node(Node parent, Direction dir, Location loc, Position goal) {
			this.parent = parent;
			this.dir = dir;
			this.location = loc;
			if (this.parent != null) {
				this.currentCost = this.parent.currentCost + 1;
			}
			this.totalCost = this.currentCost + getHeuristic(loc, goal);
		}

		@Override
		public int compareTo(Node n2) {
			int delta = this.totalCost - n2.totalCost;
			if (delta == 0) {
				// May have to swap these.
				// This makes us bias towards straight paths.
				if (this.dir.dx == 0 || this.dir.dy == 0) {
					return -1;
				}
				if (n2.dir.dx == 0 || n2.dir.dy == 0) {
					return 1;
				}
			}
			return delta;
		}
	}

	private static class NodeMap {
		private int[][] antiClips;
		private Node[][] nodes;
		private int xOff;
		private int yOff;

		public NodeMap(int buffer, Position min, Position max) {
			int width = max.x - min.x + buffer * 2 + 1;
			int length = max.y - min.y + buffer * 2 + 1;
			this.nodes = new Node[width][length];
			this.antiClips = new int[width][length];

			this.xOff = min.x - buffer;
			this.yOff = min.y - buffer;
		}

		public int getAntiClip(int x, int y) {
			return this.antiClips[x - this.xOff][y - this.yOff];
		}

		public Node getNode(int x, int y) {
			return this.nodes[x - this.xOff][y - this.yOff];
		}

		public void setAntiClip(int x, int y, int clip) {
			try {
				this.antiClips[x - this.xOff][y - this.yOff] = clip;
			} catch (IndexOutOfBoundsException e) {
				// Harmless, can't visit there anyway.
			}
		}

		public void setNode(int x, int y, Node n) {
			this.nodes[x - this.xOff][y - this.yOff] = n;
		}
	}

	private static NodeMap createMap(Location start, Position min, Position max, int buffer) {
		int minX = Calc.mini(start.x, min.x, max.x);
		int minY = Calc.mini(start.y, min.y, max.y);
		int maxX = Calc.maxi(start.x, min.x, max.x);
		int maxY = Calc.maxi(start.y, min.y, max.y);

		NodeMap map = new NodeMap(buffer, new Position(minX, minY), new Position(maxX, maxY));
		return map;
	}

	private static Position getClosest(Position from, Position min, Position max) {
		int x, y;
		if (from.x < min.x) {
			x = min.x;
		} else if (from.x > max.x) {
			x = max.x;
		} else {
			x = from.x; // Our X doesn't need changing
		}

		if (from.y < min.y) {
			y = min.y;
		} else if (from.y > max.y) {
			y = max.y;
		} else {
			y = from.y; // Our Y doesn't need changing
		}

		return new Position(x, y);
	}

	private static int getHeuristic(Position from, Position to) {
		int dx = Math.abs(from.x - to.x);
		int dy = Math.abs(from.y - to.y);

		int diag;
		int straight;
		if (dx > dy) {
			diag = dy;
			straight = dx - diag;
		} else { // Zero case here.
			diag = dx;
			straight = dy - diag;
		}
		return diag + straight;
	}

	private static boolean isContained(Position to, Position min, Position max) {
		if (to.x < min.x || to.x > max.x) {
			return false;
		}
		if (to.y < min.y || to.y > max.y) {
			return false;
		}
		return true;
	}

	private static void toPath(Path path, Node end) {
		while (end.parent != null) {
			path.addFirst(end.dir);
			end = end.parent;
		}
	}

	private int buffer;

	public AStar(int buffer) {
		if (buffer < 0) {
			throw new IllegalArgumentException("Buffer must be >= 0, given " + buffer);
		}
		this.buffer = buffer;
	}

	@Override
	public Path findPath(Location start, Position min, Position max, int xSize, int ySize) {
		NodeMap map = createMap(start, min, max, this.buffer);

		return this.findPath(start, min, max, xSize, ySize, map);
	}

	public Path findPath(Mob m, Position min, Position max, GameObject... ignores) {
		return findPath(m.getLocation(), min, max, m.getSizeX(), m.getSizeY(), ignores);
	}

	public Path findPath(Mob m, Entity to) {
		return findPath(m.getLocation(), to.getLocation(), to.getLocation().add(to.getSizeX() - 1, to.getSizeY() - 1), to.getSizeX(), to.getSizeY());
	}
	
	public Path findPath(Mob m, GameObject to) {
		return findPath(m.getLocation(), to.getLocation(), to.getLocation().add(to.getSizeX() - 1, to.getSizeY() - 1), to.getSizeX(), to.getSizeY(), to);
	}

	public Path findPath(Location start, Position min, Position max, int xSize, int ySize, GameObject... ignores) {
		NodeMap map = createMap(start, min, max, this.buffer);
		for (GameObject ignore : ignores) {
			// Set our ignored clips.
			Location swCorner = ignore.getLocation();
			if (swCorner.z == start.z) {
				int[][] clip = ignore.getClip();

				/*
				 * Applies the below clipping (# = point of interest) [?][?][?]
				 * [?][#][?] [?][?][?]
				 */
				for (int i = 0; i < ignore.getSizeX(); i++) {
					for (int j = 0; j < ignore.getSizeY(); j++) {
						map.setAntiClip(swCorner.x + i, swCorner.y + j, clip[1][1]); // Apply
																						// the
																						// center
																						// clip
					}
				}

				/*
				 * Applies the below clipping (# = point of interest) [?][#][?]
				 * [?][?][?] [?][#][?]
				 */
				for (int i = 0; i < ignore.getSizeX(); i++) {
					map.setAntiClip(swCorner.x + i, swCorner.y - 1, clip[1][0]);
					map.setAntiClip(swCorner.x + i, swCorner.y + ignore.getSizeY(), clip[1][2]);
				}

				/*
				 * Applies the below clipping (# = point of interest) [?][?][?]
				 * [#][?][#] [?][?][?]
				 */
				for (int j = 0; j < ignore.getSizeY(); j++) {
					map.setAntiClip(swCorner.x - 1, swCorner.y + j, clip[0][1]);
					map.setAntiClip(swCorner.x + ignore.getSizeX(), swCorner.y + j, clip[2][1]);
				}

				/*
				 * Applies the below clipping (# = point of interest) [#][?][#]
				 * [?][?][?] [#][?][#]
				 */
				map.setAntiClip(swCorner.x - 1, swCorner.y - 1, clip[0][0]);
				map.setAntiClip(swCorner.x - 1, swCorner.y + ignore.getSizeY(), clip[0][2]);
				map.setAntiClip(swCorner.x + ignore.getSizeX(), swCorner.y - 1, clip[2][0]);
				map.setAntiClip(swCorner.x + ignore.getSizeX(), swCorner.y + ignore.getSizeY(), clip[2][2]);
			}
		}
		return this.findPath(start, min, max, xSize, ySize, map);
	}

	public Path findPath(Location start, Position min, Position max, int xSize, int ySize, NodeMap map) {
		Path path = new Path();
		if (isContained(start, min, max)) {
			return path; // Empty, we're already there!
		}

		StopWatch w = Core.getTimings().start(this.getClass().getSimpleName() + "-pathfinder");
		try {
			PriorityQueue<Node> open = new PriorityQueue<Node>(128); // TODO:
																		// Guesstimate
																		// a
																		// size.
			Node top = new Node(null, null, start, getClosest(start, min, max));
			open.add(top);

			Node bestNode = top;
			int bestDistance = top.location.distanceSq(getClosest(top.location, min, max));

			while (open.isEmpty() == false) {
				Node n = open.poll();

				Position target = getClosest(n.location, min, max);
				for (Direction d : Directions.ALL) {
					Node m;
					try {
						m = map.getNode(n.location.x + d.dx, n.location.y + d.dy);
						if (m != null) {
							continue; // We've already inspected this node.
						}
					} catch (IndexOutOfBoundsException e) {
						continue; // Out of bounds. We treat it as if we can't
									// reach it.
					}

					int cFrom = d.conflictFrom(n.location);

					if (cFrom != 0 && (cFrom & ~map.getAntiClip(n.location.x, n.location.y)) != 0) {
						continue; // Clipped here
					}

					int cTo = d.conflictTo(n.location);
					if (cTo != 0 && (cTo & ~map.getAntiClip(n.location.x + d.dx, n.location.y + d.dy)) != 0) {
						continue; // Clipped here
					}

					m = new Node(n, d, n.location.add(d.dx, d.dy), target);

					if (isContained(m.location, min, max)) {
						toPath(path, m);
						return path; // Success
					}

					map.setNode(m.location.x, m.location.y, m);
					open.add(m);

					if (m.location.distanceSq(target) < bestDistance) {
						bestNode = m;
						bestDistance = m.location.distanceSq(target);
					}
				}
			}

			// We've failed to find a path.
			path.setFailed(true);
			toPath(path, bestNode);

			return path;
		} finally {
			w.stop();
		}
	}
}