package com.aalto.protocol.design.datastructure;

import java.util.LinkedList;

import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;

public class MyQueue {

	private LinkedList<Packet> packetsLinkedList= new LinkedList<Packet>();
	
	private LinkedList<Packet> windowLinkedList= new LinkedList<Packet>();
	
	private int windowStart = 0; // Always zero ???
	
	private int windowEnd = 1; // Starting with 1 -> incremented
	
	public LinkedList<Packet> getSendingWindow() {
		
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
			LinkedList<Packet> shrinkedList= new LinkedList<Packet>();
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
				windowLinkedList.remove(windowLinkedList.indexOf(packet));
				return true;
			} else {
				// duplicate acknowledgement
				return false;
			}
		} else {
			if(packetsLinkedList.contains(packet)){
				packetsLinkedList.remove(packetsLinkedList.indexOf(packet));
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public boolean removePacketWithSeqNumFromQueue(int seqNum){
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
		for(int i=windowEnd; i>(windowEnd/2); i--){
			packetsLinkedList.addFirst(windowLinkedList.get(i)); // Moving elements from windowList to myLinkedList on halving the congestion Window
			windowLinkedList.remove(i);
		}
		if(windowEnd>2){
			windowEnd = windowEnd/2;
		} else {
			windowEnd = 1;
		}
		
	}
	
	public void setMinimumCwnd(){
		for(int i=windowEnd; i > 1; i--){
			packetsLinkedList.addFirst(windowLinkedList.get(i)); // Moving elements from windowList to myLinkedList on making the congestion Window 1
			windowLinkedList.remove(i);
		}
		windowEnd = 1;
	}
	
	public int getWindowStart() {
		return windowStart;
	}

	public int getWindowEnd() {
		return windowEnd;
	}

	public LinkedList<Packet> getPacketsToSend() {
		return packetsLinkedList;
	}

	
	
}
