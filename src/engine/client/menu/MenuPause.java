package engine.client.menu;

import java.awt.Color;

import engine.client.graphics.FontWrapper;
import engine.client.graphics.Screen;
import engine.input.ActionMenuInput;

/**
 * A simple Menu for when the game is paused
 * 
 * @author Kevin
 */
public class MenuPause extends Menu {
	
	@Override
	public void tickMenu() {
		if (this.menuInput.getInputFromAction(ActionMenuInput.ESCAPE).isClicked()) {
			this.client.setMenu(null);
		}
	}
	
	@Override
	public void renderMenu(Screen screen) {
		String msg = "Game is Paused";
		int x = FontWrapper.getXCoord(screen, msg);
		int y = screen.height / 2;
		FontWrapper.draw(msg, screen, x, y, Color.WHITE.getRGB());
	}
	
}
