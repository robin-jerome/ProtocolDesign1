package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.aalto.protocol.design.iotps.ack.engine.AckEngine;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.subscribe.engine.SubscribeEngine;

/*  Think about
 * 	===========
 * 1. What happens when server doesn't acknowledge a Subscribe
 * 
 */
public class ClientToServerUDPEngine {

private static final int SERVER_INTERFACE_PORT = 5060;
	
	private static DatagramSocket dsocket = null;
	
	private static final int BUFFER_LENGTH = 2048;
	
	public static void listenForServerMessages() throws Exception {
		
		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){
			dsocket = new DatagramSocket(SERVER_INTERFACE_PORT);
		}
		
		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
	        System.out.println(udpPacket.getAddress().getHostName() + ": "
	            + receivedMsg);
	        udpPacket.setLength(buffer.length);
	        
	        if(receivedMsg.contains("\"acknowledgement\"")){ 
	        	/*
	        	 *  ACKNOWLEDGEMENT packet received from the server
	        	 *  ------------------------------------
	        	 *  1. Log data
	        	 *  
	        	 *  3. Wonder if there is something else to do
	        	 */
	        	System.out.println("Ack message received");
	        	
	        	
	        	
	        } else { 
	        	/*
	        	 *  Data packet received from the server
	        	 *  ------------------------------------
	        	 *  1. Log data
	        	 *  2. Send ACKNOWLEDGEMENT for correct sequence number
	        	 *  3. Wonder if there is something else to do
	        	 */
	        	
	        	System.out.println("Data message received");
	        	
	        }
		}
	}
	
	public static void sendToServer(String ip, int port, String message) throws Exception {
			
		
		  // Log outgoing data to file
		  byte[] messageInBytes = message.getBytes();
	      InetAddress address = InetAddress.getByName(ip);
	      DatagramPacket packet = new DatagramPacket(messageInBytes, messageInBytes.length, address, port);
	      DatagramSocket dsocket = new DatagramSocket();
	      dsocket.send(packet);
	      dsocket.close();
		
	}
	
	public static void main(String[] args) {
		try {
			
			listenForServerMessages();
			int i = 0;
			while(i<100){
				sendToServer("127.0.0.1",5060,"Some random Message--"+i);
				i++;
			}
			System.out.println("Sent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
