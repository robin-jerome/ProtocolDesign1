package com.aalto.protocol.design.iotps.start;

import com.aalto.protocol.design.iotps.udp.engine.ClientToServerUDPEngine;
import com.aalto.protocol.design.iotps.udp.engine.ServerToClientUDPEngine;


public class IoTPSClientStarter {
	
	public static int version = 1; // to be taken as argument
	
	
	
	/* Arguments to the script
	 * ./iot-client –s <server ip> -p <server port> [list of sensor ids]
	 */
	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { System.out.println("Received ctrl+c: shutting down"); }
		 });
		
		/*
		 * 1.Start a UDP client to receive messages from the server.
		 * 2.Start a UDP client to send messages to the server.
		 * 3.Subscribe to the sensor by sending subscribe request to the server
		 * 4.Parse the incoming message & log data
		 */
		
		if (args.length != 3) System.err.println("Incorrect number of arguments!");
		final String serverIp = args[0];
		final int serverPort = Integer.parseInt(args[1]);
		final int listenPort = Integer.parseInt(args[2]);
		
		// Listen to server
		Thread clientListenThread = new Thread(new Runnable() 
				{ public void run() {try {
					ClientToServerUDPEngine.listenForServerMessages(listenPort);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}} });
		clientListenThread.start();
		
		// Send messages to server TODO
		ClientToServerUDPEngine.sendToServer(serverIp, serverPort, "something");
	}

}
