package engine.networknio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.networknio.packet.PacketNIO;

/**
 * A wrapper around a list of {@code ConnectionNIO}
 * 
 * @author Kevin
 */
public class ConnectionList {
	
	/**
	 * The {@code List} of {@code Player}s currently connected to the server
	 */
	protected List<ConnectionNIO> connections = Collections.synchronizedList(new ArrayList<ConnectionNIO>());
	
	private Thread readThread;
	
	public ConnectionList() {
		this.readThread = new ThreadConnectionListRead(this);
		this.readThread.start();
		ConnectionNIO.logger.fine("ConnectionList Read Thread ID:\t" + this.readThread.getId());
	}
	
	/**
	 * Sends all the {@code PacketNIO} data in the sending queue
	 */
	public void sendPackets() {
		for (ConnectionNIO connect : this.connections) {
			connect.sendPackets();
		}
	}
	
	/**
	 * Adds the given {@code ConnectionNIO} to the list
	 * 
	 * @param connect
	 *            The {@code ConnectionNIO}
	 */
	public void addToList(ConnectionNIO connect) {
		this.connections.add(connect);
	}
	
	/**
	 * Retrieves the current list of {@code ConnectionNIO}s
	 * 
	 * @return
	 */
	public List<ConnectionNIO> getList() {
		return this.connections;
	}
	
	/**
	 * Sends the given {@code PacketNIO} to the {@code ConnectionNIO} destination via TCP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param dest
	 *            The {@code ConnectionNIO} destination
	 */
	public void sendTCPPacket(PacketNIO p, ConnectionNIO dest) {
		dest.addToTCPSendQueue(p);
	}
	
	/**
	 * Sends the given {@code PacketNIO} to the given player via TCP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param ind
	 *            The player number to send the {@code PacketNIO} to
	 */
	public void sendTCPPacket(PacketNIO p, int ind) {
		this.sendTCPPacket(p, this.connections.get(ind));
	}
	
	/**
	 * Sends the given {@code PacketNIO} to every player via TCP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 */
	public void sendTCPPacketAll(PacketNIO p) {
		for (ConnectionNIO conn : this.connections) {
			conn.addToTCPSendQueue(p);
		}
	}
	
	/**
	 * Sends the given {@code PacketNIO} to every player except the one noted via TCP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param id
	 *            The player number to NOT send the {@code PacketNIO} to
	 */
	public void sentTCPPacketAllExcept(PacketNIO p, int id) {
		for (int i = 0; i < this.connections.size(); i++) {
			if (i != id) {
				this.connections.get(i).addToTCPSendQueue(p);
			}
		}
	}
	
	/**
	 * Sends the given {@code PacketNIO} to the {@code ConnectionNIO} destination via UDP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param dest
	 *            The {@code ConnectionNIO} destination
	 */
	public void sendUDPPacket(PacketNIO p, ConnectionNIO dest) {
		dest.addToUDPSendQueue(p);
	}
	
	/**
	 * Sends the given {@code PacketNIO} to the given player via UDP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param ind
	 *            The player number to send the {@code PacketNIO} to
	 */
	public void sendUDPPacket(PacketNIO p, int ind) {
		this.sendUDPPacket(p, this.connections.get(ind));
	}
	
	/**
	 * Sends the given {@code PacketNIO} to every player via UDP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 */
	public void sendUDPPacketAll(PacketNIO p) {
		for (ConnectionNIO conn : this.connections) {
			conn.addToUDPSendQueue(p);
		}
	}
	
	/**
	 * Sends the given {@code PacketNIO} to every player except the one noted via UDP
	 * 
	 * @param p
	 *            The {@code PacketNIO} to send
	 * @param id
	 *            The player number to NOT send the {@code PacketNIO} to
	 */
	public void sentUDPPacketAllExcept(PacketNIO p, int id) {
		for (int i = 0; i < this.connections.size(); i++) {
			if (i != id) {
				this.connections.get(i).addToUDPSendQueue(p);
			}
		}
	}
	
}
