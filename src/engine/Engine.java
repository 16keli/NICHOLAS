package engine;

import java.io.File;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import engine.client.Client;
import engine.client.InputHandler;
import engine.config.Configuration;
import engine.launcher.LaunchConfig;
import engine.networknio.ConnectionNIO;
import engine.server.Server;

/**
 * The implementation of the game engine, a wrapper class that handles everything that needs to be handled by
 * itself. There should be little to no modification necessary, and the only calls truly necessary are to
 * start the {@code Client} and {@code Server}
 * <p>
 * To use the {@code Engine} class, first {@link #createEngine(LaunchConfig) create} the instance. This will
 * do all the initialization tasks that are required. After that, call its {@link #startClient(Client)} or
 * {@link #startServer(Server)} methods with a game instance in order to run it
 * <p>
 * In order to run {@code Server} and {@code Client}, the {@code Engine} creates separate {@code Thread}s for
 * each, which then run independently of each other. As a result, in order to resolve concurrency issues, the
 * {@code Client} and {@code Server} should each have their own instance of a {@code Game}, with data being
 * synchronized through use of {@code Packet}s.
 * <p>
 * The {@code Engine} runs on a tick-based system, which is defined by {@link #DEFAULT_TICK_RATE} which is
 * {@value #DEFAULT_TICK_RATE} ticks/second. This tick rate is the rate at which {@code Client} and {@code Server} are
 * updated. On the {@code Client} side, there also exists rendering of the {@code Game} (obviously) which
 * depends on {@link #DEFAULT_VSYNC} ({@value #DEFAULT_VSYNC}) as well as {@link #DEFAULT_FRAME_RATE} which is {@value #DEFAULT_FRAME_RATE}
 * frames/second. If {@code VSYNC} is activated, then the {@code Client} will try to render at (as smooth as
 * possible) {@value #DEFAULT_FRAME_RATE} frames/second, otherwise it will render whenever it can.
 * <p>
 * Also features some utility! For a single {@code Random} instance, there exists {@link #rand}, and for
 * {@code File} usage there is {@link #getFilePath()} to get to the directory where files should be stored.
 * <p>
 * Psst Do you know the future? I do! CLASSLOADING!
 * 
 * @author Kevin
 */
public class Engine {
	
	/**
	 * The upper-level {@code Logger} instance. Should be the parent of all loggers, at least within the
	 * {@code Engine} ecosystem.
	 */
	public static final Logger ENGINE_LOGGER = Logger.getLogger("engine");
	
	/**
	 * The {@code Handler} for config and above
	 */
	public static Handler cfgHandler;
	
	/**
	 * The {@code Handler} for all data
	 */
	public static Handler allHandler;
	
	private static final Formatter engineFormatter = new EngineLogFormatter();
	
	static {
		File logDirectory = new File("logs");
		try {
			if (!logDirectory.exists()) {
				logDirectory.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ENGINE_LOGGER.setFilter(null);
		ENGINE_LOGGER.setLevel(Level.ALL);
		try {
			cfgHandler = new FileHandler("logs/infolog%u.%g.txt", 262144, 4);
			allHandler = new FileHandler("logs/alllog%u.%g.txt", 262144, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cfgHandler.setLevel(Level.CONFIG);
		cfgHandler.setFilter(null);
		allHandler.setLevel(Level.ALL);
		allHandler.setFilter(null);
		cfgHandler.setFormatter(engineFormatter);
		allHandler.setFormatter(engineFormatter);
		ENGINE_LOGGER.addHandler(cfgHandler);
		ENGINE_LOGGER.addHandler(allHandler);
	}
	
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
	 * Whether to lock the frame rate to a certain amount
	 */
	public static final boolean DEFAULT_VSYNC = false;
	
	/**
	 * The amount of ticks (ideally) in one second
	 */
	public static final int DEFAULT_TICK_RATE = 60;
	
	/**
	 * The maximum frame rate of the game
	 */
	public static final int DEFAULT_FRAME_RATE = 60;
	
	/**
	 * The default logging behavior
	 */
	public static final boolean DEFAULT_LOG_CONFIG = true;
	
	/**
	 * The default logging behavior
	 */
	public static final boolean DEFAULT_LOG_ALL = true;
	
	/**
	 * The tickRate of this {@code Engine}
	 */
	private int tickRate;
	
	/**
	 * The frameRate of this {@code Engine}
	 */
	private int frameRate;
	
	/**
	 * The frame lock of this {@code Engine}
	 */
	private boolean vSync;
	
	/**
	 * The sole instance of the {@code Engine}
	 * <p>
	 * Not exactly sure why I'm following the Singleton pattern. Oh well
	 */
	public static Engine instance;
	
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
		public long gameTime = 0;
		
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
			double nsPerTick = 1000000000.0 / instance.tickRate;
			double nsPerFrame = 1000000000.0 / instance.frameRate;
			int frames = 0;
			int ticks = 0;
			long lastTimer1 = System.currentTimeMillis();
			
			this.init();
			
			while (this.running) {
				long now = System.nanoTime();
				uTick += (now - lastTime) / nsPerTick;
				uFrame += (now - lastTime) / nsPerFrame;
				lastTime = now;
				boolean shouldRender = !instance.vSync;
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
					ENGINE_LOGGER.log(Level.INFO, "CLIENT:\t" + ticks + " ticks, " + frames + " fps");
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
			double nsPerTick = 1000000000.0 / instance.tickRate;
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
					ENGINE_LOGGER.log(Level.INFO, "SERVER:\t" + ticks + " ticks");
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
	 * Creates a new Engine and executes tasks such as creation of a configuration file
	 * @param config The {@code LaunchConfig}
	 */
	public static void createEngine(LaunchConfig config) {
		if (instance != null) {
			ENGINE_LOGGER.warning("Why are you trying to create another instance of Engine?");
			return;
		}
		instance = new Engine();
		config.config = new Configuration(config);
		config.addProperties();
		config.config.load();
		instance.tickRate = Integer.parseInt(config.config.tickRate.getValue());
		instance.frameRate = Integer.parseInt(config.config.frameRate.getValue());
		instance.vSync = Boolean.parseBoolean(config.config.vSync.getValue());
		if (!Boolean.parseBoolean(config.config.cfgLog.getValue())) {
			cfgHandler.close();
		}
		if (!Boolean.parseBoolean(config.config.allLog.getValue())) {
			allHandler.close();
		}
		ConnectionNIO.TCP_BUFFER_SIZE = Integer.parseInt(config.config.tcpBuff.getValue());
		ConnectionNIO.UDP_BUFFER_SIZE = Integer.parseInt(config.config.udpBuff.getValue());
		config.processProperties();
		
//		for (Entry<Object, Object> e :System.getProperties().entrySet()) {
//			System.out.println("Key " + e.getKey() + " =  " + e.getValue());
//		}
		
//		System.setProperty("sun.java2d.opengl", "true");
	}
	
	/**
	 * Starts the given client in a JFrame
	 * 
	 * @param c
	 *            The Subclass of {@code Client} to run
	 */
	public static void startClient(Client c) {
		instance.filePath = "res/" + c.game.getName().toLowerCase() + "/";
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
		instance.filePath = "res/" + s.game.getName().toLowerCase() + "/";
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
		ENGINE_LOGGER.fine("Client Thread ID:\t" + this.clientThread.getId());
		this.clientThread.start();
	}
	
	/**
	 * Starts the {@code Engine}'s {@code ServerThread}
	 */
	private void startServer() {
		this.sThread = new ServerThread(this.server);
		this.serverThread = new Thread(this.sThread, "Engine Server Thread");
		ENGINE_LOGGER.fine("Server Thread ID:\t" + this.serverThread.getId());
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
	public static long getGameTimeClient() {
		return instance.cThread.gameTime;
	}
	
	/**
	 * Retrieves the game time, in ticks, that have passed since the {@code ServerThread} was started
	 * 
	 * @return
	 */
	public static long getGameTimeServer() {
		return instance.sThread.gameTime;
	}
	
	/**
	 * A Utility class that determines how to format data in the Log files
	 * 
	 * @author Kevin
	 */
	public static class EngineLogFormatter extends Formatter {
		
		@Override
		public String format(LogRecord record) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(record.getMillis());
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(String.format("%2s", c.get(Calendar.HOUR_OF_DAY)).replace(' ', '0'));
			sb.append(':');
			sb.append(String.format("%2s", c.get(Calendar.MINUTE)).replace(' ', '0'));
			sb.append(':');
			sb.append(String.format("%2s", c.get(Calendar.SECOND)).replace(' ', '0'));
			sb.append("] [");
			sb.append(record.getLoggerName());
			sb.append("] [");
			sb.append(record.getSourceClassName());
			sb.append('/');
			sb.append(record.getSourceMethodName());
			sb.append('/');
			sb.append(record.getLevel());
			sb.append("]:\t");
			sb.append(record.getMessage());
			sb.append('\n');
			return sb.toString();
		}
	}
	
}
