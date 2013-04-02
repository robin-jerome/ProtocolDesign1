package com.aalto.protocol.design.iotps.packet.sender;

import java.util.LinkedList;

import com.aalto.protocol.design.datastructure.MyQueue;
import com.aalto.protocol.design.datastructure.Packet;
import com.aalto.protocol.design.iotps.udp.engine.UDPClientEngine;

public class PacketSender {

	private String serverIp;
	
	private String remoteIp;
	
	private int serverPort;
	
	private int remotePort;
	
	private MyQueue myQueue;
	
	private String name;
	
	public String getServerIp() {
		return serverIp;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public MyQueue getMyQueue() {
		return myQueue;
	}
	
	public String getName() {
		return name;
	}

	public PacketSender(String serverIp, String toIp, int serverPort, int toPort) {
		super();
		this.serverIp = serverIp;
		this.remoteIp = toIp;
		this.serverPort = serverPort;
		this.remotePort = toPort;
		this.name = serverIp+":"+serverPort+"-"+toIp+":"+toPort;
		myQueue = new MyQueue();
	}

	public void startProcess(){
		if(null == myQueue) {
			System.out.println("Packet Sender not initialized yet.");
		} else {
			System.out.println("Packet Sender initialized.");
			new Thread(new Runnable() {
			    public void run() {
			        while(true){
			        	LinkedList<Packet> packetList = myQueue.getSendingWindow();
			        	for( Packet packet: packetList ) {
			        		if(!packet.isSent()){
			        			try {
									UDPClientEngine.sendToClient(remoteIp, remotePort, packet.getJsonObject().toJSONString());
									packet.setSent(true); // setting that the packet has been sent
								} catch (Exception e) {
									System.err.println("Packet Sending Failed:"+e.getMessage());
									e.printStackTrace();
								}
			        		}
			        	}
			        }
			    }
			}).start();
		}
	}
}
