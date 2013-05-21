package com.aalto.protocol.design.iotps.udp.engine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
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
				double receivedSubscriptionNum = dataJSON.GetNumberValue("sub_seq_no");
				System.out.println("Subscription Id received::"+receivedSubscriptionNum);
				if(unacknowledgedSubscibes.containsKey(""+receivedSubscriptionNum)){
					unacknowledgedSubscibes.remove(""+receivedSubscriptionNum);
					IoTPSClientStarter.setSubscribed(true);
					IoTPSClientStarter.setSubscriptionId((long)receivedSubscriptionNum);
				}

			} else if (receivedMsg.contains("\""+Constants.UPDATE+"\"")) { 

				/*
				 *  Data packet received from the server
				 *  ------------------------------------
				 *  1. Log data
				 *  2. Send ACKNOWLEDGEMENT for correct sequence number
				 *  3. Wonder if there is something else to do
				 */
				System.out.println("Data message received::"+receivedMsg);

				JSON_Object dataJSON = new JSON_Object(receivedMsg);
				double receivedSeqNum = dataJSON.GetNumberValue("seq_no");
				double receivedSubscriptionNum = dataJSON.GetNumberValue("sub_seq_no");
				if(receivedSeqNum > 0 && (""+receivedSubscriptionNum).equals(""+IoTPSClientStarter.getSubscriptionId())) {
					try {
						JSON_Object o = new JSON_Object();
						o.AddItem(Constants.ACTION, Constants.ACKNOWLEDGEMENT + "");
						o.AddItem("version", IoTPSClientStarter.getVersion() + "");
						o.AddItem("seq_no", receivedSeqNum + "");
						o.AddItem("sub_seq_no", IoTPSClientStarter.getSubscriptionId() + "");
						o.AddItem("timestamp", System.currentTimeMillis() + "");
						sendToServer(IoTPSClientStarter.getServerIP(), IoTPSClientStarter.getServerPort(), o.toJSONString());
					} catch (Exception e) {
						System.out.println("Sending ACKNOWLEDEMENT failed for sequence number: "+receivedSeqNum);
					}
				} else {
					System.err.println("Received invalid values for sequence number: "+receivedSeqNum+" Subscription Id:"+receivedSubscriptionNum);
				}
				
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
