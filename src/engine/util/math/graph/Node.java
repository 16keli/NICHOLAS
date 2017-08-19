package engine.util.math.graph;

import java.util.List;

/**
 * Represents a node on a graph
 * 
 * @author Kevin
 */
public interface Node {
	
	/**
	 * Gets the list of all edges that have this {@code Node} as an endpoint
	 * 
	 * @return
	 */
	public List<? extends Edge> getEdges();
	
	/**
	 * Adds an edge to the node
	 * 
	 * @param e
	 */
	public void addEdge(Edge e);
	
	/**
	 * Gets the list of all edges that can be traversed from this {@code Node} by the given
	 * {@code EdgeTraveler}
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler}
	 * @return
	 */
	public List<? extends Edge> getReachableEdges(EdgeTraveler traveler);
	
	/**
	 * Gets all adjacent nodes to this one. Adjacent means that the two nodes are connected by an edge,
	 * regardless of their direction
	 * 
	 * @return
	 */
	public Node[] getAdjacentNodes();
	
	/**
	 * Gets all nodes that are reachable from this {@code Node}, which requires either an undirected edge or a
	 * directed one in the right direction, as well as an {@code EdgeTraveler} capable of doing so
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler} to get nodes reachable by
	 * @return
	 */
	public List<? extends Node> getReachableNodes(EdgeTraveler traveler);
}
