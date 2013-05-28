package com.aalto.protocol.design.datastructure;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;

public class MyQueue {

	private ConcurrentLinkedQueue<Packet> packetsLinkedList= new ConcurrentLinkedQueue<Packet>();
	
	private ConcurrentLinkedQueue<Packet> windowLinkedList= new ConcurrentLinkedQueue<Packet>();
	
	public ConcurrentLinkedQueue<Packet> getWindowLinkedList() {
		return windowLinkedList;
	}

	private int windowStart = 0; // Always zero ???
	
	private int windowEnd = 1; // Starting with 1 -> incremented
	
	public void displayWindowSize() {
		if(IoTPSServerStarter.isCongestionControlSupported) {
			System.out.println("Current window Size::"+ (windowEnd-windowStart));
		} else {
			System.out.println("Current window Size::"+ packetsLinkedList.size());
		}
		
	}
	
	
	
	public ConcurrentLinkedQueue<Packet> getSendingWindowBasedOnCongestion() {

		if(windowLinkedList.size() == (windowEnd-windowStart)) { 
			// the window is full -- return the window itself
			return windowLinkedList;
			
		} else if (windowLinkedList.size() < (windowEnd-windowStart) && !packetsLinkedList.isEmpty()) {      // window is not full -- need to fetch entries from bigger Linked List
			
			int count1 = (windowEnd-windowStart) - windowLinkedList.size(); 
			int count2 = packetsLinkedList.size();
			int count = Math.min(count1, count2);
			
			for (Packet element : packetsLinkedList){
				if(count > 0) {
					try {
						windowLinkedList.add(element);
						packetsLinkedList.remove();
						count--;
					} catch(Exception e) {
						System.err.println("Error while removal from queue");
						System.err.println("Error details count1:"+count1+" count2:"+count2+" windowLinkedListSize:"+windowLinkedList.size()+" packetLinkedListSize:"+
								packetsLinkedList.size()+" Current count value:"+count);
					}
				} else {
					break;
				}
			}
			
			return windowLinkedList;
			
		} else if(windowLinkedList.size() < (windowEnd-windowStart) && packetsLinkedList.isEmpty()) { 
			// Nothing to fetch from bigger queue as it is empty
			
			return windowLinkedList;
			
		} else if (windowLinkedList.size() > (windowEnd-windowStart)) {   
			
			// window is bigger than expected -- shrink window and send the stuff ( packets might be lost)  
			ConcurrentLinkedQueue<Packet> shrinkedList= new ConcurrentLinkedQueue<Packet>();
			int count = 0;
			for (Packet element : packetsLinkedList){
				if(count < (windowEnd-windowStart)){
					shrinkedList.add(element);
				} else {
					break;
				}
			}
			windowLinkedList = shrinkedList;
			
			return windowLinkedList;
			
		} else {
			// Dont know what to do -- 
			return windowLinkedList;
		}
		
	}
		


	public void pushToQueue(Packet element){
		packetsLinkedList.add(element);
	}
	
	private boolean removeFromQueue(Packet packet){
		if(IoTPSServerStarter.isCongestionControlSupported){
			if(windowLinkedList.contains(packet)){
				windowLinkedList.remove(packet);
				return true;
			} else {
				// duplicate acknowledgment
				return false;
			}
		} else {
			if(packetsLinkedList.contains(packet)){
				packetsLinkedList.remove(packet);
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public boolean removePacketWithSeqNumFromQueue(long seqNum){
		boolean removed = false;
		if(IoTPSServerStarter.isCongestionControlSupported){
			for (Packet packet : windowLinkedList){
				if(packet.getSeqNum() == seqNum){
					removed = removeFromQueue(packet);
					break;
				}
			}
			return removed;
		} else {
			for (Packet packet : packetsLinkedList){
				if(packet.getSeqNum() == seqNum){
					removed = removeFromQueue(packet);
					break;
				}
			}
			return removed;
		}
		
	}
	
	public void linearIncementCwnd(){
		windowEnd++;
	}
	
	public void exponentialIncementCwnd(){
		int currWindowSize = windowLinkedList.size();
		double power = Math.log(currWindowSize);
		power++;
		windowEnd = (int) Math.exp(power);
	}
	
	public void halveCwnd(){
		System.out.println("***** Congestion window set to half of its value for Queue *****");
		System.out.println("Initial window size "+(windowEnd-windowStart));
		Packet[] temp =  windowLinkedList.toArray(new Packet[0]);
		ConcurrentLinkedQueue<Packet> newList = new ConcurrentLinkedQueue<Packet>();
		for (int i = (windowEnd/2); (windowEnd> 0 && i < windowEnd && temp.length > 0); i++) {
			newList.offer(temp[i]);
			windowLinkedList.remove(temp[i]);
		}
		for (Packet p : packetsLinkedList) {
			newList.offer(p);
		}
		packetsLinkedList = newList;
		
		if(windowEnd > 2){
			windowEnd = windowEnd/2;
		} else {
			windowEnd = 1;
		}
		System.out.println("Final window size "+(windowEnd-windowStart));
	}
	
	public void setMinimumCwnd(){
		ConcurrentLinkedQueue<Packet> newList = new ConcurrentLinkedQueue<Packet>();
		for (Packet p : windowLinkedList) {
			newList.offer(p);
			windowLinkedList.remove(p);
		}
		packetsLinkedList = newList;
		windowEnd = 1;
	}
	
	public int getWindowStart() {
		return windowStart;
	}

	public int getWindowEnd() {
		return windowEnd;
	}

	public ConcurrentLinkedQueue<Packet> getPacketsToSend() {
		return packetsLinkedList;
	}

	public void modifyTimeStampAndSent(long seqNum, boolean isSent, long currentTimeMillis) {
		// Remove it and add it back ??
		
		removePacketWithSeqNumFromQueue(seqNum);
		Packet packet = new Packet();
		packet.setSeqNum(seqNum);
		packet.setSent(isSent);
		packet.setTimeStamp(currentTimeMillis);
		
		if(IoTPSServerStarter.isCongestionControlSupported){
			windowLinkedList.add(packet);
		} else {
			packetsLinkedList.add(packet);
		}
	}

	
	
}
