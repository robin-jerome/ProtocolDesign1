package com.aalto.protocol.design.iotps.ack.engine;

import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;

public class AckEngine {

	public static IoTPSAckObject getAckObjectFromUDPMessage(String receivedMsg) {
		
		return null;
	}

	public static void removeFromPendingAcks(IoTPSAckObject ackObj) {
		
	}

	public static void main(String[] args) {
		
		int seq_no = 1;
		int sub_seq_no = 1;
		String ackMessage =  getAckMessage(sub_seq_no,seq_no);
		System.out.println(ackMessage);
	}

	private static String getAckMessage(int sub_seq_no, int seq_no) {
		
		String ack = "";
			
		return ack;
	}
}
