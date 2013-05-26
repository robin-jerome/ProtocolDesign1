package com.aalto.protocol.design.iotps.ack.engine;

import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSAckObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;
import com.aalto.protocol.design.iotps.utils.IoTUtils;

public class AckEngine {

	public static IoTPSAckObject getAckObjectFromUDPMessage(JSON_Object o) throws Exception {
		
		IoTPSAckObject ack = new IoTPSAckObject();
		ack.setSeqNo((double)o.GetNumberValue("seq_no"));
		ack.setSubSeqNo((double)o.GetNumberValue("sub_seq_no"));

		// TODO Schema or datagram??
		ack.setFromIp(o.GetValue("client_ip"));
		ack.setFromPort((int)o.GetNumberValue("client_port"));
		System.out.println("Ack object created from receivedMessage::"+ack);
		
		return ack;
	}

	public static void removeFromPendingAcks(IoTPSAckObject ackObj) {
		int remotePort = ackObj.getFromPort();
		String remoteIp = ackObj.getFromIp();
		String serverIp = IoTUtils.getMyClientFacingIp();
		int serverPort = IoTUtils.getMyClientFacingPort();
		String queueName = serverIp+":"+serverPort+"-"+remoteIp+":"+remotePort;
		System.out.println("Queue Name for the ACK -"+queueName);
		
		if(PacketSenderRepo.packetSenderMap.containsKey(queueName)){
			System.out.println("Queue Exists - "+queueName);
			boolean removed = PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().removePacketWithSeqNumFromQueue(ackObj.getSeqNo());
			System.out.println("Ack Removed - "+removed);
			if(IoTPSServerStarter.isCongestionControlSupported){
				if(removed) {
					// either increment linearly or exponentially -- To be done
					boolean isLinear = true;
					if(isLinear){
						PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().linearIncementCwnd();
						System.out.println("Linear Increase in CWND ");
					} else {
						PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().exponentialIncementCwnd();
						System.out.println("Exponential Increase in CWND ");
					}

				} else {

					PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().halveCwnd();
					System.out.println("Halving the CWND as the Acknowledgement is duplicate");
				}
			} else {
				System.out.println("Server doesn't support Congestion Control - No change in rates");
			}

		} else {
			System.err.println("Queue Name -"+queueName+" does not exist - Something is wrong !!!");
		} 
	}

}
