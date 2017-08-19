package engine.util.math.graph.pathfind;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import engine.util.math.graph.EdgeTraveler;
import engine.util.math.graph.Node;

/**
 * A navigation mesh used in pathfinding and displaying possibilities
 * 
 * @author Kevin
 */
public class Mesh {
	
	
	/**
	 * The map used to store both data at once
	 */
	private Map<Node, MeshEntry> meshMap = new HashMap<Node, MeshEntry>();
	
	/**
	 * The {@code EdgeTraveler} the mesh was generated for
	 */
	private EdgeTraveler traveler;
	
	/**
	 * The source {@code Node}
	 */
	private Node sourceNode;
	
	/**
	 * The paths to each node using this {@code Mesh}
	 */
	private Map<Node, Path> paths;
	
	public Mesh(EdgeTraveler traveler, Node source, Map<Node, Node> cameFrom, Map<Node, Integer> costs) {
		this.traveler = traveler;
		this.sourceNode = source;
		// Compile the information into a single map
		for (Node n : cameFrom.keySet()) {
			meshMap.put(n, new MeshEntry(cameFrom.get(n), costs.get(n)));
		}
	}
	
	/**
	 * Retrieves the source node
	 * 
	 * @return
	 */
	public Node getSourceNode() {
		return this.sourceNode;
	}
	
	/**
	 * Retrieves the {@code MeshEntry} for the given {@code Node}, or {@code null} if no such entry exists
	 * 
	 * @param n
	 * @return
	 */
	public MeshEntry getEntry(Node n) {
		return meshMap.get(n);
	}
	
	/**
	 * Reconstructs the path from the {@code sourceNode} to the given {@code Node}. Assumes that the given
	 * {@code Node} is present in the {@code Mesh}
	 * 
	 * @param n
	 * @return
	 */
	public Path getPathTo(Node n) {
		// Take some of the hard work off us...
		if (pathsGenerated() && paths.containsKey(n)) {
			return paths.get(n);
		}
		Node last = n;
		// rip
		List<Node> nodes = new LinkedList<Node>();
		nodes.add(n);
		int cost = meshMap.get(n).getCost();
		while (meshMap.containsKey(n)) {
			n = meshMap.get(n).getPrevious();
			nodes.add(n);
		}
		Collections.reverse(nodes);
		return new Path(traveler, sourceNode, last, nodes, cost);
	}
	
	/**
	 * Checks whether paths have been created yet
	 * 
	 * @return
	 */
	public boolean pathsGenerated() {
		return paths != null;
	}
	
	/**
	 * Generates all possible paths in this {@code Mesh}. I'm expecting Path generation to take a while, so we
	 * don't generate all paths automatically
	 */
	public void generatePaths() {
		paths = new HashMap<Node, Path>();
		for (Node n : meshMap.keySet()) {
			paths.put(n, getPathTo(n));
		}
	}
	
	/**
	 * Prints the mesh to {@code System.out}
	 */
	public void print() {
		meshMap.forEach((n, e) -> {
			System.out.println(n + ", prev = " + e.getPrevious() + ", cost = " + e.getCost());
		});
	}
	
	/**
	 * An entry into the mesh map, containing the node to come from and the cost for travelling to that node
	 * 
	 * @author Kevin
	 */
	public static class MeshEntry {
		
		
		/**
		 * The node to come from that produces the cheapest path
		 */
		private Node previous = null;
		
		/**
		 * The cost of coming from the previous node
		 */
		private int cost = Integer.MAX_VALUE;
		
		/**
		 * Initializes the {@code MeshEntry} with the given previous Node and cost
		 * 
		 * @param node
		 * @param cost
		 */
		protected MeshEntry(Node node, int cost) {
			this.previous = node;
			this.cost = cost;
		}
		
		/**
		 * Retrieves the node to come from to produce the cheapest path
		 * 
		 * @return
		 */
		public Node getPrevious() {
			return this.previous;
		}
		
		/**
		 * Retrieves the cost of getting to this node in total
		 * 
		 * @return
		 */
		public int getCost() {
			return this.cost;
		}
	}
}
