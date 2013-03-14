package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.aalto.protocol.design.iotps.ack.engine.AckEngine;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.subscribe.engine.SubscribeEngine;

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
	        
	        if(receivedMsg.contains("\"acknowledgement\"")){ // ACK message received from the client
	        	System.out.println("Ack message received");
	        	IoTPSAckObject ackObj = AckEngine.getAckObjectFromUDPMessage(receivedMsg);
	        	AckEngine.removeFromPendingAcks(ackObj);
	        	
	        } else if(receivedMsg.contains("\"subscribe\"")){ // Subscribe/Un-subscribe message received from the client
	        	System.out.println("Subscribe message received");
	        	IoTPSSubscribeObject subObj = SubscribeEngine.getSubscribeObjectFromUDPMessage(receivedMsg);
	        	if(receivedMsg.contains("unsubscribe")){  
	        		SubscribeEngine.removeSubscription(subObj); // Unsubscribe
	        	} else {
	        		SubscribeEngine.addSubscription(subObj);	// Subscribe
	        	}
	        }
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
