package engine.level.tiled.pathfind;

import java.util.List;

/**
 * A Node that is used by the Pathfinding functions to find paths between Nodes. Wow that was a really
 * roundabout explanation.
 * 
 * @author Kevin
 */
public interface PathfindNode {
	
	/**
	 * Retrieves the adjacent Pathfinding nodes
	 * 
	 * @return The pathfinding nodes adjacent to this node
	 */
	public List<? extends PathfindNode> getAdjacentNodes();
	
	/**
	 * Retrieves the specific movement cost for objects of the given class
	 * 
	 * @param c
	 *            The class
	 * @return The movement cost of the class
	 */
	public abstract int getMovementCost(Class<?> c);
	
	/**
	 * Checks whether objects of the given class can move to this {@code PathfindNode}
	 * 
	 * @param c
	 *            The class
	 * @return Whether it can move to this {@code PathfindNode}
	 */
	public abstract boolean canMoveToHere(Class<?> c);
	
	/**
	 * A heuristic method used to estimate the cost of travelling from {@code this} to {@code other}.
	 * <p>
	 * According to the pathfinding algorithm used by AStar, this method should be consistent. In other words,
	 * the result should be independent of the path taken. A state function.
	 * 
	 * @param other
	 * @return
	 */
	public abstract int AStarCostEstimate(PathfindNode other);
}
