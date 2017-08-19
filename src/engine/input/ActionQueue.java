package engine.input;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import engine.networknio.packet.PacketNIO;

/**
 * Represents the Action queue for a given player
 * <p>
 * Basically a wrapper class around a queue that implements all that is needed to handle the requirements
 * <p>
 * Not too sure why the choice of the queue interface. Whatever.
 * 
 * @author Kevin
 */
public class ActionQueue {
	
	
	/**
	 * The List of Actions. Or a queue. I guess. It's a linkedlist okay
	 */
	private Queue<Action> actions = new LinkedList<Action>();
	
	/**
	 * Adds an action to the queue
	 */
	public void addActionToQueue(Action action) {
		actions.add(action);
	}
	
	/**
	 * Returns the first action in the queue, and removes it
	 */
	public Action getAction() {
		return actions.poll();
	}
	
	/**
	 * Populates the {@code ActionQueue} with actions from the previous tick that were just read in
	 * 
	 * @param buffer
	 *            The {@code ByteBuffer} containing the data
	 */
	public void read(ByteBuffer buffer) {
		if (!actions.isEmpty()) {
			Action.logger.warning(
					"Action queue was not empty before repopulation! Logging leftover actions at level FINE");
			for (Action act : actions) {
				Action.logger.fine(act.getClass().getSimpleName() + " (" + act.getID() + ")");
			}
			actions.clear();
		}
		
		for (// The number of actions to read
		int numActions = buffer.getInt(); numActions > 0; numActions--) {
			int id = buffer.getInt();
			Action act = Action.getNewAction(id);
			act.readData(buffer);
			this.addActionToQueue(act);
		}
	}
	
	/**
	 * Writes all the actions currently in the queue to the given {@code ByteBuffer}, then clears the queue
	 * 
	 * @param buffer
	 */
	public void write(ByteBuffer buffer) {
		buffer.putInt(actions.size());
		for (Action act : actions) {
			buffer.putInt(act.getID());
			act.writeData(buffer);
		}
		actions.clear();
	}
	
	/**
	 * Gets the queue into the form of a {@code PacketNIO} to send across the connection
	 * 
	 * @return
	 */
	public PacketNIO getPacket() {
		return new PacketActionQueue(this);
	}
	
	/**
	 * Copies the contents of {@code other} into {@code this}
	 * 
	 * @param other
	 *            The other {@code ActionQueue} to copy actions from
	 */
	public void populate(ActionQueue other) {
		this.actions.clear();
		for (Action act : other.actions) {
			this.addActionToQueue(act);
		}
	}
}
