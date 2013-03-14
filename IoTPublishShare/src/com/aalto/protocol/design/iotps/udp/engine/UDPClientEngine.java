package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientEngine {

	private static final int CLIENT_INTERFACE_PORT = 5060;
	
	private static DatagramSocket dsocket = null;
	
	private static final int BUFFER_LENGTH = 2048;
	
	private static void listenForClientMessages() throws Exception {
		
		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){
			dsocket = new DatagramSocket(CLIENT_INTERFACE_PORT);
		}
		
		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
	        System.out.println(udpPacket.getAddress().getHostName() + ": "
	            + receivedMsg);
	        udpPacket.setLength(buffer.length);
			
		}
	}
	
	private static void sendToClient(String ip, int port, String message) throws Exception {
		
		  byte[] messageInBytes = message.getBytes();
	      InetAddress address = InetAddress.getByName(ip);
	      DatagramPacket packet = new DatagramPacket(messageInBytes, messageInBytes.length, address, port);
	      DatagramSocket dsocket = new DatagramSocket();
	      dsocket.send(packet);
	      dsocket.close();
		
	}
	
	public static void main(String[] args) {
		try {
			
			listenForClientMessages();
			int i = 0;
			while(i<100){
				sendToClient("127.0.0.1",5060,"Some random Message--"+i);
				i++;
			}
			System.out.println("Sent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
