package com.aalto.protocol.design.iotps.subscribe.engine;

import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;

public class SubscribeEngine {

	public static IoTPSSubscribeObject getSubscribeObjectFromUDPMessage(
			String receivedMsg) {
		
		return null;
	}

	public static void addSubscription(IoTPSSubscribeObject subObj) {
		
		/*
		 * 1. Add entry to DB
		 * 2. Send ACK
		 * 3. Send first update
		 */
	}

	public static void removeSubscription(IoTPSSubscribeObject subObj) {
		/*
		 * 1. Remove entry from DB
		 * 2. Send ACK if necessary
		 */
		
	}

}
