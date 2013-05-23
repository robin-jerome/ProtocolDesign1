package com.aalto.protocol.design.iotps.utils;

import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;

public class IoTUtils {
	
	public static String getMyClientFacingIp() {
		return IoTPSServerStarter.getSelfIP();
	}

	public static int getMyClientFacingPort() {
		return IoTPSServerStarter.updatePort;
	}

}
