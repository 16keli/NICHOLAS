package engine;

import engine.network.synchro.Rebuildable;

/**
 * A Player of the Game.
 * <p>
 * Note that creation of {@code Player} instances should <b>NEVER</b> be done through the {@code protected}
 * constructors! (Not like you could do that for an abstract class, anyways) In order to create instances, use
 * the corresponding methods in {@code Game}, which are {@link engine.Game#getNewPlayerInstance()} and
 * {@link engine.Game#getNewPlayerInstance(String)}! If you create any {@code Player} instances outside of
 * these methods, you <strike>may</strike> <b>WILL INEVITABLY</b> run into problems with your player number
 * clashing! So don't do it! Also do not forget to override {@link engine.Game#getPlayerClass()}! I don't even
 * know why I'm yelling!
 * <p>
 * Note: The constructors of all subclasses should be public in order for the reflection to work its magic.
 * <p>
 * Did you hear that? If nothing else, <b>MAKE SURE THESE CONSTRUCTORS EXIST!</b>
 * 
 * <pre>
 * 
 * public PlayerSubclass(Game game, short pnum) {
 * 	super(game, pnum);
 * 	// Additional code...
 * }
 * 
 * public PlayerSubclass(Game game, short pnum, String name) {
 * 	super(game, pnum, name);
 * 	// Additional code...
 * }
 * </pre>
 * <p>
 * A List of all {@code Player}s should exist on the side of {@code Game}.
 * <p>
 * <strike>A single {@code Player} with a valid {@code Connection} should exist on the side of {@code Client},
 * which is the {@code Client}'s {@code Player}. Of course, the Client will have access to all of the
 * {@code Player} objects through its {@code Game} instance. {@code Player} instances that belong to
 * {@code Game} instances should have null {@code Connection}s, because there is no way or reason to access
 * them</strike>
 * <p>
 * Hokay that was a stressful time. They've finished the divorce proceedings tho. Them being together was just
 * way too convoluted...
 * 
 * @author Kevin
 */
public abstract class Player implements Rebuildable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Player Number
	 * <p>
	 * -1 means that this object probably should not exist
	 */
	public short number = -1;
	
	/**
	 * The {@code Game} instance
	 */
	public transient Game game;
	
	/**
	 * The Username of the {@code Player}
	 */
	public String name = "Unknown";
	
	/**
	 * Checks whether this {@code Player} has a name
	 */
	public boolean hasName = false;
	
	/**
	 * The {@code Team} that this {@code Player} is on
	 */
	public Team team;
	
	/**
	 * Creates a new Player based on the {@code Game}
	 * 
	 * @param g
	 *            The Game
	 * @param number
	 *            The Player's number
	 * @param name
	 *            The player's username
	 */
	protected Player(Game g, short number, String name) {
		this.game = g;
		this.game.events.register(this);
		this.number = number;
		this.name = name;
		this.hasName = true;
	}
	
	/**
	 * Creates a new Player based on the {@code Game}, without a name
	 * 
	 * @param g
	 *            The Game
	 * @param number
	 *            The Player's number
	 */
	protected Player(Game g, short number) {
		this.game = g;
		this.game.events.register(this);
		this.number = number;
	}
	
	@Override
	public void rebuild(Game g) {
		this.game = g;
		this.game.events.register(this);
	}
	
	public void setPlayerNumber(short num) {
		this.number = num;
	}
	
	/**
	 * Leaves the {@code Team} that the {@code Player} is currently on
	 */
	public void leaveTeam() {
		if (this.team != null) {
			this.team.players.remove(this);
			this.team = null;
		}
	}
	
	/**
	 * Joins the given {@code Team}
	 * 
	 * @param t
	 *            The {@code Team} to join
	 */
	public void joinTeam(Team t) {
		this.team = t;
		t.players.add(this);
	}
	
	public boolean hasName() {
		return hasName;
	}
	
	public void setName(String name) {
		this.name = name;
		this.hasName = true;
	}
	
}
