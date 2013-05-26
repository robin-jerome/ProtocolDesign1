package com.aalto.protocol.design.iotps.packet.sender;

import java.util.concurrent.ConcurrentHashMap;

public class PacketSenderRepo {
	
	public static ConcurrentHashMap<String, PacketSender> packetSenderMap = new ConcurrentHashMap<String, PacketSender>();

}
