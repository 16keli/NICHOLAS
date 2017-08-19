package engine.util.math.graph;

/**
 * A basic edge traveler that can travel on all edges and has no additional traversal cost for moving along
 * edges
 * 
 * @author Kevin
 */
public class BasicEdgeTraveler implements EdgeTraveler {
	
	
	public BasicEdgeTraveler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canTraverseEdge(Edge edge) {
		return true;
	}
	
	@Override
	public int getAdditionalTraversalCost(Edge edge) {
		return 0;
	}
	
}
