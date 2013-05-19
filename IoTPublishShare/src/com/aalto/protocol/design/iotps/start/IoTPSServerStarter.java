package com.aalto.protocol.design.iotps.start;

import com.aalto.protocol.design.iotps.udp.engine.UDPClientEngine;
import com.aalto.protocol.design.iotps.udp.engine.UDPSensorEngine;

public class IoTPSServerStarter {

	public static int version = 1; // to be taken as argument
	
	public static boolean isCongestionControlSupported = false;

	/* Arguments to the script
	 * ./iot-server �p <publish port> -s <subscribe port>
	 */
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { System.out.println("Received ctrl+c: shutting down"); }
		 });
		
		if(version == 1) {
			isCongestionControlSupported = false;
		} else if (version > 1) {
			isCongestionControlSupported = true;
		}
		 // TODO ??????
		
		if (args.length != 2) System.err.println("Incorrect number of arguments!");
		final int publishPort = Integer.parseInt(args[0]);
		final int subscribePort = Integer.parseInt(args[1]);
		
		Thread clientListenThread = new Thread(new Runnable() 
				{ public void run() {try {
					UDPClientEngine.listenForClientMessages(subscribePort);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}} });
		clientListenThread.start();
		
		Thread sensorListenThread = new Thread(new Runnable() 
		{ public void run() {try {
			UDPSensorEngine.listenForSensorMessages(publishPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}} });
		sensorListenThread.start();
	}
}
