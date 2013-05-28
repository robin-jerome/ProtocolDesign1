package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.aalto.protocol.design.iotps.json.engine.SensorDataParser;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;
import com.aalto.protocol.design.iotps.update.engine.UpdateEngine;

public class ServerToSensorUDPEngine {
	
	private static DatagramSocket dsocket = null;
	
	private static final int BUFFER_LENGTH = 66000;
	
	public static void listenForSensorMessages(int port) throws Exception {
		
		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){
			dsocket = new DatagramSocket(port);
		}
		
		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
	        System.out.println(udpPacket.getAddress().getHostName() + ": "+ receivedMsg);
	        udpPacket.setLength(buffer.length);
	        try { 
	        	
	        	IoTPSSensorUpdateObject sensorUpdateObject = SensorDataParser.generateSensorObject(receivedMsg);
				UpdateEngine.update(sensorUpdateObject);
	        } catch (Exception e) {
	        	System.err.println("Exception while parsing sensor data");
	        	e.printStackTrace();
	        }
			
		}
	}
	
	public static void main(String[] args) {
		try {
			listenForSensorMessages(5090);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
