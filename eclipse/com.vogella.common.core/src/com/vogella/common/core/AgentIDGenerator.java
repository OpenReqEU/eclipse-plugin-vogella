package com.vogella.common.core;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

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
		agendId = generateAgentID(sb.toString());

		return agendId;
	}

	private static String generateAgentID(String macAddress) {
		String temp = macAddress;
		for (int round = 0; round < 10; round++) {
			temp = hash(temp);
		}

		/*
		 * damit die AgentID nicht zu lange wird kürzen wir die Länge der AgentID auf 9
		 * Zeichen, das reicht für unsere Zwecke um Kollisionen zu vermeiden
		 */
		return temp.substring(0, 9);
	}

	private static String hash(String input) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			String salt = "GkHwnFmstcpAgKB8Wyx4";
			String inputWithSalt = input + salt;
			byte[] inpBytes = inputWithSalt.getBytes();
			byte[] inpHash = sha256.digest(inpBytes);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < inpHash.length; i++) {
				sb.append(Integer.toString((inpHash[i] & 0xff) + 0x100, 16).substring(1));
			}
			String generatedOutput = sb.toString();
			return generatedOutput;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private AgentIDGenerator() {
	}
}
