package engine.client.menu;

import engine.client.Client;
import engine.client.KeyInputProcessor;
import engine.client.MouseInputProcessor;

/**
 * A Menu that the Client can navigate through, whether through keyboard or mouse inputs.
 * <p>
 * {@code Menu}s contain many {@code MenuComponent}s, which can be navigated between via keyboard (and soon
 * mouse). Of course, the organizational concept is not the best, though that is something that isn't very
 * high on the docket.
 * <p>
 * Typically, a {@code null Menu} means that gameplay is going on, though that is not always the case.
 * Sometimes, {@code Menu}s are simply overlays, which is important in online play so that a {@code Client}
 * does not miss out on anything simply because an interactive {@code Menu} is open.
 * <p>
 * {@code Menu}s typically have {@link #parent parent Menus}, which they will go back to if {@link #keyInput}'s
 * escape key is pressed.
 * <p>
 * {@code Menu} is built upon {@link engine.client.menu.MenuOverlay}, but should be more commonly used as it
 * automatically covers the entire screen.
 * 
 * @author Kevin
 */
public abstract class Menu extends MenuOverlay {
	
	public Menu() {
		super();
	}
	
	public Menu(MenuComponent[][] comps) {
		super(comps);
	}
	
	public Menu(Menu p, MenuComponent[][] comps) {
		super(p, comps);
	}
	
	@Override
	public void init(Client client, KeyInputProcessor keyInput, KeyInputProcessor menuInput, MouseInputProcessor mouseInput) {
		super.init(client, keyInput, menuInput, mouseInput);
		this.setRegion(0, 0, client.WIDTH, client.HEIGHT);
	}
	
}
