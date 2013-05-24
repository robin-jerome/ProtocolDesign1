package com.aalto.protocol.design.iotps.start;


import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.udp.engine.ClientToServerUDPEngine;
import com.aalto.protocol.design.iotps.utils.Constants;



public class IoTPSClientStarter {

	private static int version = 1; // to be taken as argument

	private static String clientIP;

	private static int port;

	private static String serverIP;

	private static int serverPort;
	
	public static HashMap<String,Double> subscriptionIdSeqNumMap = new HashMap<String,Double>();
	
	private static HashMap<String,String> subscriptionIdDeviceIdMap = new HashMap<String,String>();
	
	public static int getVersion() {
		return version;
	}

	public static String getClientIP() {
		return clientIP;
	}

	public static int getPort() {
		return port;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static int getServerPort() {
		return serverPort;
	}
	
	public static int getSelfPortListeningToServer() {
		return 5062;
	}

	public static String getSelfIPListeningToServer() {
		try {
			return(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return "127.0.0.1"; // To be changed later
		}
	}
	

	/* Arguments to the script
	 * ./iot-client –s <server ip> -p <server port> [list of sensor ids]
	 */

	public static void main(String[] args) {

		/*
		 * 1.Start a UDP client to receive messages from the server.
		 * 2.Start a UDP client to send messages to the server.
		 * 3.Subscribe to the sensor by sending subscribe request to the server
		 * 4.Parse the incoming message & log data
		 */

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { System.out.println("Received ctrl+c: shutting down"); }
		});
		
		if (args.length <= 2) {
			System.err.println("Incorrect number of arguments!");
			System.exit(-1);
		} else {
			
			serverIP = args[0];
			serverPort = Integer.parseInt(args[1]);
			for(int i=2; null!=args[i]; i++) {
				subscriptionIdDeviceIdMap.put(args[i],"");
			}
			
			Thread serverListenThread = new Thread(new Runnable() 
			{ 
				public void run() {
					try {
						ClientToServerUDPEngine.listenForServerMessages(getSelfPortListeningToServer());
					} catch (Exception e) {
						System.err.println("Error while starting to listen for Server messages");
						e.printStackTrace();
						System.exit(-1);
					}
				} 
			});
			serverListenThread.start();
			
			System.out.println("Request to subscribe to server with IP: "+serverIP+" Port: "+serverPort+" Number of devices: "+subscriptionIdDeviceIdMap.keySet().size());
			for(String deviceId: subscriptionIdDeviceIdMap.keySet()) {
				System.out.println("Subscribe to server with IP: "+serverIP+" Port: "+serverPort+" DeviceId: "+deviceId);	
				JSON_Object o = new JSON_Object();
				long subscriptionId = System.currentTimeMillis();
				o.AddItem(Constants.ACTION, Constants.SUBSCRIBE + "");
				o.AddItem("version", IoTPSClientStarter.getVersion() + "");
				o.AddItem("seq_no", 1 + "");
				o.AddItem("client_ip", ""+getSelfIPListeningToServer());
				o.AddItem("client_port", ""+getSelfPortListeningToServer());
				o.AddItem("sub_seq_no", subscriptionId + "");
				o.AddItem("device_id", deviceId + "");
				o.AddItem("timestamp", System.currentTimeMillis() + "");
				try {
					ClientToServerUDPEngine.sendToServer(getServerIP(), getServerPort(), o.toJSONString());
					ClientToServerUDPEngine.unacknowledgedSubscibes.put(subscriptionId + "", 0L);
					subscriptionIdDeviceIdMap.put(deviceId, ""+subscriptionId);
					// Start a timer to retry failed subscriptions with exponential back-off
				} catch (Exception e) {
					System.err.println("Error while subscribing:");
					e.printStackTrace();
					System.exit(-1);
				}
			}
			
		}
	}

}
