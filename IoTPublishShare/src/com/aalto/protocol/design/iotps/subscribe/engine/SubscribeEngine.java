package com.aalto.protocol.design.iotps.subscribe.engine;

import java.util.List;

import com.aalto.protocol.design.iotps.db.engine.DBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSender;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.utils.IoTUtils;

public class SubscribeEngine {

	public static IoTPSSubscribeObject getSubscribeObjectFromUDPMessage(String receivedMsg) throws Exception {
		JSON_Object o = new JSON_Object(receivedMsg);
		IoTPSSubscribeObject subs = new IoTPSSubscribeObject();
		subs.setDeviceId(o.GetValue("device_id"));
		subs.setIp(o.GetValue("client_ip"));
		subs.setPort((int)o.GetNumberValue("client_port"));
		subs.setSeqNo((int)o.GetNumberValue("seq_no"));
		subs.setSubSeqNo((int)o.GetNumberValue("sub_seq_no"));
		subs.setVersion(1); //TODO
		
		try {
			subs.setAckSupport((int)o.GetNumberValue("ack_support"));
		} catch (Exception e) {
			subs.setAckSupport(0);
		}
		
		return subs;
	}

	public static void addSubscription(IoTPSSubscribeObject subObj) {
		
		/*
		 * 1. Add entry to DB
		 * 2. Create Queue
		 * 3. Send ACK
		 * 4. Send first update
		 */
		String remoteIp = subObj.getIp();
		int remotePort = subObj.getPort();
		String selectClientQuery = "select * from client_table where ip = '"+remoteIp+"' and port ='"+remotePort+"'";
		List<IoTPSObject> iotPSObjectList = DBEngine.executeQuery(selectClientQuery, DBEngine.CLIENT_OBJECT);
		if(null != iotPSObjectList && iotPSObjectList.size()>0){
			// There is already a Subscription -- Delete and recreate
			removeSubscription(subObj);
		}
		String insertClientQuery = "insert into client_table (ip, port, sub_seq_no, seq_no, device_id, ack_support, version) values ('"+subObj.getIp()+"',"+subObj.getPort()+","+subObj.getSubSeqNo()+","+subObj.getSeqNo()+", '"+subObj.getDeviceId()+"', "+subObj.getAckSupport()+", "+subObj.getVersion()+"); ";
		DBEngine.executeUpdate(insertClientQuery);
		
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+remoteIp+":"+remotePort;
		PacketSender ps = new PacketSender(IoTUtils.getMyClientFacingIp(), remoteIp, IoTUtils.getMyClientFacingPort(), remotePort);
		PacketSenderRepo.packetSenderMap.put(queueName, ps);
		PacketSenderRepo.packetSenderMap.get(queueName).startProcess();
		
		// Send ACK for the subscribe request
		
		// Send First Update - If any
		
	}

	public static void removeSubscription(IoTPSSubscribeObject subObj) {
		/*
		 * 1. Remove entry from DB
		 * 2. Send ACK if necessary
		 * 3. Delete Queue from repo
		 */
		
		String remoteIp = subObj.getIp();
		int remotePort = subObj.getPort();
		// Delete from client DB 
		String deleteQuery = "delete from client_table where ip = '"+remoteIp+"' and port ='"+remotePort+"'";
		DBEngine.executeUpdate(deleteQuery);
		// Send ACK for the un-subscribe request
		
		// Delete Queue from repo
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+remoteIp+":"+remotePort;
		
		if(PacketSenderRepo.packetSenderMap.containsKey(queueName)){
			PacketSenderRepo.packetSenderMap.get(queueName).stopProcess();
			PacketSenderRepo.packetSenderMap.remove(queueName);	
		}
		
	}

}
