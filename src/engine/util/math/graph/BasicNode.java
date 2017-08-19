package engine.util.math.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic implementation of the {@code Node}
 * 
 * @author Kevin
 */
public class BasicNode implements Node {
	
	
	/**
	 * Name for testing purposes
	 */
	private String name;
	
	/**
	 * The list of edges featuring this {@code Node} as an endpoint
	 */
	private List<Edge> edges = new ArrayList<Edge>();
	
	public BasicNode(String name) {
		this.name = name;
	}
	
	@Override
	public List<Edge> getEdges() {
		return edges;
	}
	
	@Override
	public void addEdge(Edge e) {
		this.edges.add(e);
	}
	
	@Override
	public List<Edge> getReachableEdges(EdgeTraveler traveler) {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge e : this.edges) {
			// Check this permission first
			if (e.canTraverse(traveler)) {
				if (e.getBaseNode() == this || !e.isDirected()) {
					edges.add(e);
				}
			}
		}
		return edges;
	}
	
	@Override
	public Node[] getAdjacentNodes() {
		Node[] nodes = new Node[edges.size()];
		for (int i = 0; i < edges.size(); i++) {
			// Put the other node into the array
			if (edges.get(i).getBaseNode() == this) {
				nodes[i] = edges.get(i).getEndNode();
			} else {
				nodes[i] = edges.get(i).getBaseNode();
			}
		}
		return nodes;
	}
	
	@Override
	public List<Node> getReachableNodes(EdgeTraveler traveler) {
		List<Node> nodes = new ArrayList<Node>();
		for (Edge e : edges) {
			// Check permissions
			if (e.canTraverse(traveler)) {
				// Put the other node into the array, if applicable
				if (e.getBaseNode() == this || !e.isDirected()) {
					nodes.add(e.getEndNode());
				} else {
					nodes.add(e.getBaseNode());
				}
			}
		}
		return nodes;
	}
	
	@Override
	public String toString() {
		return "Node " + this.name;
	}
}
