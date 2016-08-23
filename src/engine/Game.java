package engine;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import engine.client.Client;
import engine.event.EventBus;
import engine.level.Entity;
import engine.level.Level;
import engine.server.Server;

/**
 * A representation of a game. Be aware of the existence of {@link engine.physics.Physics} in case you want to
 * add Physics, or simply a List of {@code Entity}s.
 * <p>
 * Any code common to both Client and Server (such as levels, players, and other gameplay mechanics) should be
 * placed inside {@code Game}. The gameplay should be updated on the Server side by {@link #tick(Server)} and
 * {@link #tickServer(Server)} synchronized on the Client side based on {@code Packet}s sent by the Server in
 * {@link engine.server.Server#sendPacket(Packet, Connection)} and
 * {@link engine.server.Server#synchronizeClientGameData(Player)}
 * <p>
 * If the core gameplay mechanics have to change at all, consider creating another subclass of {@code Game}.
 * 
 * @author Kevin
 */
public abstract class Game {
	
	/**
	 * A debug {@code Logger} for use in debugging I guess
	 */
	public static final Logger logger = Logger.getLogger("engine.game");
	
	/**
	 * The current game time
	 */
	public long gameTime;
	
	/**
	 * The {@code Game}'s {@code EventBus} for posting {@code Event}s.
	 * <p>
	 * For {@code Event}s concerning {@code EntityPhysics}, use {@link engine.physics.Physics#PHYSICS_BUS}
	 * 
	 * @see engine.physics.Physics.ENTITY_BUS
	 */
	public EventBus events = new EventBus("Game Events");
	
	/**
	 * The {@code Game}'s {@code EventBus} for {@code ITemporaryEventListener}s. No special handling really,
	 * only to separate the two {@code EventBus}es to reduce stress
	 */
	public EventBus temporaryEvents = new EventBus("Game Temporary Events");
	
	/**
	 * The current {@code Level} being played.
	 */
	public Level level;
	
	/**
	 * Whether the game is started or not.
	 */
	public boolean start = false;
	
	/**
	 * A list of {@code Player}s
	 */
	public List<Player> players = new ArrayList<Player>();
	
	/**
	 * The next available Player Number
	 */
	public short nextPlayerNumber = 0;
	
	/**
	 * Creates a new {@code Game}
	 */
	public Game() {
		this.events.register(this);
	}
	
	/**
	 * Sets the {@code Level} of the game in question
	 * 
	 * @param l
	 *            The {@code Level} to set
	 */
	public void setLevel(Level l) {
		this.level = l;
	}
	
	/**
	 * Ticks the game. Be aware that the tickrate is specified in {@code Engine} as a static final int.
	 */
	public void tick(Server s) {
		if (this.start) {
			this.level.tick();
			this.tickServer(s);
		}
	}
	
	/**
	 * Any ticking that {@code Client}s running this game should do
	 */
	public abstract void tickClient(Client c);
	
	/**
	 * Any ticking that the {@code Server} hosting this game should do
	 */
	protected abstract void tickServer(Server s);
	
	/**
	 * Called when first initializing the game. This is not called from the constructor, rather from the
	 * thread that starts either the Client or the Server.
	 */
	protected abstract void init();
	
	/**
	 * Called to reset the game, if necessary.
	 */
	public abstract void resetGame();
	
	/**
	 * Gets an Entity
	 * 
	 * @param id
	 *            The ID of the Entity
	 * @return
	 */
	public Entity getEntity(int id) {
		return this.level.getEntity(id);
	}
	
	/**
	 * Gets a new {@code Player} instance
	 * 
	 * @return A new {@code Player} instance
	 */
	public Player getNewPlayerInstance() {
		Class<? extends Player> cls = this.getPlayerClass();
		try {
			Constructor<? extends Player> cst = cls.getConstructor(Game.class, short.class);
			Player p = cst.newInstance(this, this.nextPlayerNumber++);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets a new {@code Player} instance with the given name
	 * 
	 * @param name
	 *            The name
	 * @return A new {@code Player} instance
	 */
	public Player getNewPlayerInstance(String name) {
		Class<? extends Player> cls = this.getPlayerClass();
		try {
			Constructor<? extends Player> cst = cls.getConstructor(Game.class, short.class, String.class);
			Player p = cst.newInstance(this, this.nextPlayerNumber++, name);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the {@code Player} subclass used by this {@code Game}
	 * 
	 * @return
	 */
	public abstract Class<? extends Player> getPlayerClass();
	
	/**
	 * Gets the name of this game
	 * @return
	 */
	public String getName() {
		return getName(this.getClass());
	}
	
	/**
	 * Gets the name of the game class
	 * @param game
	 * @return
	 */
	public static String getName(Class<? extends Game> game) {
		return game.getSimpleName();
	}
	
}
