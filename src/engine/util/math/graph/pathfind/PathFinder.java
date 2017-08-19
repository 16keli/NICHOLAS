package engine.util.math.graph.pathfind;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import engine.level.tiled.pathfind.UnitPath;
import engine.util.math.graph.Edge;
import engine.util.math.graph.EdgeTraveler;
import engine.util.math.graph.Node;

/**
 * A utility class that creates {@link UnitPath}s with different algorithms
 * <p>
 * Currently supported algorithms and their purposes:
 * <ul>
 * <li>A*: {@link #AStar(EdgeTraveler, Node, Node, AStarHeuristic)} (Effective path between two points)</li>
 * <li>Dijkstra:
 * </ul>
 * 
 * @author Kevin
 */
public class PathFinder {
	
	
	/**
	 * Cache the previously calculated {@code Path} so as to not have to do a bunch of calculations over again
	 */
	public static Path pathCache;
	
	// A* algorithm
	
	/**
	 * Gets the shortest path for the given {@code EdgeTraveler} from the {@code start} Node to the
	 * {@code goal} Node
	 * <p>
	 * The A* algorithm is used in Pathfinding, which takes into account movement costs for the given
	 * {@code EdgeTraveler}s
	 * <p>
	 * <strike>Alright we're gonna try some A* les go</strike>
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler} that wants to see get a path, if any
	 * @param goal
	 *            The destination {@code Node}
	 * @return A valid {@code Path} if there is one, or {@code null} if there is not
	 */
	public static Path AStar(EdgeTraveler traveler, Node start, Node goal, AStarHeuristic heuristic) {
		// Check the cache to see whether a path has already been calculated
		if (pathCache != null && pathCache.isPathWith(traveler, goal)) {
			return pathCache;
		}
		// Bummer. welp, off to do some calculations!
		
		// Set of already evaluated Tiles
		Set<Node> eval = new HashSet<Node>();
		// Tiles Not yet evaluated
		Set<Node> open = new HashSet<Node>();
		// A Map of the most efficient way to get from one Node to another
		Map<Node, Node> cameFrom = new HashMap<Node, Node>();
		// Cost of getting to the specific Node from the start
		Map<Node, Integer> gScore = new HashMap<Node, Integer>();
		// Cost of getting to the destination by passing through this Node
		Map<Node, Integer> fScore = new HashMap<Node, Integer>();
		
		// Initialize some values
		open.add(start);
		gScore.put(start, 0);
		fScore.put(start, heuristic.calculate(traveler, start, goal));
		
		while (!open.isEmpty()) {
			// Get the node with the lowest FScore in the open set, as that's our best bet
			// Sidenote not quite sure why I did this O(n) stuff here. Consider replacing?
			// Save some memory? But use more time?
			// Should be okay. A* has a specific goal in mind
			Node current = null;
			int minFScore = Integer.MAX_VALUE;
			for (Node t : open) {
				if (fScore.get(t) < minFScore) {
					minFScore = fScore.get(t);
					current = t;
				}
			}
			
			if (current == goal) {// We've gotten to the goal, so let's reconstruct the path.
				Path path = AStarReconstructPath(traveler, cameFrom, start, current, gScore.get(current));
				pathCache = path;
				return path;
			}
			
			// Remove the node we're evaluating from the open set and put it in the evaluated set
			open.remove(current);
			eval.add(current);
			// Test each neighbor of the current tile to find the optimal way to get to the destination
			for (Edge e : current.getReachableEdges(traveler)) {
				// Gets the other node
				Node node = e.getOtherNode(current);
				// Already evaluated this Node, or this tile cannot be moved to (this part is handled by
				// reachable edges)
				if (eval.contains(node)) {
					continue;
				}
				// The tentative gScore. Subject to scrutiny.
				int tCost = gScore.get(current) + e.getCost(traveler);
				// Determine what to do with the tentative cost
				if (!open.contains(node)) {// We might've gotten ahead of ourselves. Let's fix that, shall we?
					open.add(node);
				} else if (tCost > gScore.get(node)) {// This Path is not the best...
					continue;
				}
				// This IS the best path for now...
				cameFrom.put(node, current);
				gScore.put(node, tCost);
				fScore.put(node, tCost + heuristic.calculate(traveler, node, goal));
			}
		}
		return null;// There is no path...
	}
	
	/**
	 * Reconstructs the {@code Path} from the given parameters
	 * <p>
	 * The reconstruction algorithm puts the tiles into the list in reverse order, thus the invocation of
	 * {@code reverse} is necessary to ensure that the {@code Node}s are in order
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler}
	 * @param cameFrom
	 *            The Map of the most efficient ways to move between {@code Node}s
	 * @param start
	 *            The start {@code Node}
	 * @param current
	 *            The "current" {@code Node} (The destination)
	 * @param cost
	 *            The cost of moving along this path
	 * @return A fresh UnitPath reconstructed for your health
	 */
	protected static Path AStarReconstructPath(EdgeTraveler traveler, Map<Node, Node> cameFrom, Node start,
			Node current, int cost) {
		List<Node> tiles = new LinkedList<Node>();
		tiles.add(current);
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			tiles.add(current);
		}
		Collections.reverse(tiles);
		return new Path(traveler, start, tiles.get(0), tiles, cost);
	}
	
	// Dijkstra's algorithm
	
	/**
	 * Calculates the pathfinding mesh for the entire graph with the given {@code EdgeTraveler} and
	 * {@code Node} to start with
	 * <p>
	 * While this is certainly much more expensive and time consuming as it covers the whole graph, the mesh
	 * is also much more complete over the A* algorithm
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler} to get the mesh of
	 * @param start
	 *            The {@code Node} to start generating the mesh from
	 * @return The completed navigation mesh
	 */
	public static Mesh Dijkstra(EdgeTraveler traveler, Node start) {
		
		System.out.println("Evaluating Dijktra for " + start);
		
		// Set of already evaluated Tiles
		Set<Node> eval = new HashSet<Node>();
		// Tiles Not yet evaluated
		Set<Node> open = new HashSet<Node>();
		// A Map of the most efficient way to get from one Node to another
		Map<Node, Node> cameFrom = new HashMap<Node, Node>();
		// Cost of getting to the specific Node from the start
		Map<Node, Integer> gScore = new HashMap<Node, Integer>();
		
		// Initialize some values
		open.add(start);
		gScore.put(start, 0);
		
		while (!open.isEmpty()) {
			// Get the node with the lowest FScore in the open set, as that's our best bet
			// Sidenote not quite sure why I did this O(n) stuff here. Consider replacing?
			// Save some memory? But use more time?
			Node current = null;
			int minGScore = Integer.MAX_VALUE;
			for (Node t : open) {
				if (gScore.get(t) < minGScore) {
					minGScore = gScore.get(t);
					current = t;
				}
			}
			
			System.out.println("Dijktra evaluating " + current);
			
			// Remove the node we're evaluating from the open set and put it in the evaluated set
			open.remove(current);
			eval.add(current);
			// Test each neighbor of the current tile to find the optimal way to get to the destination
			for (Edge e : current.getReachableEdges(traveler)) {
				// Gets the other node
				Node node = e.getOtherNode(current);
				System.out.println("Testing neighbor " + node);
				// Already evaluated this Node, or this tile cannot be moved to (this part is handled by
				// reachable edges)
				if (eval.contains(node)) {
					continue;
				}
				// The tentative gScore. Subject to scrutiny.
				int tCost = gScore.get(current) + e.getCost(traveler);
				// Determine what to do with the tentative cost
				if (!open.contains(node)) {// We might've gotten ahead of ourselves. Let's fix that, shall we?
					open.add(node);
				} else if (tCost > gScore.get(node)) {// This Path is not the best...
					continue;
				}
				// This IS the best path for now...
				cameFrom.put(node, current);
				gScore.put(node, tCost);
			}
		}
		
		Mesh mesh = new Mesh(traveler, start, cameFrom, gScore);
		
		return mesh;
	}
	
	/**
	 * Hokay this is supposed to be Heuristic and can never overestimate so let's make this as simple as
	 * possible shall we
	 * <p>
	 * And by that I mean just use the code from {@code Node}
	 * <p>
	 * So why is this its own method? And why does it have so much documentation??
	 * <p>
	 * IDK. In case I ever want to change it I guess
	 * 
	 * @param tile
	 *            The {@code Node} to start at
	 * @param goal
	 *            The destination {@code Node}
	 * @return A cost estimate of getting from tile to goal
	 */
//	private static int AStarCostEstimate(Node tile, Node goal) {
//		return tile.AStarCostEstimate(goal);
//	}

}
