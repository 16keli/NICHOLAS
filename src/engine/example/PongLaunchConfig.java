package engine.example;

import engine.Engine;
import engine.Game;
import engine.client.Client;
import engine.launcher.LaunchConfig;
import engine.launcher.LaunchWrapper;
import engine.server.Server;

public class PongLaunchConfig extends LaunchConfig {
	
	// Test code
	public static LaunchConfig self = new PongLaunchConfig();
	
	public static void main(String[] args) {
		//For packaged (.jar) games, LaunchWrapper should follow this same set of actions
		LaunchWrapper.setLaunchConfig(self);
		LaunchWrapper.initializeGame();
		Engine.createEngine(self);
		LaunchWrapper.launchFullscreen(320, 180);
	}
	
	@Override
	public void addProperties() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processProperties() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Class<? extends Game> getGameClass() {
		return Pong.class;
	}
	
	@Override
	public Class<? extends Client> getClientClass() {
		return PongClient.class;
	}
	
	@Override
	public Class<? extends Server> getServerClass() {
		return PongServer.class;
	}
	
}
