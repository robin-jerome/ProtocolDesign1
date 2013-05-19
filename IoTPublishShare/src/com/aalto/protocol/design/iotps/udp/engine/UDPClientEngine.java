package com.aalto.protocol.design.iotps.udp.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.aalto.protocol.design.iotps.ack.engine.AckEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.subscribe.engine.SubscribeEngine;

public class UDPClientEngine {
	
	private static DatagramSocket dsocket = null;
	
	private static final int BUFFER_LENGTH = 2048;
	
	public static void listenForClientMessages(int port) throws Exception {
		
		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){
			dsocket = new DatagramSocket(port);
		}
		
		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
	        System.out.println(udpPacket.getAddress().getHostName() + ": "
	            + receivedMsg);
	        udpPacket.setLength(buffer.length);
	        
	        if(receivedMsg.contains("\"acknowledgement\"")){ // ACK message received from the client
	        	System.out.println("Ack message received");
	        	try {
	        		IoTPSAckObject ackObj = AckEngine.getAckObjectFromUDPMessage(receivedMsg);
	        		AckEngine.removeFromPendingAcks(ackObj);
	        	} catch (Exception e) {
	        		continue;
	        	}
	        	
	        } else if(receivedMsg.contains("\"subscribe\"")){ // Subscribe/Un-subscribe message received from the client
	        	System.out.println("Subscribe message received");
	        	try {
	        		IoTPSSubscribeObject subObj = SubscribeEngine.getSubscribeObjectFromUDPMessage(receivedMsg);
		        	if(receivedMsg.contains("unsubscribe")){  
		        		SubscribeEngine.removeSubscription(subObj); // Unsubscribe
		        	} else {
		        		SubscribeEngine.addSubscription(subObj);	// Subscribe
		        	}
	        	} catch (Exception e) {
	        		continue;
	        	}
	        }
		}
	}
	
	
	public static void sendToClient(String ip, int port, JSON_Object o) throws Exception {
		// ------ Log outgoing data to file ---------
		String filename = "client_" + ip + "_" + port + ".log";
		String logData = o.GetValue("sensor_data");
		if (o.GetValue("dev_id").contains("camera")) logData = Integer.toString(logData.length());

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
			out.write("send_ts \t" + logData);
			out.close();
		} catch (Exception e) {System.err.println("Error: " + e.getMessage());}
		// ------------------------------------------
			
		
		  // Log outgoing data to file
		  byte[] messageInBytes = o.toJSONString().getBytes();
	      InetAddress address = InetAddress.getByName(ip);
	      DatagramPacket packet = new DatagramPacket(messageInBytes, messageInBytes.length, address, port);
	      DatagramSocket dsocket = new DatagramSocket();
	      dsocket.send(packet);
	      dsocket.close();
		
	}
	
	public static void main(String[] args) {
		try {
			
			listenForClientMessages(5080);
			int i = 0;
			while(i<100){
				JSON_Object o = new JSON_Object();
				o.AddItem("message", "Some random Message--"+i);
				sendToClient("127.0.0.1",5060,o);
				i++;
			}
			System.out.println("Sent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
