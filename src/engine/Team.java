package engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A collection of {@code Player}s that are on the same team for some reason.
 * <p>
 * A rather general implementation. All the rest can be handled through use of subclasses.
 * @author Kevin
 *
 */
public class Team implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static List<Team> teams = new ArrayList<Team>();
	
	/**
	 * The {@code Team}'s name
	 */
	public String name;
	
	/**
	 * The {@code List} of all the {@code Player}s on this {@code Team}
	 */
	public List<Player> players = new LinkedList<Player>();
	
	public Team(String name) {
		this.name = name;
		teams.add(this);
	}

}
