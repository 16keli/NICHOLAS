package engine.level.tiled.pathfind;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import engine.level.tiled.EntityTiled;

/**
 * A utility class that creates {@link UnitPath}s with different algorithms
 * <p>
 * Currently supported algorithms:
 * <ul>
 * <li>A*: {@link #AStar(EntityTiled, PathfindNode)}</li>
 * </ul>
 * 
 * @author Kevin
 */
public class PathFinder {
	
	/**
	 * Cache the previously calculated {@code UnitPath} so as to not have to do a bunch of calculations over
	 * again
	 */
	public static UnitPath cache;
	
	// A* algorithm
	
	/**
	 * Gets the shortest path for the given {@code EntityTiled} from its {@code currentTile} to the
	 * {@code goal}.
	 * <p>
	 * The A* algorithm is used in the Pathfinding, which takes into account movement costs for the given
	 * {@code EntityTiled}s
	 * <p>
	 * <strike>Alright we're gonna try some A* les go</strike>
	 * 
	 * @param traveler
	 *            The {@code EntityTiled} that wants to see get a path, if any
	 * @param goal
	 *            The destination {@code PathfindNode}
	 * @return A valid {@code UnitPath} if there is one, or {@code null} if there is not
	 */
	public static UnitPath AStar(EntityTiled traveler, PathfindNode goal) {
		// Check the cache to see whether a path has already been calculated
		if (cache != null && cache.isPathWith(traveler, goal)) {
			return cache;
		}
		// Bummer. welp, off to do some calculations!
		
		PathfindNode start = traveler.currentTile;
		// Set of already evaluated Tiles
		Set<PathfindNode> eval = new HashSet<PathfindNode>();
		// Tiles Not yet evaluated
		Set<PathfindNode> open = new HashSet<PathfindNode>();
		// A Map of the most efficient way to get from one PathfindNode to another
		Map<PathfindNode, PathfindNode> cameFrom = new HashMap<PathfindNode, PathfindNode>();
		// Cost of getting to the specific PathfindNode from the start
		Map<PathfindNode, Integer> gScore = new HashMap<PathfindNode, Integer>();
		// Cost of getting to the destination by passing through this PathfindNode
		Map<PathfindNode, Integer> fScore = new HashMap<PathfindNode, Integer>();
		
		// Initialize some values
		open.add(start);
		gScore.put(start, 0);
		fScore.put(start, AStarCostEstimate(start, goal));
		
		while (!open.isEmpty()) {
			// Get the node with the lowest FScore in the open set, as that's our best bet
			PathfindNode current = null;
			int minFScore = Integer.MAX_VALUE;
			for (PathfindNode t : open) {
				if (fScore.get(t) < minFScore) {
					minFScore = fScore.get(t);
					current = t;
				}
			}
			
			if (current == goal) {// We've gotten to the goal, so let's reconstruct the path.
				UnitPath path = AStarReconstructPath(traveler, cameFrom, current, gScore.get(current));
				cache = path;
				return path;
			}
			
			open.remove(current);
			eval.add(current);
			// Test each neighbor of the current tile to find the optimal way to get to the destination
			for (PathfindNode node : current.getAdjacentNodes()) {
				// Already evaluated this PathfindNode, or this tile cannot be moved to
				if (eval.contains(node) || !node.canMoveToHere(traveler.getClass())) {
					continue;
				}
				// The tentative gScore. Subject to scrutiny.
				int tCost = gScore.get(current) + node.getMovementCost(traveler.getClass());
				// Determine what to do with the tentative cost
				if (!open.contains(node)) {// We might've gotten ahead of ourselves. Let's fix that, shall we?
					open.add(node);
				} else if (tCost > gScore.get(node)) {// This Path is not the best...
					continue;
				}
				// This IS the best path for now...
				cameFrom.put(node, current);
				gScore.put(node, tCost);
				fScore.put(node, tCost + AStarCostEstimate(node, goal));
			}
		}
		return null;// There is no path...
	}
	
	/**
	 * Reconstructs the {@code UnitPath} from the given parameters
	 * <p>
	 * The reconstruction algorithm puts the tiles into the list in reverse order, thus the invocation of
	 * {@code reverse} is necessary to ensure that the {@code PathfindNode}s are in order
	 * 
	 * @param ent
	 *            The {@code EntityTiled}
	 * @param cameFrom
	 *            The Map of the most efficient ways to move between {@code PathfindNode}s
	 * @param current
	 *            The "current" {@code PathfindNode} (The destination)
	 * @param cost
	 *            The cost of moving along this path
	 * @return A fresh UnitPath reconstructed for your health
	 */
	private static UnitPath AStarReconstructPath(EntityTiled ent, Map<PathfindNode, PathfindNode> cameFrom, PathfindNode current,
			int cost) {
		List<PathfindNode> tiles = new LinkedList<PathfindNode>();
		tiles.add(current);
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			tiles.add(current);
		}
		Collections.reverse(tiles);
		return new UnitPath(ent, ent.currentTile, tiles.get(0), tiles, cost);
	}
	
	/**
	 * Hokay this is supposed to be Heuristic and can never overestimate so let's make this as simple as
	 * possible shall we
	 * <p>
	 * And by that I mean just use the code from {@code PathfindNode}
	 * <p>
	 * So why is this its own method? And why does it have so much documentation??
	 * <p>
	 * IDK. In case I ever want to change it I guess
	 * 
	 * @param tile
	 *            The {@code PathfindNode} to start at
	 * @param goal
	 *            The destination {@code PathfindNode}
	 * @return A cost estimate of getting from tile to goal
	 */
	private static int AStarCostEstimate(PathfindNode tile, PathfindNode goal) {
		return tile.AStarCostEstimate(goal);
	}
	
}
