package engine.util.math.graph.pathfind;

import engine.util.math.graph.EdgeTraveler;
import engine.util.math.graph.Node;

/**
 * The heuristic used in A* pathfinding, specifically to estimate the remaining distance to the goal from a
 * specific {@code Node}
 * 
 * @author Kevin
 */
public abstract class AStarHeuristic {
	
	
	/**
	 * Calculates the A* Heuristic f-Score given the parameters
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler} moving along the path
	 * @param start
	 *            The {@code Node} to start the calculations from
	 * @param goal
	 *            The {@code Node} that is the goal
	 * @return
	 */
	public abstract int calculate(EdgeTraveler traveler, Node start, Node goal);
	
}
