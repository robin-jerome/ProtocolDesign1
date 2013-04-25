package com.aalto.protocol.design.iotps.ack.engine;

import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.utils.IoTUtils;

public class AckEngine {

	public static IoTPSAckObject getAckObjectFromUDPMessage(String receivedMsg) throws Exception {
		JSON_Object o = new JSON_Object(receivedMsg);
		IoTPSAckObject ack = new IoTPSAckObject();
		ack.setSeqNo((int)o.GetNumberValue("seq_no"));
		ack.setSubSeqNo((int)o.GetNumberValue("sub_seq_no"));
		
		// TODO Schema or datagram??
		ack.setFromIp(o.GetValue("client_ip"));
		ack.setFromPort((int)o.GetNumberValue("client_port"));
		//
		return ack;
	}

	public static void removeFromPendingAcks(IoTPSAckObject ackObj) {
		int remotePort = ackObj.getFromPort();
		String remoteIp = ackObj.getFromIp();
		String serverIp = IoTUtils.getMyClientFacingIp();
		int serverPort = IoTUtils.getMyClientFacingPort();
		String queueName = serverIp+":"+serverPort+"-"+remoteIp+":"+remotePort;
		if(PacketSenderRepo.packetSenderMap.containsKey(queueName)){
			boolean removed = PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().removePacketWithSeqNumFromQueue(ackObj.getSeqNo());
			if(removed){
				// either increment linearly or exponentially -- To be done
				boolean isLinear = false;
				if(isLinear){
					PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().linearIncementCwnd();
				} else {
					PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().exponentialIncementCwnd();
				}

			} else {
				PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().halveCwnd();
			}
		}
		
		
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
