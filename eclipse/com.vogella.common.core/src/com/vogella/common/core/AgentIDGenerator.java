package com.vogella.common.core;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

public class AgentIDGenerator {

	private static String agendId;

	public static String getAgentID() {
		if (agendId != null) {
			return agendId;
		}
		Enumeration<NetworkInterface> networkInterfaces;
		StringBuilder sb = new StringBuilder();
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface s = networkInterfaces.nextElement();
				byte[] mac = s.getHardwareAddress();
				if (mac != null) {
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Version version = Platform.getBundle("org.eclipse.platform").getVersion();
		sb.append(version.toString());

		agendId = sb.toString();

		return agendId;
	}

	private AgentIDGenerator() {
	}
}
