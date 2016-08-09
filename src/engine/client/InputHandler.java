package engine.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import engine.Engine;
import engine.level.Vector2;

/**
 * Provides the framework for Keyboard and Mouse input on the Client side
 * 
 * @author Kevin
 */
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	// private Game g;
	private File cfg;
	
	private double scale;
//	void readConfig() {
//		cfg = new File(Engine.instance.filePath + "config.txt");
//		try {
//			Scanner s = new Scanner(cfg);
//			while (s.hasNext()) {
//				String l = s.nextLine();
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public InputHandler(Client client) {
		System.setProperty("sun.awt.enableExtraMouseButtons", "true");
		client.addKeyListener(this);
		client.addMouseListener(this);
		client.addMouseMotionListener(this);
		client.addMouseWheelListener(this);
		this.scale = client.SCALE;
		// this.g = game;
		this.cfg = new File(Engine.instance.filePath + "keycfg.txt");
		if (!this.cfg.exists()) {
			try {
				this.cfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		this.readBinds();
	}
	
	/**
	 * Represents an input. Be sure to define your own {@code Key}s somewhere in your game. At the moment,
	 * keys are limited to a single binding.
	 */
	public class Input {
		
		public int presses = 0, absorbs = 0;
		
		public boolean down = false, clicked = false;
		
		/**
		 * The {@code KeyEvent} or {@code MouseEvent} that this key is bound to by default
		 */
		public int def;
		
		/**
		 * Toggles the {@code Input}
		 * 
		 * @param pressed
		 */
		public void toggle(boolean pressed) {
			if (pressed != this.down) {
				this.down = pressed;
			}
			if (pressed) {
				this.presses++;
			}
		}
		
		/**
		 * Ticks
		 */
		public void tick() {
			if (this.absorbs < this.presses) {
				this.absorbs++;
				this.clicked = true;
			} else {
				this.clicked = false;
			}
		}
	}
	
	public List<Input> inputs = new ArrayList<Input>();
	
	/**
	 * Represents a key. Be sure to define your own {@code Key}s somewhere in your game. At the moment, keys
	 * are limited to a single binding.
	 */
	public class Key extends Input {
		
		/**
		 * The {@code KeyCode} that this key is bound to.
		 * <p>
		 * When changing, please call {@code changeBinding} to change a binding or {@code addBinding} to add
		 * one.
		 */
		public List<Integer> binds = new LinkedList<Integer>();
		
		/**
		 * Used if the default Binding and the current Binding are the same
		 * 
		 * @param def
		 *            The {@code KeyCode} of the default and current binding
		 */
		public Key(int def) {
			this(def, def);
		}
		
		/**
		 * Used if the default Binding differs from the current Binding
		 * 
		 * @param def
		 * @param bind
		 */
		public Key(int def, int bind) {
			InputHandler.this.keys.add(this);
			InputHandler.this.inputs.add(this);
			this.def = def;
			this.binds.add(bind);
		}
		
		/**
		 * Changes the binding of the key from old to new
		 * 
		 * @param old
		 *            The previous binding
		 * @param new
		 *            The new binding
		 */
		public void changeBinding(int oldb, int newb) {
			int ind;
			if ((ind = this.binds.indexOf(oldb)) != -1) {
				this.binds.set(ind, newb);
			}
			this.writeBinds();
		}
		
		/**
		 * Adds a new binding to the list of binds
		 * 
		 * @param bind
		 *            The binds to add
		 */
		public void addBinding(int... bind) {
			List<Integer> bl = new ArrayList<Integer>(bind.length);
			for (int i = 0; i < bl.size(); i++) {
				bl.set(i, bind[i]);
			}
			this.binds.addAll(bl);
			this.writeBinds();
		}
		
		/**
		 * Removes bindings from the list of binds
		 * 
		 * @param bind
		 *            The binds to remove
		 */
		public void removeBinding(int... bind) {
			List<Integer> bl = new ArrayList<Integer>(bind.length);
			for (int i = 0; i < bl.size(); i++) {
				bl.set(i, bind[i]);
			}
			this.binds.removeAll(bl);
			this.writeBinds();
		}
		
		public void write(PrintStream out) {
			out.println(this.def);
			for (Integer i : this.binds) {
				out.println("," + i);
			}
		}
		
		public void readBinds() {
			try (Scanner s = new Scanner(InputHandler.this.cfg)) {
				while (s.hasNext()) {
					String l = s.nextLine();
					String bs[] = l.split(",");
					int def = Integer.parseInt(bs[0]);
					for (int i = 0; i < InputHandler.this.keys.size(); i++) {
						if (InputHandler.this.keys.get(i).def == def) {
							for (int j = 1; j < bs.length; j++) {
								InputHandler.this.keys.get(i).addBinding(Integer.parseInt(bs[j]));
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void writeBinds() {
			try (PrintStream out = new PrintStream(InputHandler.this.cfg)) {
				for (Key k : InputHandler.this.keys) {
					k.write(out);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Key> keys = new ArrayList<Key>();
	
	public Key escape = new Key(KeyEvent.VK_ESCAPE);
	
	public Key up = new Key(KeyEvent.VK_UP, KeyEvent.VK_W);
	
	public Key down = new Key(KeyEvent.VK_DOWN, KeyEvent.VK_S);
	
	public Key up2 = new Key(KeyEvent.VK_I);
	
	public Key down2 = new Key(KeyEvent.VK_K);
	
	public Key left = new Key(KeyEvent.VK_LEFT, KeyEvent.VK_A);
	
	public Key right = new Key(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
	
	public Key left2 = new Key(KeyEvent.VK_J);
	
	public Key right2 = new Key(KeyEvent.VK_L);
	
	public Key enter = new Key(KeyEvent.VK_ENTER);
	
	public Key space = new Key(KeyEvent.VK_SPACE);
	
	public Key tab = new Key(KeyEvent.VK_TAB);
	
//	public Key up = new Key();
//	public Key down = new Key();
//	public Key left = new Key();
//	public Key right = new Key();
//	public Key attack = new Key();
//	public Key menu = new Key();
	
	public void releaseAll() {
		this.releaseAllKeys();
		this.releaseAllMice();
	}
	
	public void releaseAllKeys() {
		for (int i = 0; i < this.keys.size(); i++) {
			this.keys.get(i).down = false;
		}
	}
	
	public void releaseAllMice() {
		for (int i = 0; i < this.mice.size(); i++) {
			this.mice.get(i).down = false;
		}
	}
	
	public void tick() {
		for (int i = 0; i < this.inputs.size(); i++) {
			this.inputs.get(i).tick();
		}
		this.mouseWheel.tick();
	}
	
	@Override
	public void keyPressed(KeyEvent ke) {
		this.toggleKey(ke, true);
	}
	
	@Override
	public void keyReleased(KeyEvent ke) {
		this.toggleKey(ke, false);
	}
	
	private void toggleKey(KeyEvent ke, boolean pressed) {
		for (int i = 0; i < this.keys.size(); i++) {
			for (Integer b : this.keys.get(i).binds) {
				if (ke.getKeyCode() == b.intValue()) {
					this.keys.get(i).toggle(pressed);
				}
			}
		}
	}
	
	/**
	 * Represents a MouseButton. There is no real need to implement more
	 */
	public class Mouse extends Input {
		
		public int bind;
		
		public int x, y;
		
		public Mouse(int bind) {
			InputHandler.this.mice.add(this);
			InputHandler.this.inputs.add(this);
			this.bind = bind;
		}
	}
	
	public List<Mouse> mice = new ArrayList<Mouse>();
	
	public Mouse mouseLeft = new Mouse(MouseEvent.BUTTON1);
	
	public Mouse mouseRight = new Mouse(MouseEvent.BUTTON3);
	
	public Mouse mouseMiddle = new Mouse(MouseEvent.BUTTON2);
	
	public Mouse mouseLeftSide = new Mouse(4);
	
	public Mouse mouseRightSide = new Mouse(5);
	
	@Override
	public void mousePressed(MouseEvent e) {
		this.toggleMouse(e, true);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		this.toggleMouse(e, false);
	}
	
	private void toggleMouse(MouseEvent e, boolean b) {
		for (int i = 0; i < this.mice.size(); i++) {
			this.mice.get(i).x = e.getX();
			this.mice.get(i).y = e.getY();
			if (e.getButton() == this.mice.get(i).bind) {
				this.mice.get(i).toggle(b);
			}
		}
	}
	
	private int mouseX;
	
	private int mouseY;
	
	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}
	
	public Vector2 getMousePos() {
		return Vector2.of(this.getMouseX(), this.getMouseY());
	}
	
	public double getMouseX() {
		return this.mouseX / this.scale;
	}
	
	public double getMouseY() {
		return this.mouseY / this.scale;
	}
	
	public int getMouseXAbsolute() {
//		System.out.println("X:\t" + this.mouseX);
		return this.mouseX;
	}
	
	public int getMouseYAbsolute() {
//		System.out.println("Y:\t" + this.mouseY);
		return this.mouseY;
	}
	
	public class MouseWheel {
		
		public int rotations;
		
		public void tick() {
			this.rotations = 0;
		}
		
		public boolean scrollUp() {
			return this.rotations > 0;
		}
		
		public boolean scrollDown() {
			return this.rotations < 0;
		}
	}
	
	public MouseWheel mouseWheel = new MouseWheel();
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.mouseWheel.rotations = e.getWheelRotation();
	}
	
	// Don't need these
	@Override
	public void keyTyped(KeyEvent ke) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
	
}
