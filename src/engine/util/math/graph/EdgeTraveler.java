package engine.util.math.graph;

/**
 * A utility class for traversing graphs
 * 
 * @author Kevin
 */
public interface EdgeTraveler {
	
	/**
	 * Checks whether this {@code EdgeTraveler} can travel along the edge
	 * 
	 * @param edge
	 *            The edge in question
	 * @return
	 */
	public boolean canTraverseEdge(Edge edge);
	
	/**
	 * Retrieves any additional traversal cost that this {@code EdgeTraveler} encurs while traveling along the
	 * edge
	 * 
	 * @param edge
	 *            The edge in question
	 * @return
	 */
	public int getAdditionalTraversalCost(Edge edge);
}
