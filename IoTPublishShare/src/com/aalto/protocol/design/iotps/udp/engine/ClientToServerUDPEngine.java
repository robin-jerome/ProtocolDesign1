package com.aalto.protocol.design.iotps.udp.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.start.IoTPSClientStarter;
import com.aalto.protocol.design.iotps.utils.Constants;

/*  Think about
 * 	===========
 * 1. What happens when server doesn't acknowledge a Subscribe
 * 
 */
public class ClientToServerUDPEngine {

	private static DatagramSocket dsocket = null;

	private static final int BUFFER_LENGTH = 2048;
	
	public static HashMap<String , Long> unacknowledgedSubscibes = new HashMap<String, Long>();

	/**
	 * @param serverInterfacePort
	 * @throws Exception
	 */
	public static void listenForServerMessages(int serverInterfacePort) throws Exception {

		byte[] buffer = new byte[BUFFER_LENGTH];
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		if(null == dsocket){

			dsocket = new DatagramSocket(serverInterfacePort);
		}

		while(true) {
			dsocket.receive(udpPacket);
			String receivedMsg = new String(buffer, 0, udpPacket.getLength());
			System.out.println(udpPacket.getAddress().getHostName() + ": "
					+ receivedMsg);
			udpPacket.setLength(buffer.length);

			if(receivedMsg.contains("\""+Constants.ACKNOWLEDGEMENT+"\"")) { 
				/*
				 *  ACKNOWLEDGEMENT packet received from the server
				 *  ------------------------------------
				 *  1. Log data
				 *  2. Remove pending ACKOWLEDGEMENT for Subscribe
				 *  3. Wonder if there is something else to do
				 */
				System.out.println("Ack message received::"+receivedMsg);
				JSON_Object dataJSON = new JSON_Object(receivedMsg);
				long receivedSubscriptionNum = dataJSON.GetNumberValue("sub_seq_no");
				System.out.println("Subscription Id received::"+receivedSubscriptionNum);
				if(unacknowledgedSubscibes.containsKey(""+receivedSubscriptionNum)){
					System.out.println("Acknowledge message received for Valid SubscriptionId - Subscription Complete");
					IoTPSClientStarter.subscriptionIdSeqNumMap.put(""+receivedSubscriptionNum, Long.valueOf(0L));
					unacknowledgedSubscibes.remove(""+receivedSubscriptionNum);
				} else {
					System.err.println("Acknowledge message received for Invalid SubscriptionId - Will be unsubscribed");
					sendUnsubscriptionMessage(receivedSubscriptionNum, 0L);
				}

			} else if (receivedMsg.contains("\""+Constants.UPDATE+"\"")) { 

				/*
				 *  Data packet received from the server
				 *  ------------------------------------
				 *  1. Log data
				 *  3. Wonder if there is something else to do
				 */
				// LOG
				String filename = "client.log";
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
					out.write("Receive_ts \t" + receivedMsg);
					out.close();
				} catch (Exception e) {System.err.println("Error: " + e.getMessage());}
				// ------------------------------------------

				JSON_Object dataJSON = new JSON_Object(receivedMsg);
				long receivedSeqNum = dataJSON.GetNumberValue("seq_no");
				long receivedSubscriptionNum = dataJSON.GetNumberValue("sub_seq_no");
				long responseSeqNum = receivedSeqNum;

				if(receivedSeqNum > 0) { // Valid update message

					if(IoTPSClientStarter.subscriptionIdSeqNumMap.containsKey(""+receivedSubscriptionNum)){ // subscribed
						System.out.println(" Update recieved for a valid subscription");
						long currentSeqNum = IoTPSClientStarter.subscriptionIdSeqNumMap.get(""+receivedSubscriptionNum);
						if(currentSeqNum == 0L) { // Might be first update
							currentSeqNum = receivedSeqNum;
							responseSeqNum = receivedSeqNum;
							
						} else if (currentSeqNum < receivedSeqNum) { // In order update
							responseSeqNum = receivedSeqNum;
							
						} else if (currentSeqNum > receivedSeqNum) { // Duplicate
							responseSeqNum = currentSeqNum; 
							
						}
						IoTPSClientStarter.subscriptionIdSeqNumMap.put(""+receivedSubscriptionNum, responseSeqNum);
						
						try {
							sendAcknowledgement(receivedSubscriptionNum,responseSeqNum);
							// Setting the latest sequence number in the HashMap
							IoTPSClientStarter.subscriptionIdSeqNumMap.put(""+receivedSubscriptionNum, responseSeqNum);
						} catch (Exception e) {
							System.err.println("Sending ACKNOWLEDEMENT failed for sequence number: "+receivedSeqNum);
						}
						
					} else { // Do something else
						System.err.println(" Update recieved for a in-valid subscription - Unsubscription will be triggered");
						sendUnsubscriptionMessage(receivedSubscriptionNum,responseSeqNum);
					}
					
				} else {
					System.err.println("Received invalid values for sequence number: "+receivedSeqNum+" Subscription Id:"+receivedSubscriptionNum);
				}
				
			} else if (receivedMsg.contains("\""+Constants.RESULT+"\"")) { 
				System.out.println("Received response for the find sensors request");
				List<String> sensorNameList = findAvailableSensorsFromResult(receivedMsg);
				if(sensorNameList.size()==0) {
					System.out.println("No sensors could be found in the Server");
				} else {
					System.out.println(sensorNameList.size()+" sensors found in the Server");
					int counter = 0;
					for(String sensorName : sensorNameList) {
						System.out.println(counter+": "+sensorName);
						counter++;
					}
				}
				System.exit(0);
			}
		}
	}

	// Expected data format [device_2, device_1]
	private static List<String> findAvailableSensorsFromResult(String receivedMsg) {
		
		List<String> sensorNameList = new ArrayList<String>();
		
		JSON_Object o = new JSON_Object(receivedMsg);
		String rawData = o.GetValue("sensors");
		rawData = rawData.replace("[", "");
		rawData = rawData.replace("]", "");
		
		if(!"".equals(rawData.trim())){
			String [] deviceNameArray = rawData.trim().split(",");
			sensorNameList = Arrays.asList(deviceNameArray);
		}
		
		return sensorNameList;
	}

	/**
	 * @param receivedSubscriptionNum
	 * @param responseSeqNum
	 * @throws Exception
	 */
	private static void sendAcknowledgement(long receivedSubscriptionNum,
			long responseSeqNum) throws Exception {
		JSON_Object o = new JSON_Object();
		o.AddItem(Constants.ACTION, Constants.ACKNOWLEDGEMENT + "");
		o.AddItem("version", IoTPSClientStarter.getVersion() + "");
		o.AddItem("client_ip", IoTPSClientStarter.getSelfIPListeningToServer() + "");
		o.AddItem("client_port", IoTPSClientStarter.getSelfPortListeningToServer() + "");
		o.AddItem("seq_no", responseSeqNum + "");
		o.AddItem("sub_seq_no", receivedSubscriptionNum + "");
		o.AddItem("timestamp", System.currentTimeMillis() + "");
		sendToServer(IoTPSClientStarter.getServerIP(), IoTPSClientStarter.getServerPort(), o.toJSONString());
	}

	/**
	 * @param receivedSubscriptionNum
	 * @param responseSeqNum
	 * @throws Exception
	 */
	private static void sendUnsubscriptionMessage(long receivedSubscriptionNum, 
			long responseSeqNum) throws Exception {
		JSON_Object o = new JSON_Object();
		o.AddItem(Constants.ACTION, Constants.UNSUBSCRIBE + "");
		o.AddItem("version", IoTPSClientStarter.getVersion() + "");
		o.AddItem("seq_no", responseSeqNum + "");
		o.AddItem("sub_seq_no", receivedSubscriptionNum + "");
		o.AddItem("timestamp", System.currentTimeMillis() + "");
		sendToServer(IoTPSClientStarter.getServerIP(), IoTPSClientStarter.getServerPort(), o.toJSONString());
	}
	

	/**
	 * @param ip
	 * @param port
	 * @param message
	 * @throws Exception
	 */
	public static void sendToServer(String ip,
			int port, 
			String message) throws Exception {

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
			listenForServerMessages(5061);
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
