package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Scanner;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.papa.networks.SimplePacketDriver;

public class NetworkPacketSource implements PacketSource {
	
	static {
		System.loadLibrary("SimplePacketDriver");
	}
	
	private SimplePacketDriver driver;
	
	public NetworkPacketSource(String string) {
		
		this.driver = new SimplePacketDriver();
		driver.openAdapter(string);
	}
	
	public NetworkPacketSource(SimplePacketDriver driver) {
		this.driver = driver;
	}
	
	public static NetworkPacketSource fromPrompt() {
        SimplePacketDriver driver = new SimplePacketDriver();
        
        //Get adapter names and print info
        String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) System.out.println(i+" Device name in Java ="+adapters[i]);
        
        System.out.print("Which adapter? ");
        int choice = new Scanner(System.in).nextInt();
        
        //Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[choice])) System.out.println("Adapter is open: "+adapters[choice]);
        
        return new NetworkPacketSource(driver);
	}
	
	/**
	 * So not thread-safe. Please do not use more than one of these at once.
	 */
	@Override
	public Iterator<ByteBuffer> iterator() {
		return new Iterator<ByteBuffer>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public ByteBuffer next() {
				return ByteBuffer.wrap(driver.readPacket());
			}

			@Override
			public void remove() {
				throw new NotImplementedException();
			}
		};
	}

}
