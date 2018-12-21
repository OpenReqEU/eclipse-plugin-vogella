package com.vogella.common.core;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

public class AgentIDGenerator {
	
	public static String getAgentID() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		StringBuilder sb = new StringBuilder();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface s = networkInterfaces.nextElement();
			byte[] mac = s.getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
		}
		
		Version version = Platform.getBundle("org.eclipse.platform").getVersion();
		sb.append(version.toString());

		return sb.toString();
	}
	
	private AgentIDGenerator() {
	}
}
