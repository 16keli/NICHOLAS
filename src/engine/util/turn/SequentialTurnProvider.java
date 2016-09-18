package engine.util.turn;

import java.util.LinkedList;

/**
 * An implementation of {@code ITurnProvider} that uses a rotating queue to determine the next {@code ITurnUser}
 * @author Kevin
 *
 */
public class SequentialTurnProvider implements ITurnProvider {
	
	/**
	 * The list of users
	 */
	private LinkedList<ITurnUser> users = new LinkedList<ITurnUser>();
	
	/**
	 * The previous retrieved value
	 */
	private int prev;
	
	@Override
	public ITurnUser getNext() {
		ITurnUser user = users.get(prev++);
		if (prev >= users.size()) {
			prev = 0;
		}
		return user;
	}

	@Override
	public void add(ITurnUser user) {
		users.add(user);
	}
	
}
