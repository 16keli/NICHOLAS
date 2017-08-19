package engine.util.math.graph;

/**
 * Represents an edge connecting two nodes together
 * 
 * @author Kevin
 */
public class Edge {
	
	
	/**
	 * Whether or not the edge is directed
	 */
	private boolean directed;
	
	/**
	 * The base cost of traveling along this edge
	 */
	private int baseCost;
	
	/**
	 * The {@code Node} that this edge starts on
	 */
	private Node baseNode;
	
	/**
	 * The {@code Node} that this edge ends on
	 */
	private Node endNode;
	
	/**
	 * Constructs an {@code Edge} with the given two {@code Node}s as its endpoints. The base cost of moving
	 * along the edge is 1, and it is undirected
	 * 
	 * @param baseNode
	 *            The node this {@code Edge} starts on
	 * @param endNode
	 *            The node this {@code Edge} ends on
	 */
	protected Edge(Node baseNode, Node endNode) {
		this(baseNode, endNode, 1, false);
	}
	
	/**
	 * Constructs an {@code Edge} with the given two {@code Node}s as its endpoints and the cost of moving
	 * along the node. The edge is undirected
	 * 
	 * @param baseNode
	 *            The node this {@code Edge} starts on
	 * @param endNode
	 *            The node this {@code Edge} ends on
	 * @param baseCost
	 *            The base cost of traversing this {@code Edge}
	 */
	protected Edge(Node baseNode, Node endNode, int baseCost) {
		this(baseNode, endNode, baseCost, false);
	}
	
	/**
	 * Constructs an {@code Edge} with the given two {@code Node}s as its endpoints and the cost of moving
	 * along the node
	 * 
	 * @param baseNode
	 *            The node this {@code Edge} starts on
	 * @param endNode
	 *            The node this {@code Edge} ends on
	 * @param baseCost
	 *            The base cost of traversing this {@code Edge}
	 * @param directed
	 *            Whether or not this edge is directed
	 */
	protected Edge(Node baseNode, Node endNode, int baseCost, boolean directed) {
		this.baseNode = baseNode;
		this.endNode = endNode;
		this.baseCost = baseCost;
		this.directed = directed;
	}
	
	/**
	 * Retrieves the cost of traveling along this edge with the given {@code EdgeTraveler} or not
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler}
	 * @return
	 */
	public int getCost(EdgeTraveler traveler) {
		return this.baseCost + traveler.getAdditionalTraversalCost(this);
	}
	
	/**
	 * Checks whether the given {@code EdgeTraveler} can traverse this edge or not
	 * 
	 * @param traveler
	 *            The {@code EdgeTraveler}
	 * @return
	 */
	public boolean canTraverse(EdgeTraveler traveler) {
		return traveler.canTraverseEdge(this);
	}
	
	/**
	 * Checks whether this Edge is directed
	 * 
	 * @return
	 */
	public boolean isDirected() {
		return this.directed;
	}
	
	/**
	 * Gets the base node of this Edge, or the one that it starts on if this edge is directed
	 * 
	 * @return
	 */
	public Node getBaseNode() {
		return this.baseNode;
	}
	
	/**
	 * Gets the end node of this Edge, or the one that it ends on if this edge is directed
	 * 
	 * @return
	 */
	public Node getEndNode() {
		return this.endNode;
	}
	
	/**
	 * Gets the other node in the edge. This method assumes {@code node} is an endpoint
	 * 
	 * @param node
	 *            The {@code Node} to get the counterpart of
	 * @return
	 */
	public Node getOtherNode(Node node) {
		if (this.baseNode == node) {
			return this.endNode;
		} else {
			return this.baseNode;
		}
	}
	
}
