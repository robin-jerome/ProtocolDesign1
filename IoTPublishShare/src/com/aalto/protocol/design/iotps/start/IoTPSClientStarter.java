package com.aalto.protocol.design.iotps.start;


public class IoTPSClientStarter {

	private static int version = 1; // to be taken as argument

	private static long subscriptionId;

	private static String clientIP;

	private static int port;

	private static String serverIP;

	private static int serverPort;
	
	private static boolean isSubscribed;

	public static boolean isSubscribed() {
		return isSubscribed;
	}

	public static void setSubscribed(boolean isSubscribed) {
		IoTPSClientStarter.isSubscribed = isSubscribed;
	}

	public static int getVersion() {
		return version;
	}

	public static long getSubscriptionId() {
		return subscriptionId;
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


	public static void setSubscriptionId(long subscriptionId) {
		IoTPSClientStarter.subscriptionId = subscriptionId;
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

	}

}
