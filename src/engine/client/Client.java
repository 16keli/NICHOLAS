package engine.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import javax.swing.JFrame;

import engine.Engine;
import engine.Game;
import engine.Player;
import engine.client.graphics.FontWrapper;
import engine.client.graphics.HUD;
import engine.client.graphics.Screen;
import engine.client.graphics.sprite.Sprite;
import engine.client.menu.Menu;
import engine.event.EventBus;
import engine.event.game.TickEvent;
import engine.networknio.ConnectionNIO;
import engine.networknio.packet.PacketChat;
import engine.networknio.packet.PacketNIO;
import engine.networknio.packet.PacketPing;

/**
 * Represents the game client, the component of gameplay that the player interacts with.
 * <p>
 * There are two possible ways to run the client: in {@link #Client(Game, int, int, int) Windowed} mode, or
 * {@link #Client(Game, int, int) Fullscreen} mode.
 * <p>
 * Game data should be synchronized from the Server side via use of {@code Packet}s, and rendering code should
 * go here as well.
 * 
 * @author Kevin
 */
public abstract class Client extends Canvas {
	
	/**
	 * The {@code Client} instance of {@code Logger}
	 */
	public static final Logger logger = Logger.getLogger("engine.client");
	
	/**
	 * The {@code EventBus} used by the {@code Client} to process {@code GameEvent}s
	 */
	public static EventBus CLIENT_BUS = new EventBus("Client Bus");
	
	/**
	 * The {@code Client}'s instance of the {@code Game}
	 */
	public Game game;
	
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	
	/**
	 * The Height of the {@code vImg}
	 */
	public int HEIGHT;
	
	/**
	 * The Width of the {@code vImg}
	 */
	public int WIDTH;
	
	/**
	 * The Scale between monitor pixels and what is considered pixels for the game.
	 * <p>
	 * In other words, any drawing done will be scaled up by this factor.
	 */
	public double SCALE;
	
	/**
	 * The desired username of this {@code Client}
	 */
	protected String desiredUsername = "Steve";
	
	/**
	 * The Player
	 */
	public Player player;
	
	/**
	 * The {@code BufferedImage}'s pixels
	 */
	protected int[] pixels;
	
	/**
	 * The {@code Screen} to render on
	 */
	protected Screen screen;
	
	/**
	 * The {@code InputHander} to handle interactions with the player
	 */
	public InputHandler input;
	
	/**
	 * The current {@code Menu} that the {@code Client} is on
	 * <p>
	 * If {@code menu} is {@code null}, it is assumed that the game is currently being played
	 */
	protected Menu menu;
	
	/**
	 * The current {@code HUD} that the {@code Client} is on
	 */
	protected HUD hud;
	
	/**
	 * The {@code Connection} between the {@code Client} and {@code Server}
	 */
	public ConnectionNIO connection;
	
	/**
	 * The {@code Socket} between the {@code Client} and {@code Server}
	 */
	protected SocketChannel socketChannel;
	
	/**
	 * The remote address that this {@code Client} is connected to
	 */
	protected SocketAddress remoteAddress;
	
	/**
	 * The cursor
	 * <p>
	 * Using the cursor is as simple as initializing this object. The corresponding code in
	 * {@link #renderOnScreen(Graphics)} will draw it.
	 */
	protected Sprite cursor;
	
	/**
	 * The {@code JFrame} that encapsulates the {@code Client}
	 */
	public JFrame frame;
	
	/**
	 * The {@code GraphicsEnvironment}
	 */
	public GraphicsEnvironment gfxEnv;
	
	/**
	 * The default {@code GraphicsDevice}
	 */
	public GraphicsDevice gfxDvc;
	
	/**
	 * The default {@code GraphicsConfiguration}
	 */
	public GraphicsConfiguration gfxCfg;
	
	/**
	 * The current {@code DisplayMode}
	 */
	public DisplayMode dm;
	
	/**
	 * The {@code VolatileImage} that all drawing to happens on
	 */
	public VolatileImage vImg;
	
	/**
	 * Creates a new windowed {@code Client} to interact with the user.
	 * <p>
	 * The Frame's size is based on the parameters w, h, and s, with width w * s and height h * s
	 * 
	 * @param g
	 *            The {@code Game} that the client wraps
	 * @param w
	 *            The width of the vImg, in pixels
	 * @param h
	 *            The height of the vImg, in pixels
	 * @param s
	 *            The scale of the game
	 */
	public Client(Game g, int w, int h, int s) {
		this.game = g;
		this.WIDTH = w;
		this.HEIGHT = h;
		this.SCALE = s;
		Client.CLIENT_BUS.register(this);
		this.gfxEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.gfxDvc = this.gfxEnv.getDefaultScreenDevice();
		this.gfxCfg = this.gfxDvc.getDefaultConfiguration();
		this.dm = this.gfxDvc.getDisplayMode();
		
		this.setMinimumSize(new Dimension(this.WIDTH * (int) this.SCALE, this.HEIGHT * (int) this.SCALE));
		this.setMaximumSize(new Dimension(this.WIDTH * (int) this.SCALE, this.HEIGHT * (int) this.SCALE));
		this.setPreferredSize(new Dimension(this.WIDTH * (int) this.SCALE, this.HEIGHT * (int) this.SCALE));
		
		this.frame = new JFrame(g.getName());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		this.frame.add(this, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setResizable(false);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
		this.recreateVImg();
		
		logger.config("Starting Client in windowed mode. Parameters: Internal Window (w x h) = " + this.WIDTH
				+ " x " + this.HEIGHT + " Scale = " + this.SCALE);
	}
	
	/**
	 * Creates a new fullscreen {@code Client} to interact with the user.
	 * <p>
	 * The internal {@code VolatileImage} is automatically upscaled to fit the entire screen.
	 * 
	 * @param g
	 *            The {@code Game} that the client wraps
	 * @param w
	 *            The width of the vImg, in pixels
	 * @param h
	 *            The height of the vImg, in pixels
	 */
	public Client(Game g, int w, int h) {
		this.game = g;
		this.WIDTH = w;
		this.HEIGHT = h;
		Client.CLIENT_BUS.register(this);
		this.gfxEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.gfxDvc = this.gfxEnv.getDefaultScreenDevice();
		this.gfxCfg = this.gfxDvc.getDefaultConfiguration();
		this.dm = this.gfxDvc.getDisplayMode();
		
		this.frame = new JFrame(g.getName());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		this.frame.add(this, BorderLayout.CENTER);
		if (this.gfxDvc.isFullScreenSupported()) {
			this.frame.setUndecorated(true);
			this.gfxDvc.setFullScreenWindow(this.frame);
		}
		this.frame.pack();
		this.frame.setResizable(false);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
		
		this.SCALE = (double) this.dm.getWidth() / (double) this.WIDTH;
		
		this.recreateVImg();
		
		logger.config("Starting Client in fullscreen mode. Parameters: Internal Window (w x h) = "
				+ this.WIDTH + " x " + this.HEIGHT + " Scale = " + this.SCALE);
	}
	
	/**
	 * Attempts to connect to the given host
	 * 
	 * @param host
	 *            The host name
	 * @param port
	 *            The port to connect on
	 * @return Whether a connection was successful
	 */
	public boolean connect(String host, int port) {
		try {
			this.remoteAddress = new InetSocketAddress(host, port);
			this.socketChannel = SocketChannel.open(this.remoteAddress);
			return this.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Attempts to connect to the given host
	 * 
	 * @param address
	 *            The host's {@code InetAddress}
	 * @param port
	 *            The port to connect on
	 * @return Whether a connection was successful
	 */
	public boolean connect(InetAddress address, int port) {
		try {
			this.remoteAddress = new InetSocketAddress(address, port);
			this.socketChannel = SocketChannel.open(this.remoteAddress);
			return this.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Attempts to connect to the existing {@code Socket}. Has a built-in timeout parameter of 10 seconds.
	 * 
	 * @return Whether a connection was successfully established
	 * @throws IOException
	 *             If an I/O Stream cannot be opened
	 */
	private boolean connect() throws IOException {
		logger.info("Client Attempting Connection to " + this.remoteAddress);
		this.connection = new ConnectionNIO(this.socketChannel, "Client-Side", true);
		this.player = this.game.getNewPlayerInstance();
		this.player.name = this.desiredUsername;
		this.connection.addToSendQueue(new PacketChat(this.player));
		// TODO: Readd this
//		Client.CLIENT_BUS.post(new ConnectionEstablishedEvent(this.game, this.connection));
		return true;
	}
	
	/**
	 * Attempts to disconnect from the {@code Server} the {@code Client} is connected to
	 */
	public void disconnect() {
		try {
			this.connection.disconnect();
			this.socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.player = null;
		this.initClient();
	}
	
	/**
	 * Renders a frame of the game or client
	 */
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			this.requestFocus();
			return;
		}
		
		if ((this.menu != null && this.menu.rendersGame()) || this.menu == null) {
			this.game.level.render(this.screen);
			this.renderGame(this.screen);
			if (this.hud != null) {
				this.hud.render(this.screen);
			}
		}
		if (this.menu != null) {
			this.menu.render(this.screen);
		}
		if (!this.hasFocus()) {
			this.renderFocusNagger();
		}
		
		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		this.renderOffScreen();
		this.renderOnScreen(g);
		
		g.dispose();
		bs.show();
	}
	
	private void recreateVImg() {
		this.vImg = this.gfxCfg.createCompatibleVolatileImage(this.WIDTH, this.HEIGHT);
	}
	
	/**
	 * Renders the image offscreen
	 */
	private void renderOffScreen() {
		do {
			if (this.vImg.validate(this.getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				this.recreateVImg();
			}
			Graphics2D g = this.vImg.createGraphics();
			
			g.dispose();
		} while (this.vImg.contentsLost());
	}
	
	/**
	 * Renders the image onscreen
	 * 
	 * @param graphics
	 */
	private void renderOnScreen(Graphics graphics) {
		do {
			int returnCode = this.vImg.validate(this.getGraphicsConfiguration());
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// Contents need to be restored
				this.renderOffScreen();      // restore contents
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				this.recreateVImg();
				this.renderOffScreen();
			}
			double ww = this.WIDTH * this.SCALE;
			double hh = this.HEIGHT * this.SCALE;
			double xo = (this.getWidth() - ww) / 2;
			double yo = (this.getHeight() - hh) / 2;
			graphics.drawImage(this.vImg, (int) xo, (int) yo, (int) ww, (int) hh, null);
			if (this.cursor != null) {
				graphics.drawImage(this.cursor.getImage(),
						this.input.getMouseXAbsolute() - (this.cursor.width / 2),
						this.input.getMouseYAbsolute() - (this.cursor.height / 2), null);
			}
		} while (this.vImg.contentsLost());
//		System.out.println(vImg.getClass().getName());
	}
	
	/**
	 * A tick of game time on the client side. Should be used to manage menus and synchronizing data from the
	 * server
	 */
	public void tick() {
		this.game.gameTime = Engine.getGameTimeClient();
		this.game.temporaryEvents.post(new TickEvent(this.game.gameTime));
		if (!this.hasFocus()) {
			this.input.releaseAll();
		} else {
			this.input.tick();
			
			if (this.menu != null) {
				this.menu.tick();
			}
			if (this.connection != null) {
				this.processReceivedPackets();
				if (Engine.getGameTimeClient() % PacketPing.PING_PERIOD == 0) {
					this.connection.addToSendQueue(new PacketPing(System.currentTimeMillis()));
					System.out.println("Ping: " + this.connection.ping + " ms");
				}
			}
		}
		
		this.game.tickClient(this);
		
		this.tickClient();
		
		if (this.connection != null) {
			this.connection.sendPackets();
		}
	}
	
	/**
	 * Process all {@code Packet}s received from the server.
	 * <p>
	 * May result in a bunch of code, depending on how many {@code Packet}s are flying around.
	 */
	protected void processReceivedPackets() {
		PacketNIO p;
		while ((p = this.connection.getReadPacket()) != null) {
			p.processClient(this);
		}
	}
	
	/**
	 * Any other ticking that the client may need to do goes here
	 */
	protected abstract void tickClient();
	
	/**
	 * Sets the {@code Client}'s current {@code Menu}.
	 * <p>
	 * Set this to {@code null} in order to go to the game
	 * 
	 * @param m
	 *            The {@code Menu} to set
	 */
	public void setMenu(Menu m) {
		this.menu = m;
		if (m != null) {
			m.init(this, this.input);
		}
	}
	
	/**
	 * Sets the {@code Client}'s current {@code HUD}.
	 * 
	 * @param h
	 *            The {@code HUD} to set
	 */
	public void setHUD(HUD h) {
		this.hud = h;
		if (h != null) {
			h.init(this, this.input);
		}
	}
	
	/**
	 * Renders the annoying focus nagger
	 */
	private void renderFocusNagger() {
		String msg = "Click to focus!";
		int x = FontWrapper.getXCoord(this.screen, msg);
		int y = this.HEIGHT / 2;
		FontWrapper.draw(msg, this.screen, x, y, Color.WHITE.getRGB());
	}
	
	/**
	 * Any extra rendering that may have to occur
	 */
	public abstract void renderGame(Screen screen);
	
	/**
	 * Any {@code Client} initialization that may need to take place should be here
	 */
	protected abstract void initClient();
	
	/**
	 * Any {@code Client} reseting should be here
	 */
	public abstract void resetClient();
	
	/**
	 * Starts the game, initializing any resources required
	 */
	public void init() {
		this.input = new InputHandler(this);
		this.screen = new Screen(this, this.WIDTH, this.HEIGHT);
		this.initClient();
		this.resetClient();
	}
	
	/**
	 * Sets the player number
	 * 
	 * @param p
	 *            The player number
	 */
	public void setPlayerNumber(short p) {
		this.player.setPlayerNumber(p);
	}
	
	/**
	 * Sets the Ping of this {@code Client}'s {@code Connection}
	 * 
	 * @param l
	 *            The Ping in Milliseconds
	 */
	public void setPing(long l) {
		this.connection.setPing(l);
	}
	
	public boolean hasPlayerNumber() {
		return this.player.number != -1;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean menuOpen() {
		return this.menu != null;
	}
	
}
