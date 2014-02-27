package edu.papa;


/**
 * Turns out you can't import things in the default package.
 * But when you rename packages, JNI breaks.
 * So we use reflection.
 * @author Christian
 *
 */
public class SPDDriver {
	
	private Class SimplePacketDriver;
	private Object driver;
	
	public SPDDriver() throws ReflectiveOperationException {
		SimplePacketDriver = Class.forName("SimplePacketDriver");
		driver = SimplePacketDriver.newInstance();
	}
	
	public String[] getAdapterNames() throws RuntimeException, SecurityException, ReflectiveOperationException {
		return (String[]) SimplePacketDriver.getMethod("getAdapterNames", null).invoke(driver, null);
	}
	
	public boolean openAdapter(String s) throws ReflectiveOperationException, IllegalArgumentException, SecurityException {
		return (Boolean) SimplePacketDriver.getMethod("openAdapter", String.class).invoke(driver, s);
	}
	
	public byte[] readPacket() throws ReflectiveOperationException, IllegalArgumentException, SecurityException {
		return (byte[]) SimplePacketDriver.getMethod("readPacket", null).invoke(driver, null);
	}
	
	public boolean sendPacket(byte[] b) throws ReflectiveOperationException, IllegalArgumentException, SecurityException {
		return (Boolean) SimplePacketDriver.getMethod("sendPacket", byte[].class).invoke(driver, b);
	}
}
