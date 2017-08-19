package engine.util.math.graph;

import java.util.List;

/**
 * A graph represented using an adjacency matrix
 * 
 * @author Kevin
 */
public interface Graph {
	
	/**
	 * The List of {@code Node}s this {@code Graph} contains
	 * 
	 * @return
	 */
	public List<Node> getNodes();
	
	/**
	 * Adds a {@code Node} to the graph
	 * 
	 * @param n
	 *            The {@code Node} to add
	 */
	public void addNode(Node n);
	
	/**
	 * Connects the given start and end {@code Node}s together with an edge. The edge can have a certain cost
	 * associated with crossing it, and it can be directed
	 * 
	 * @param start
	 *            The first {@code Node}, the starting endpoint
	 * @param end
	 *            The second {@code Node}, the ending endpoint
	 * @param cost
	 *            The cost of traversing the edge
	 * @param directed
	 *            Whether the connection between the nodes is directed
	 */
	public void connectNodes(Node start, Node end, int cost, boolean directed);
	
}
