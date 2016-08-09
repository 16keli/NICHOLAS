package engine.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import engine.Engine;
import engine.client.menu.MenuComponent;

/**
 * A Utility Class that helps organize all the saved server connections
 * 
 * @author Kevin
 */
public class ServerList {
	
	/**
	 * The {@code File} containing the list of servers
	 */
	public File serverFile;
	
	/**
	 * The file reader
	 */
	public Scanner reader;
	
	/**
	 * The file writer
	 */
	public PrintStream writer;
	
	/**
	 * The list of ServerConnections
	 */
	public List<ServerConnection> connections = new ArrayList<ServerConnection>();
	
	protected ServerList() {
		this.serverFile = new File(Engine.getFilePath() + "servers.txt");
		try {
			this.reader = new Scanner(this.serverFile);
			this.writer = new PrintStream(new FileOutputStream(this.serverFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.readList();
	}
	
	public void readList() {
		while (this.reader.hasNext()) {
			this.connections.add(ServerConnection.read(this.reader.nextLine()));
		}
	}
	
	public void writeList() {
		for (ServerConnection c : this.connections) {
			c.write(this.writer);
		}
	}
	
	/**
	 * Creates a new {@code ServerList} instance
	 * 
	 * @return
	 */
	public static ServerList createServerList() {
		return new ServerList();
	}
	
	/**
	 * Creates a {@code MenuComponent[][]} for use with MenuServerList
	 * 
	 * @return
	 */
	public MenuComponent[][] createComponentList() {
		MenuComponent[][] comps = new MenuComponent[this.connections.size()][1];
		for (int i = 0; i < this.connections.size(); i++) {
			comps[i][0] = new MenuComponent(this.connections.get(i).name + ":\t" + this.connections.get(i).ip,
					32, 32 + 16 * i);
		}
		return comps;
	}
	
	public static class ServerConnection {
		
		public String name;
		
		public String ip;
		
		public ServerConnection(String name, String ip) {
			this.name = name;
			this.ip = ip;
		}
		
		public static ServerConnection read(String s) {
			String[] sub = s.split(";");
			return new ServerConnection(sub[0], sub[1]);
		}
		
		public void write(PrintStream os) {
			os.println(this.name + ";" + this.ip);
		}
	}
	
}
