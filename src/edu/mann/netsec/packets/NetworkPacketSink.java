package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.Scanner;

import edu.papa.SPDDriver;

public class NetworkPacketSink implements PacketSink {

	private SPDDriver driver;

	public NetworkPacketSink(String s) throws ReflectiveOperationException {
		this.driver = new SPDDriver();
		driver.openAdapter(s);
	}
	
	public NetworkPacketSink(SPDDriver driver) {
		this.driver = driver;
	}

	public static PacketSink fromPrompt() throws ReflectiveOperationException {
        SPDDriver driver = new SPDDriver();
        
        //Get adapter names and print info
        String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) System.out.println(i+" Device name in Java ="+adapters[i]);
        
        System.out.print("Which adapter? ");
        int choice = new Scanner(System.in).nextInt();
        
        //Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[choice])) System.out.println("Adapter is open: "+adapters[choice]);
        
        return new NetworkPacketSink(driver);
	}
	
	/* (non-Javadoc)
	 * @see edu.mann.netsec.packets.PacketSink#writePacket(edu.mann.netsec.packets.Packet)
	 */
	@Override
	public boolean writePacket(Packet p) throws IllegalArgumentException, SecurityException, ReflectiveOperationException {
		// for now we only accept Ethernet packets
		if (p.getType() != "eth")
			throw new IllegalArgumentException("Packet type must be ethernet");
		return this.writePacket(p.getData());
	}
	
	/* (non-Javadoc)
	 * @see edu.mann.netsec.packets.PacketSink#writePacket(java.nio.ByteBuffer)
	 */
	@Override
	public boolean writePacket(ByteBuffer bb) throws IllegalArgumentException, SecurityException, ReflectiveOperationException {
		byte[] data = new byte[bb.remaining()];
		System.arraycopy(bb.array(), 0, data, 0, bb.remaining());
		return driver.sendPacket(data);
	}
}
