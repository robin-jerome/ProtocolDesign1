package com.aalto.protocol.design.iotps.packet.sender;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.aalto.protocol.design.datastructure.MyQueue;
import com.aalto.protocol.design.datastructure.Packet;
import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;
import com.aalto.protocol.design.iotps.udp.engine.ServerToClientUDPEngine;
import com.aalto.protocol.design.iotps.utils.Constants;

public class PacketSender {

	private String serverIp;
	
	private String remoteIp;
	
	private int serverPort;
	
	private int remotePort;
	
	private MyQueue myQueue;
	
	private String name;
	
	private Thread packetSenderThread = null; 
	
	private Thread timeoutCheckerThread = null; 
	
	private boolean packetSenderThreadStopped = false;
	
	private boolean timeoutCheckerThreadStopped = false;
	
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
			packetSenderThread = new Thread(new Runnable() {
			    public void run() {
			    	
			        while(!packetSenderThreadStopped){
			        	
			        	if(null != myQueue){ // Only non-null Queue is processed
			        		
			        		ConcurrentLinkedQueue<Packet> packetList = null;
			        		
			        		if(IoTPSServerStarter.isCongestionControlSupported) {
			        			packetList = myQueue.getSendingWindowBasedOnCongestion();
			        		} else {
			        			packetList = myQueue.getPacketsToSend();
			        		}
			        		int sentCount = 0;
			        		for( Packet packet: packetList ) {
				        		if(!packet.isSent()){
				        			try {
										ServerToClientUDPEngine.sendToClient(remoteIp, remotePort, packet.getJsonObject());
										myQueue.modifyTimeStampAndSent(packet.getSeqNum(),true,System.currentTimeMillis());
										myQueue.displayWindowSize();
										sentCount++;
						        		
									} catch (Exception e) {
										System.err.println("Packet Sending Failed:"+e.getMessage());
										e.printStackTrace();
									}
				        		}
				        	}	
			        		if(sentCount > 0) {
			        			System.out.println("Number of packets sent to client:"+sentCount);
			        		}
				        	
			        	}  else { // Stop Processing method called
			        		// Kill the thread if it is interrupted
			        		if (packetSenderThread.isInterrupted()) {
								packetSenderThread.stop();
							}
			        	}
			        	
			        }
			        
			    }
			});
			packetSenderThread.start();
			
			timeoutCheckerThread = new Thread(new Runnable() {
			    public void run() {
			    	
			        while(!timeoutCheckerThreadStopped){
			        	
			        	if(null != myQueue){ // Only non-null Queue is processed
			        		
			        		ConcurrentLinkedQueue<Packet> packetList = null;
			        		
			        		if(IoTPSServerStarter.isCongestionControlSupported) {
			        			packetList = myQueue.getWindowLinkedList();
			        		} else {
			        			packetList = myQueue.getPacketsToSend();
			        		}
			        				
			        		for( Packet packet: packetList ) {
				        		if(packet.isSent() && (System.currentTimeMillis()-packet.getTimeStamp() > Constants.WAIT_TIMEOUT)){
				        			myQueue.removePacketWithSeqNumFromQueue(packet.getSeqNum());
				        			if(IoTPSServerStarter.isCongestionControlSupported) {
					        			// halve congestion window
				        				myQueue.halveCwnd();
					        		} else {
					        			// Do nothing
					        		}	
				        		}
				        	}	
				        	
			        	}  else { // Stop Processing method called
			        		// Kill the thread if it is interrupted
			        		if (timeoutCheckerThread.isInterrupted()) {
			        			timeoutCheckerThread.stop();
							}
			        	}
			        	
			        }
			        
			    }
			});
			timeoutCheckerThread.start();
	
		}
	}
	
	public void stopProcess(){
		// Setting the queue to null would stop processing
		packetSenderThreadStopped = true;
		packetSenderThread.interrupt();
		timeoutCheckerThreadStopped = true;
		timeoutCheckerThread.interrupt();
		myQueue = null;
	}
}
