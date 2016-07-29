package engine.util;

import static engine.Engine.rand;

/**
 * A class full of utility methods dealing with {@link engine.Engine#rand}
 * 
 * @author Kevin
 */
public class RandUtil {
	
	public static int rollDice() {
		return rand.nextInt(5) + 1;
	}
	
	public static int rollDie(int numDie) {
		int sum = 0;
		for (int i = 0; i < numDie; i++) {
			sum += rollDice();
		}
		return sum;
	}
	
}
