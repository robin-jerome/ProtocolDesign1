package com.aalto.protocol.design.iotps.subscribe.engine;

import java.util.List;


import com.aalto.protocol.design.iotps.db.engine.SQLiteDBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.objects.IoTPSUpdateObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSender;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.start.IoTPSServerStarter;
import com.aalto.protocol.design.iotps.udp.engine.ServerToClientUDPEngine;
import com.aalto.protocol.design.iotps.update.engine.UpdateEngine;
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
		
		System.out.println("Subscribe/Unsubscribe object created from receivedMessage::"+subs);
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
		String deviceId = subObj.getDeviceId();
		String selectClientQuery = "select * from client_table where ip = '"+remoteIp+"' and port ='"+remotePort+"' and device_id ='"+deviceId+"'";
		List<IoTPSObject> iotPSObjectList = SQLiteDBEngine.executeQuery(selectClientQuery, SQLiteDBEngine.CLIENT_OBJECT);
		if(null != iotPSObjectList && iotPSObjectList.size()>0){
			// There is already a Subscription -- Delete and recreate
			System.out.println("Subscription already exists - Recreating");
			removeSubscription(subObj);
		}
		String insertClientQuery = "insert into client_table (ip, port, sub_seq_no, seq_no, device_id, ack_support, version) values ('"+subObj.getIp()+"',"+subObj.getPort()+","+subObj.getSubSeqNo()+","+subObj.getSeqNo()+", '"+subObj.getDeviceId()+"', "+subObj.getAckSupport()+", "+subObj.getVersion()+"); ";
		SQLiteDBEngine.executeUpdate(insertClientQuery);
		System.out.println("Added client in the DB");
		
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+remoteIp+":"+remotePort;
		System.out.println("Queue Name to be added: "+queueName);
		PacketSender ps = new PacketSender(IoTUtils.getMyClientFacingIp(), remoteIp, IoTUtils.getMyClientFacingPort(), remotePort);
		PacketSenderRepo.packetSenderMap.put(queueName, ps);
		PacketSenderRepo.packetSenderMap.get(queueName).startProcess();
		System.out.println("Started processing queue with queueName: "+queueName);
		ServerToClientUDPEngine.sendAcknowledgementForSubscription(subObj);
		
		// Send First Update - If any
		String selectInitialDataQuery = "select latest_json_data from sensor_table where device_id ='"+deviceId+"'";
		String currentJSONDataString = SQLiteDBEngine.executeQuery(selectInitialDataQuery);
		String selectInitialSeqNumQuery = "select latest_seq_num from sensor_table where device_id ='"+deviceId+"'";
		String currentSeqNumString = SQLiteDBEngine.executeQuery(selectInitialSeqNumQuery);
		
		if(null!=currentJSONDataString && !"".equals(currentJSONDataString)) {
			IoTPSUpdateObject obj = new IoTPSUpdateObject();
			obj.setClientIp(remoteIp);
			obj.setClientPort(remotePort);
			obj.setSensorData(currentJSONDataString);
			obj.setDeviceId(deviceId);
			obj.setVersion(IoTPSServerStarter.version);
			obj.setSubSeqNo(subObj.getSubSeqNo());
			obj.setSeqNo(Integer.valueOf(currentSeqNumString));
			obj.setAckSupport(subObj.getAckSupport());
			obj.setTimestamp(System.currentTimeMillis());
			UpdateEngine.sendUpdate(obj);
			System.out.println("Sent initial update after subscription -"+obj);
		}
	}

	public static void removeSubscription(IoTPSSubscribeObject subObj) {
		/*
		 * 1. Remove entry from DB
		 * 3. Delete Queue from repo
		 */
		
		String remoteIp = subObj.getIp();
		int remotePort = subObj.getPort();
		// Delete from client DB 
		String deleteQuery = "delete from client_table where ip = '"+remoteIp+"' and port ='"+remotePort+"'";
		SQLiteDBEngine.executeUpdate(deleteQuery);
		
		// Delete Queue from repo
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+remoteIp+":"+remotePort;
		
		if(PacketSenderRepo.packetSenderMap.containsKey(queueName)){
			PacketSenderRepo.packetSenderMap.get(queueName).stopProcess();
			PacketSenderRepo.packetSenderMap.remove(queueName);	
			System.out.println("Queue removed "+queueName);
		}
		
		System.out.println("Unsubscribe complete for "+subObj);
	}

}
