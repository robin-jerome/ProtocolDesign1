package com.aalto.protocol.design.iotps.start;


public class IoTPSClientStarter {
	
	public static int version = 1; // to be taken as argument
	
	
	
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
