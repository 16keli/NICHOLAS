package engine.util.math.graph;

import java.util.ArrayList;
import java.util.List;

import engine.util.math.graph.pathfind.Mesh;
import engine.util.math.graph.pathfind.PathFinder;

/**
 * A basic implementation of the {@code Graph} Interace
 * 
 * @author Kevin
 */
public class BasicGraph implements Graph {
	
	
	private List<Node> nodes = new ArrayList<Node>();
	
	@Override
	public List<Node> getNodes() {
		return nodes;
	}
	
	@Override
	public void addNode(Node n) {
		nodes.add(n);
	}
	
	@Override
	public void connectNodes(Node start, Node end, int cost, boolean directed) {
		Edge e = new Edge(start, end, cost, directed);
		start.addEdge(e);
		end.addEdge(e);
	}
	
	// Test
	public static void main(String[] args) {
		Graph test = new BasicGraph();
		Node a = new BasicNode("A");
		Node b = new BasicNode("B");
		Node c = new BasicNode("C");
		Node d = new BasicNode("D");
		Node e = new BasicNode("E");
		Node z = new BasicNode("Z");
		test.addNode(a);
		test.addNode(b);
		test.addNode(c);
		test.addNode(d);
		test.addNode(e);
		test.addNode(z);
		test.connectNodes(a, b, 4, false);
		test.connectNodes(a, c, 2, false);
		test.connectNodes(b, c, 1, false);
		test.connectNodes(b, d, 5, false);
		test.connectNodes(c, d, 8, false);
		test.connectNodes(c, e, 10, false);
		test.connectNodes(d, e, 2, false);
		test.connectNodes(d, z, 6, false);
		test.connectNodes(e, z, 3, false);
		Mesh mesh = PathFinder.Dijkstra(new BasicEdgeTraveler(), a);
		mesh.generatePaths();
		mesh.getPathTo(z).print();
		mesh.print();
	}
	
}
