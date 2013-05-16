package com.aalto.protocol.design.iotps.update.engine;

import java.util.ArrayList;
import java.util.List;

import com.aalto.protocol.design.datastructure.Packet;
import com.aalto.protocol.design.iotps.db.engine.DBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSClientObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;
import com.aalto.protocol.design.iotps.objects.IoTPSUpdateObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.utils.IoTUtils;

public class UpdateEngine {
	
	public static void update(IoTPSSensorUpdateObject sensorUpdate) {
		// Add logging for sensor data
		// Open a file with name, write and close
		
		List<IoTPSUpdateObject> updates = getUpdateObjects(sensorUpdate);
		for (IoTPSUpdateObject o : updates) {
			sendUpdate(o);
		}
	}
	
	public static List<IoTPSUpdateObject> getUpdateObjects(IoTPSSensorUpdateObject sensorUpdate) {
		String selectClientQuery = "select * from client_table where device_id = '" + sensorUpdate.getDevId() + "'";
		List<IoTPSObject> clients = DBEngine.executeQuery(selectClientQuery, DBEngine.CLIENT_OBJECT);
		List<IoTPSUpdateObject> updates = new ArrayList<IoTPSUpdateObject>();
		
		for (IoTPSObject client : clients) {
			IoTPSClientObject c = (IoTPSClientObject)client;
			IoTPSUpdateObject u = new IoTPSUpdateObject();
			
			u.setClientIp(c.getIp());
			u.setClientPort(c.getPort());
			u.setDeviceId(c.getDeviceId());
			u.setSeqNo(c.getSeqNo());
			u.setSubSeqNo(c.getSubSeqNo()); // TODO
			u.setVersion(c.getVersion());
			u.setAckSupport(c.getAckSupport());
			
			u.setTimestamp(sensorUpdate.getTimeStamp());
			u.setSensorData(sensorUpdate.getData());
			updates.add(u);
		}
		return updates;
	}
	
	public static void sendUpdate(IoTPSUpdateObject update){
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+update.getClientIp()+":"+update.getClientPort();
		Packet packet = new Packet();
		packet.setSeqNum(update.getSeqNo());
		// Petteri's code
		PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().pushToQueue(packet);
	}
}
