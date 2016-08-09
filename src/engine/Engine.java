package engine;

import java.util.Random;
import java.util.logging.Logger;

import engine.client.Client;
import engine.client.InputHandler;
import engine.server.Server;

/**
 * The implementation of the game engine, a wrapper class that handles everything that needs to be handled by
 * itself. There should be little to no modification necessary, and the only calls truly necessary are to
 * start the {@code Client} and {@code Server}
 * <p>
 * To use the {@code Engine} class, call its {@link #startClient(Client)} or {@link #startServer(Server)}
 * methods with a game instance in order to run it
 * <p>
 * In order to run {@code Server} and {@code Client}, the {@code Engine} creates separate {@code Thread}s for
 * each, which then run independently of each other. As a result, in order to resolve concurrency issues, the
 * {@code Client} and {@code Server} should each have their own instance of a {@code Game}, with data being
 * synchronized through use of {@code Packet}s.
 * <p>
 * The {@code Engine} runs on a tick-based system, which is defined by {@link #TICK_RATE} which is
 * {@value #TICK_RATE} ticks/second. This tick rate is the rate at which {@code Client} and {@code Server} are
 * updated. On the {@code Client} side, there also exists rendering of the {@code Game} (obviously) which
 * depends on {@link #VSYNC} ({@value #VSYNC}) as well as {@link #FRAME_RATE} which is {@value #FRAME_RATE}
 * frames/second. If {@code VSYNC} is activated, then the {@code Client} will try to render at (as smooth as
 * possible) {@value #FRAME_RATE} frames/second, otherwise it will render whenever it can.
 * <p>
 * Also features some utility! For a single {@code Random} instance, there exists {@link #rand}, and for
 * {@code File} usage there is {@link #getFilePath()} to get to the directory where files should be stored.
 * <p>
 * Psst Do you know the future? CLASSLOADING! REMEMBER THAT!
 * 
 * @author Kevin
 */
public class Engine {
	
	public static final Logger ENGINE_LOGGER = Logger.getLogger("engine");
	
	/**
	 * The {@code Client} instance
	 */
	private Client client;
	
	/**
	 * The {@code Server} instance
	 */
	private Server server;
	
	/**
	 * The {@code Random} instance, for any random number generation needs
	 */
	public static final Random rand = new Random();
	
	/**
	 * Whether to lock the frame rate
	 */
	public static final boolean VSYNC = false;
	
	/**
	 * The amount of ticks (ideally) in one second
	 */
	public static final int TICK_RATE = 60;
	
	/**
	 * The maximum frame rate of the game
	 */
	public static final int FRAME_RATE = 60;
	
	/**
	 * The sole instance of the {@code Engine}
	 */
	public static Engine instance = new Engine();
	
	/**
	 * The shortcut {@code String} for the filePath where data is stored
	 */
	public String filePath = "";
	
	private ClientThread cThread;
	
	private ServerThread sThread;
	
	/**
	 * The server thread
	 */
	private Thread serverThread;
	
	/**
	 * The client thread
	 */
	private Thread clientThread;
	
	/**
	 * A {@code Thread} that the {@code Engine} uses to control gameplay
	 * 
	 * @author Kevin
	 */
	protected static abstract class EngineThread implements Runnable {
		
		/**
		 * Whether the {@code Engine} should be running
		 */
		protected boolean running = true;
		
		/**
		 * The game time, in ticks, that has passed since this thread has launched
		 */
		public int gameTime = 0;
		
		public abstract void tick();
		
		public abstract void init();
		
		public void stop() {
			this.running = false;
		}
	}
	
	/**
	 * The {@code EngineThread} used to control {@code Client}-based activity
	 * 
	 * @author Kevin
	 */
	protected static class ClientThread extends EngineThread {
		
		private Client c;
		
		public ClientThread(Client c) {
			this.c = c;
		}
		
		@Override
		public void run() {
			long lastTime = System.nanoTime();
			double uTick = 0;
			double uFrame = 0;
			double nsPerTick = 1000000000.0 / TICK_RATE;
			double nsPerFrame = 1000000000.0 / FRAME_RATE;
			int frames = 0;
			int ticks = 0;
			long lastTimer1 = System.currentTimeMillis();
			
			this.init();
			
			while (this.running) {
				long now = System.nanoTime();
				uTick += (now - lastTime) / nsPerTick;
				uFrame += (now - lastTime) / nsPerFrame;
				lastTime = now;
				boolean shouldRender = !VSYNC;
				while (uTick >= 1) {
					ticks++;
					this.tick();
					uTick -= 1;
				}
				
				while (uFrame >= 1) {
					uFrame--;
					shouldRender = true;
				}
				
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (shouldRender) {
					frames++;
					this.render();
				}
				
				if (System.currentTimeMillis() - lastTimer1 > 1000) {
					lastTimer1 += 1000;
					System.out.println("CLIENT:\t" + ticks + " ticks, " + frames + " fps");
					frames = 0;
					ticks = 0;
				}
			}
		}
		
		@Override
		public void tick() {
			this.gameTime++;
			this.c.tick();
		}
		
		@Override
		public void init() {
			this.c.game.init();
			this.c.init();
		}
		
		public void render() {
			this.c.render();
		}
		
	}
	
	/**
	 * The {@code EngineThread} used to control {@code Server}-based activity
	 * 
	 * @author Kevin
	 */
	protected static class ServerThread extends EngineThread {
		
		private Server s;
		
		public ServerThread(Server s) {
			this.s = s;
		}
		
		@Override
		public void run() {
			long lastTime = System.nanoTime();
			double unprocessed = 0;
			double nsPerTick = 1000000000.0 / TICK_RATE;
			int ticks = 0;
			long lastTimer1 = System.currentTimeMillis();
			
			this.init();
			
			while (this.running) {
				long now = System.nanoTime();
				unprocessed += (now - lastTime) / nsPerTick;
				lastTime = now;
				while (unprocessed >= 1) {
					ticks++;
					this.tick();
					unprocessed -= 1;
				}
				
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (System.currentTimeMillis() - lastTimer1 > 1000) {
					lastTimer1 += 1000;
					System.out.println("SERVER:\t" + ticks + " ticks");
					ticks = 0;
				}
			}
		}
		
		@Override
		public void tick() {
			this.gameTime++;
			this.s.tick();
		}
		
		@Override
		public void init() {
			this.s.game.init();
		}
		
	}
	
	private Engine() {
	}
	
	/**
	 * Starts the given client in a JFrame
	 * 
	 * @param c
	 *            The Subclass of {@code Client} to run
	 */
	public static void startClient(Client c) {
		instance.filePath = "res/" + c.game.name.toLowerCase() + "/";
		instance.client = c;
		instance.client.input = new InputHandler(instance.client);
		instance.startClient();
	}
	
	/**
	 * Starts the given server in a JFrame
	 * 
	 * @param s
	 *            The Subclass of {@code Server} to run
	 */
	public static void startServer(Server s) {
		instance.server = s;
		instance.startServer();
	}
	
	/**
	 * Gets the Filepath to the directory used
	 * <p>
	 * Comes in the form "res/gamename/" where gamename is simply the {@code Game}'s name in lowercase.
	 * 
	 * @return
	 */
	public static String getFilePath() {
		return instance.filePath;
	}
	
	public static Server getServer() {
		return instance.server;
	}
	
	public static Client getClient() {
		return instance.client;
	}
	
	/**
	 * Starts the {@code Engine}'s {@code ClientThread}
	 */
	private void startClient() {
		this.cThread = new ClientThread(this.client);
		this.clientThread = new Thread(this.cThread, "Engine Client Thread");
		System.out.println("Client Thread ID:\t" + this.clientThread.getId());
		this.clientThread.start();
	}
	
	/**
	 * Starts the {@code Engine}'s {@code ServerThread}
	 */
	private void startServer() {
		this.sThread = new ServerThread(this.server);
		this.serverThread = new Thread(this.sThread, "Engine Server Thread");
		System.out.println("Server Thread ID:\t" + this.serverThread.getId());
		this.serverThread.start();
	}
	
	/**
	 * Stops the {@code ClientThread}
	 */
	public void stopClient() {
		this.cThread.stop();
	}
	
	/**
	 * Stops the {@code ServerThread}
	 */
	public void stopServer() {
		this.sThread.stop();
	}
	
	/**
	 * Retrieves the game time, in ticks, that have passed since the {@code ClientThread} was started
	 * 
	 * @return
	 */
	public static int getGameTimeClient() {
		return instance.cThread.gameTime;
	}
	
	/**
	 * Retrieves the game time, in ticks, that have passed since the {@code ServerThread} was started
	 * 
	 * @return
	 */
	public static int getGameTimeServer() {
		return instance.sThread.gameTime;
	}
	
}
