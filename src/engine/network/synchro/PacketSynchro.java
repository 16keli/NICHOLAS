package engine.network.synchro;

import engine.network.packet.PacketObject;

/**
 * A basic {@code PacketObject} used in synchronization of {@code Game} data
 * <p>
 * Because these {@code Packet}s are large in nature, they should be used sparingly. It is not necessary to
 * send these every tick. Instead, consider sending these only when necessary, such as when a large change
 * occurs that requires more information than a standard {@code Packet} can hold (reasonably), or when a new
 * {@code Client} connects to the {@code Server} and requires synchronization of {@code Game} data.
 * <p>
 * I honestly don't know why this is a class because it is exactly like {@code PacketObject}
 * 
 * @author Kevin
 */
public abstract class PacketSynchro extends PacketObject {

}
