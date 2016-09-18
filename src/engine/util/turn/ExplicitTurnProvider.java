package engine.util.turn;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An implementation of {@code ITurnProvider} that is entirely queue-based. {@code ITurnUser}s are added into
 * the queue and then retrieved in their respective order
 * 
 * @author Kevin
 */
public class ExplicitTurnProvider implements ITurnProvider {
	
	
	private Deque<ITurnUser> users = new ArrayDeque<ITurnUser>();
	
	@Override
	public void add(ITurnUser user) {
		users.addLast(user);
	}
	
	@Override
	public ITurnUser getNext() {
		return users.removeFirst();
	}
	
}
