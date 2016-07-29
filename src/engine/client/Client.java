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
import java.net.Socket;

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
import engine.event.game.ConnectionEstablishedEvent;
import engine.network.Connection;
import engine.network.packet.Packet;
import engine.network.packet.PacketChat;
import engine.network.packet.PacketPing;
import engine.network.packet.udp.PacketUDP;

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
	 * The {@code EventBus} used by the {@code Client} to process {@code GameEvent}s
	 */
	public static EventBus CLIENT_BUS = new EventBus();
	
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
	protected Connection connection;
	
	/**
	 * The {@code Socket} between the {@code Client} and {@code Server}
	 */
	protected Socket socket;
	
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
		this.gfxDvc = gfxEnv.getDefaultScreenDevice();
		this.gfxCfg = gfxDvc.getDefaultConfiguration();
		this.dm = gfxDvc.getDisplayMode();
		
		this.setMinimumSize(new Dimension(WIDTH * (int) SCALE, HEIGHT * (int) SCALE));
		this.setMaximumSize(new Dimension(WIDTH * (int) SCALE, HEIGHT * (int) SCALE));
		this.setPreferredSize(new Dimension(WIDTH * (int) SCALE, HEIGHT * (int) SCALE));
		
		frame = new JFrame(g.name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		this.recreateVImg();
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
		this.gfxDvc = gfxEnv.getDefaultScreenDevice();
		this.gfxCfg = gfxDvc.getDefaultConfiguration();
		this.dm = this.gfxDvc.getDisplayMode();
		
		frame = new JFrame(g.name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		if (this.gfxDvc.isFullScreenSupported()) {
			frame.setUndecorated(true);
			gfxDvc.setFullScreenWindow(frame);
		}
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		this.SCALE = (double) dm.getWidth() / (double) WIDTH;
		
		this.recreateVImg();
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
			this.socket = new Socket(host, port);
			return connect();
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
			this.socket = new Socket(address, port);
			return connect();
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
//		if (!this.socket.getInetAddress().isReachable(10000)) {
//			return false;
//		}
		this.connection = new Connection(this.socket, "Client-Side");
		this.player = this.game.getNewPlayerInstance();
		this.player.name = desiredUsername;
		this.sendPacket(new PacketChat(this.player));
		Client.CLIENT_BUS.post(new ConnectionEstablishedEvent(this.game, this.connection));
		return true;
	}
	
	/**
	 * Attempts to disconnect from the {@code Server} the {@code Client} is connected to
	 */
	public void disconnect() {
		try {
			this.connection.wakeThreads();
			this.socket.close();
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
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			requestFocus();
			return;
		}
		
		if ((this.menu != null && this.menu.rendersGame()) || this.menu == null) {
			this.game.level.render(screen);
			this.renderGame(screen);
			if (this.hud != null) {
				this.hud.render(screen);
			}
		}
		if (this.menu != null) {
			this.menu.render(screen);
		}
		if (!this.hasFocus()) {
			this.renderFocusNagger();
		}
		
		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());
		
		renderOffScreen();
		renderOnScreen(g);
		
		g.dispose();
		bs.show();
	}
	
	private void recreateVImg() {
		this.vImg = this.gfxCfg.createCompatibleVolatileImage(WIDTH, HEIGHT);
	}
	
	/**
	 * Renders the image offscreen
	 */
	private void renderOffScreen() {
		do {
			if (vImg.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				recreateVImg();
			}
			Graphics2D g = vImg.createGraphics();
			
			g.dispose();
		} while (vImg.contentsLost());
	}
	
	/**
	 * Renders the image onscreen
	 * 
	 * @param graphics
	 */
	private void renderOnScreen(Graphics graphics) {
		do {
			int returnCode = vImg.validate(getGraphicsConfiguration());
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// Contents need to be restored
				renderOffScreen();      // restore contents
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				recreateVImg();
				renderOffScreen();
			}
			double ww = WIDTH * SCALE;
			double hh = HEIGHT * SCALE;
			double xo = (getWidth() - ww) / 2;
			double yo = (getHeight() - hh) / 2;
			graphics.drawImage(vImg, (int) xo, (int) yo, (int) ww, (int) hh, null);
			if (cursor != null) {
				graphics.drawImage(cursor.getImage(), (int) (input.getMouseXAbsolute() - (cursor.width / 2)),
						(int) (input.getMouseYAbsolute() - (cursor.height / 2)), null);
			}
		} while (vImg.contentsLost());
//		System.out.println(vImg.getClass().getName());
	}
	
	/**
	 * A tick of game time on the client side. Should be used to manage menus and synchronizing data from the
	 * server
	 */
	public void tick() {
		if (!hasFocus()) {
			input.releaseAll();
		} else {
			input.tick();
			
			if (this.menu != null) {
				this.menu.tick();
			}
			if (this.connection != null) {
				this.processReceivedPackets();
				if (Engine.getGameTimeClient() % PacketPing.PING_PERIOD == 0) {
					this.sendPacket(new PacketPing(System.currentTimeMillis()));
					System.out.println("Ping: " + this.connection.ping + " ms");
				}
			}
		}
		
		this.game.tickClient(this);
		
		this.tickClient();
	}
	
	/**
	 * Schedules a {@code Packet} for sending to the server
	 * 
	 * @param p
	 *            The {@code Packet} to send
	 */
	public void sendPacket(Packet p) {
		this.connection.addToSendQueue(p);
	}
	
	/**
	 * Schedules a {@code PacketUDP} for sending to the server
	 * 
	 * @param p
	 *            The {@code PacketUDP} to send
	 */
	public void sendUDPPacket(PacketUDP p) {
		this.connection.addUDPToSendQueue(p);
	}
	
	/**
	 * Process all {@code Packet}s received from the server.
	 * <p>
	 * May result in a bunch of code, depending on how many {@code Packet}s are flying around.
	 * 
	 * @param packets
	 *            A {@code LinkedList} of {@code Packet}s to process
	 */
	protected void processReceivedPackets() {
		Packet p;
		while ((p = this.connection.getReadPacket()) != null) {
			p.processClient(this);
		}
		PacketUDP udp;
		while ((udp = this.connection.getReadUDPPacket()) != null) {
			udp.processClient(this);
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
			m.init(this, input);
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
			h.init(this, input);
		}
	}
	
	/**
	 * Renders the annoying focus nagger
	 */
	private void renderFocusNagger() {
		String msg = "Click to focus!";
		int x = FontWrapper.getXCoord(screen, msg);
		int y = HEIGHT / 2;
		FontWrapper.draw(msg, screen, x, y, Color.WHITE.getRGB());
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
		screen = new Screen(this, WIDTH, HEIGHT);
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
