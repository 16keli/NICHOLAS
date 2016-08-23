package engine.launcher;

import engine.Game;
import engine.client.Client;
import engine.config.Configuration;
import engine.server.Server;

/**
 * A Helper class that is used to determine which classes are grouped together, as well as read configuration
 * data from the file.
 * 
 * @author Kevin
 */
public abstract class LaunchConfig {
	
	/**
	 * The {@code Configuration} instance
	 */
	public Configuration config;
	
	/**
	 * Add any necessary properties to the given {@code Configuration}
	 */
	public abstract void addProperties();
	
	/**
	 * After the properties have been read and values assigned, do stuff with them!
	 */
	public abstract void processProperties();
	
	/**
	 * Gets the {@code Game} class for this game
	 * @return
	 */
	public abstract Class<? extends Game> getGameClass();
	
	/**
	 * Gets the {@code Client} class for this game
	 * @return
	 */
	public abstract Class<? extends Client> getClientClass();
	
	/**
	 * Gets the {@code Server} class for this game
	 * @return
	 */
	public abstract Class<? extends Server> getServerClass();
	
}
