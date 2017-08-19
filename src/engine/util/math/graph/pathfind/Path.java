package engine.util.math.graph.pathfind;

import java.io.Serializable;
import java.util.List;

import engine.util.math.graph.EdgeTraveler;
import engine.util.math.graph.Node;

/**
 * Essentially a wrapper class that represents a valid path that an {@code EdgeTraveler} can take on its way
 * to its destination
 * 
 * @author Kevin
 */
public class Path implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The {@code EdgeTraveler} that is moving
	 */
	public EdgeTraveler traveler;
	
	/**
	 * The {@code Node} that the {@code EdgeTraveler} starts on
	 */
	public Node start;
	
	/**
	 * The {@code Node} that the {@code EdgeTraveler} ends on
	 */
	public Node end;
	
	/**
	 * A List of {@code Node}s between the start and end points
	 */
	public List<Node> nodes;
	
	/**
	 * The total cost of moving
	 */
	public int cost;
	
	protected Path(EdgeTraveler traveler, Node start, Node end, List<Node> nodes, int cost) {
		this.traveler = traveler;
		this.start = start;
		this.end = end;
		this.nodes = nodes;
		this.cost = cost;
	}
	
	/**
	 * Prints this path
	 */
	public void print() {
		System.out.print("Path from " + start + " to " + end + ": [");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i) + ((i == nodes.size() - 1) ? "" : ", "));
		}
		System.out.println("], cost: " + this.cost);
	}
	
//	/**
//	 * Checks whether this Path is, in fact, valid.
//	 * 
//	 * @return
//	 */
//	public boolean isValidPath() {
//		return this.traveler.actions.getValue() <= this.cost;
//	}
	
	/**
	 * Checks whether this {@code UnitPath} is the one with the given parameters
	 * 
	 * @param traveler
	 *            The {@code EntityTiled}
	 * @param goal
	 *            The {@code Node} destination
	 * @return
	 */
	boolean isPathWith(EdgeTraveler traveler, Node goal) {
		return traveler.equals(this.traveler) && goal.equals(this.end);
	}
}
