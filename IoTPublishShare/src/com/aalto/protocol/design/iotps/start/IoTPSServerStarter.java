package com.aalto.protocol.design.iotps.start;

public class IoTPSServerStarter {

	public static int version = 1; // to be taken as argument
	
	public static boolean isCongestionControlSupported = false;

	/* Arguments to the script
	 * ./iot-server –p <publish port> -s <subscribe port>
	 */
	public static void main(String[] args) {
		
		
		
		if(version == 1) {
			isCongestionControlSupported = false;
		} else if (version > 1) {
			isCongestionControlSupported = true;
		}

	}
}
