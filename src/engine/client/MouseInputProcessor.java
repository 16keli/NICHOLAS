package engine.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import engine.geom2d.Vector2;

/**
 * An input processor for the mouse
 * 
 * @author Kevin
 */
public class MouseInputProcessor extends InputProcessor implements MouseListener, MouseMotionListener {
	
	
	/**
	 * The X position of the mouse cursor
	 */
	private int mouseX;
	
	/**
	 * The Y position of the mouse cursor
	 */
	private int mouseY;
	
	public MouseInputProcessor(Client client) {
		super(client, "Mouse");
		System.setProperty("sun.awt.enableExtraMouseButtons", "true");
		client.addMouseListener(this);
		client.addMouseMotionListener(this);
	}
	
	// Standard Mouse Buttons
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (keyInputs.containsKey(e.getButton())) {
			keyInputs.get(e.getButton()).toggle(true);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (keyInputs.containsKey(e.getButton())) {
			keyInputs.get(e.getButton()).toggle(false);
		}
	}
	
	// Mouse Motion
	
	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}
	
	/**
	 * Retrieves the current mouse position
	 * 
	 * @return
	 */
	public Vector2 getMousePos() {
		return Vector2.of(this.getMouseX(), this.getMouseY());
	}
	
	/**
	 * Retrieves the current mouse X position
	 * 
	 * @return
	 */
	public double getMouseX() {
		return this.mouseX / this.client.SCALE;
	}
	
	/**
	 * Retrieves the current mouse Y position
	 * 
	 * @return
	 */
	public double getMouseY() {
		return this.mouseY / this.client.SCALE;
	}
	
	/**
	 * Retrieves the current absolute mouse position
	 * 
	 * @return
	 */
	public Vector2 getAbsoluteMousePos() {
		return Vector2.of(getMouseXAbsolute(), getMouseYAbsolute());
	}
	
	/**
	 * Retrieves the current absolute mouse X position
	 * 
	 * @return
	 */
	public int getMouseXAbsolute() {
		return this.mouseX;
	}
	
	/**
	 * Retrieves the current absolute mouse Y position
	 * 
	 * @return
	 */
	public int getMouseYAbsolute() {
		return this.mouseY;
	}
	
	// Unneeded FeelsBadMan
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
