package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSensorEngine {
	
	private static final int SENSOR_INTERFACE_PORT = 5061;
	
	private static DatagramSocket dsocket = null;
	
	private static final int BUFFER_LENGTH = 2048;
	
	private static void listenForSensorMessages() throws Exception {
		
		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){
			dsocket = new DatagramSocket(SENSOR_INTERFACE_PORT);
		}
		
		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
	        System.out.println(udpPacket.getAddress().getHostName() + ": "
	            + receivedMsg);
	        udpPacket.setLength(buffer.length);
			
		}
	}
	
	public static void main(String[] args) {
		try {
			listenForSensorMessages();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
