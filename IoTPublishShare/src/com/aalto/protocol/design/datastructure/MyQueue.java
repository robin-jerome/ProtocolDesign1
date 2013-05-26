package com.aalto.protocol.design.datastructure;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;

public class MyQueue {

	private ConcurrentLinkedQueue<Packet> packetsLinkedList= new ConcurrentLinkedQueue<Packet>();
	
	private ConcurrentLinkedQueue<Packet> windowLinkedList= new ConcurrentLinkedQueue<Packet>();
	
	private int windowStart = 0; // Always zero ???
	
	private int windowEnd = 1; // Starting with 1 -> incremented
	
	public ConcurrentLinkedQueue<Packet> getSendingWindow() {
		
		if(windowLinkedList.size() == (windowEnd-windowStart)){             // the window is full
			return windowLinkedList;
		} else if (windowLinkedList.size() < (windowEnd-windowStart)){      // window is not full -- need to fetch entries from bigger Linked List
			int count = (windowEnd-windowStart)-windowLinkedList.size();
			for (Packet element : packetsLinkedList){
				if(count > 0){
					windowLinkedList.add(element);
					count--;
					packetsLinkedList.remove();
				} else {
					break;
				}
			}
			return windowLinkedList;
		} else {                                                      // window is bigger than expected -- shrink window and send the stuff ( packets might be lost)          
			ConcurrentLinkedQueue<Packet> shrinkedList= new ConcurrentLinkedQueue<Packet>();
			int count = 0;
			for (Packet element : packetsLinkedList){
				if(count<(windowEnd-windowStart)){
					shrinkedList.add(element);
				} else {
					break;
				}
			}
			windowLinkedList = shrinkedList;
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
				if(packet.isSent() && packet.getSeqNum()==seqNum){
					removed = removeFromQueue(packet);
					break;
				}
			}
			return removed;
		} else {
			for (Packet packet : packetsLinkedList){
				if(packet.isSent() && packet.getSeqNum()==seqNum){
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
		Packet[] temp = windowLinkedList.toArray(new Packet[0]);
		ConcurrentLinkedQueue<Packet> newList = new ConcurrentLinkedQueue<Packet>();
		for (int i = (windowEnd/2); i < windowEnd; i++) {
			newList.offer(temp[i]);
			windowLinkedList.remove(temp[i]);
		}
		for (Packet p : packetsLinkedList) {
			newList.offer(p);
		}
		packetsLinkedList = newList;
		
		if(windowEnd>2){
			windowEnd = windowEnd/2;
		} else {
			windowEnd = 1;
		}
		
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

	
	
}
