package com.aalto.protocol.design.iotps.udp.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import com.aalto.protocol.design.iotps.ack.engine.AckEngine;
import com.aalto.protocol.design.iotps.db.engine.SQLiteDBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.start.IoTPSClientStarter;
import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;
import com.aalto.protocol.design.iotps.subscribe.engine.SubscribeEngine;
import com.aalto.protocol.design.iotps.utils.Constants;

public class ServerToClientUDPEngine {
	
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
        
        try {
			JSON_Object o = new JSON_Object(receivedMsg);
			String action = o.GetValue(Constants.ACTION);
			int version = (int)o.GetNumberValue(Constants.VERSION);
		
        
	        if(action.equalsIgnoreCase("acknowledgement")) {
	        	System.out.println("Ack message received::"+receivedMsg);
        		IoTPSAckObject ackObj = AckEngine.getAckObjectFromUDPMessage(o);
        		AckEngine.removeFromPendingAcks(ackObj);
        		
	        } else if(action.equalsIgnoreCase("subscribe")) { 
	        	System.out.println("Subscribe message received::"+receivedMsg);
	        	IoTPSSubscribeObject subObj = SubscribeEngine.getSubscribeObjectFromUDPMessage(o);
	        	if (version != IoTPSServerStarter.version) {
	        		sendErrorAcknowledgement (subObj, Constants.ERROR_VERSION_MISMATCH);
	        		continue;
	        	} else {
	        		SubscribeEngine.addSubscription(subObj);
	        	}
	        	
	        } else if (action.equalsIgnoreCase("unsubscribe")) {
        		IoTPSSubscribeObject subObj = SubscribeEngine.getSubscribeObjectFromUDPMessage(o);
        		SubscribeEngine.removeSubscription(subObj); 
        		sendAcknowledgementForSubscription(subObj);
        		
	        } else if(o.GetValue("action").equalsIgnoreCase(Constants.FIND)) { 
	        	System.out.println("Find sensor message received::"+receivedMsg);
	    		String clientIp = o.GetValue("client_ip");
	    		int clientPort = (int)o.GetNumberValue("client_port");
	    		String sensorListString = "[";
	    		String selectClientQuery = "select * from sensor_table";
	    		List<IoTPSObject> iotPSObjectList = SQLiteDBEngine.executeQuery(selectClientQuery, SQLiteDBEngine.SENSOR_OBJECT);
	    		for(IoTPSObject obj: iotPSObjectList) {
	    			IoTPSSensorObject sensorObj = (IoTPSSensorObject)obj;
	    			sensorListString = sensorListString + sensorObj.getDeviceId() + ",";
	    		}
	    		if(sensorListString.endsWith(",")){
	    			sensorListString = sensorListString.substring(0, sensorListString.length()-1);
	    		}
	    		
	    		sensorListString = sensorListString + "]";
	    		
	    		o = new JSON_Object();
	    		o.AddItem(Constants.ACTION, Constants.RESULT);
	    		o.AddItem("sensors", sensorListString);
	    		sendToClient(clientIp, clientPort, o);
	        	
	        }
	        
        } catch (Exception e) {
        	System.err.println("Error;;;;;;;;;;;;");
        	e.printStackTrace();
			continue;
		}
	}
	}
	
	public static void sendAcknowledgementForSubscription(IoTPSSubscribeObject subObj) {

		JSON_Object o = new JSON_Object();
		o.AddItem(Constants.ACTION, Constants.ACKNOWLEDGEMENT + "");
		o.AddItem("version", IoTPSClientStarter.getVersion() + "");
		o.AddItem("seq_no",  subObj.getSeqNo() + "");
		o.AddItem("sub_seq_no", subObj.getSubSeqNo() + "");
		o.AddItem("timestamp", System.currentTimeMillis() + "");
		try {
			sendToClient(subObj.getIp(), subObj.getPort(), o);
			System.out.println("Acknowledgement sent for subscribe/unsubscribe");
		} catch (Exception e) {
			System.err.println("Error while sending acknowledgement for subscribe/unsubscribe");
			e.printStackTrace();
		}

	}

	public static void sendToClient(String ip, int port, JSON_Object o) throws Exception {

		System.out.println(" -----------"+o.toJSONString());

		String action = o.GetValue(Constants.ACTION);

		if (action.equalsIgnoreCase(Constants.UPDATE)) {
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
		}

		byte[] messageInBytes = o.toJSONString().getBytes();
		InetAddress address = InetAddress.getByName(ip);
		DatagramPacket packet = new DatagramPacket(messageInBytes, messageInBytes.length, address, port);
		DatagramSocket dsocket = new DatagramSocket();
		dsocket.send(packet);
		dsocket.close();

	}
	
	public static void sendErrorAcknowledgement (IoTPSSubscribeObject subObj, int reason) {
		JSON_Object o = new JSON_Object();
		o.AddItem(Constants.ACTION, Constants.ACKNOWLEDGEMENT + "");
		o.AddItem("version", IoTPSClientStarter.getVersion() + "");
		o.AddItem("seq_no",  subObj.getSeqNo() + "");
		o.AddItem("sub_seq_no", subObj.getSubSeqNo() + "");
		o.AddItem("timestamp", System.currentTimeMillis() + "");
		o.AddItem("reason", String.valueOf(reason));
		try {
			sendToClient(subObj.getIp(), subObj.getPort(), o);
			System.out.println("Error acknowledgement with reason " + reason + " sent for subscribe/unsubscribe");
		} catch (Exception e) {
			System.err.println("Error while sending error acknowledgement for subscribe/unsubscribe");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			
			listenForClientMessages(5080);
			int i = 0;
			while(i<100){
				JSON_Object o = new JSON_Object();
				o.AddItem("message", "Some random Message--"+i);
				sendToClient("127.0.0.1",5060, o);
				i++;
			}
			System.out.println("Sent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
