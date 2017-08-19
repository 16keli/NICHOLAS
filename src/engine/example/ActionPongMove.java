package engine.example;

import java.nio.ByteBuffer;

import engine.input.Action;
import engine.server.Server;

public class ActionPongMove extends Action {
	
	
	/**
	 * The direction the action was made in
	 */
	private int direction;
	
	/**
	 * The player that sends the action
	 */
	private int player;
	
	/**
	 * Moving up
	 */
	public static final ActionPongMove UP = new ActionPongMove(-1, 0);
	
	/**
	 * Stopping movement
	 */
	public static final ActionPongMove STOP = new ActionPongMove(0, 0);
	
	/**
	 * Moving down
	 */
	public static final ActionPongMove DOWN = new ActionPongMove(1, 0);
	
	/**
	 * Moving up (p2)
	 */
	public static final ActionPongMove UP2 = new ActionPongMove(-1, 1);
	
	/**
	 * Stopping movement (p2)
	 */
	public static final ActionPongMove STOP2 = new ActionPongMove(0, 1);
	
	/**
	 * Moving down (p2)
	 */
	public static final ActionPongMove DOWN2 = new ActionPongMove(1, 1);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9037070020787358571L;
	
	public ActionPongMove() {
		// TODO Auto-generated constructor stub
	}
	
	public ActionPongMove(int direction, int player) {
		this.direction = direction;
		this.player = player;
	}
	
	@Override
	public void writeData(ByteBuffer buffer) {
		buffer.putInt(this.direction);
		buffer.putInt(this.player);
	}
	
	@Override
	public void readData(ByteBuffer buffer) {
		this.direction = buffer.getInt();
		this.player = buffer.getInt();
	}
	
	@Override
	public void processActionOnServer(int player, Server server) {
		server.game.events.post(new EventInput(this.player, direction));
	}
	
	@Override
	public int hashCode() {
		// Direction can be -1, 0, or 1 and we don't want to collide with other actions
		return this.getHashcodeBase() + this.direction + 1 + 5 * this.player;
	}
	
}
