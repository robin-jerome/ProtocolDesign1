package com.aalto.protocol.design.iotps.update.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.aalto.protocol.design.datastructure.Packet;
import com.aalto.protocol.design.iotps.db.engine.DBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSClientObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.objects.IoTPSUpdateObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSenderRepo;
import com.aalto.protocol.design.iotps.utils.IoTUtils;

public class UpdateEngine {
	
	public static void update(IoTPSSensorUpdateObject sensorUpdate) {
		// -------- Log incoming sensor data -----------------
		String filename = "server_" + sensorUpdate.getDevId() + ".log";
		String logData = sensorUpdate.getData();
		if (sensorUpdate.getDevId().contains("camera")) logData = Integer.toString(logData.length());
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
			out.write("receive_ts \t" + logData);
			out.close();
		} catch (Exception e) {System.err.println("Error: " + e.getMessage());}
		// ---------------------------------------------------
		

		
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
		Packet packet = new Packet();
		packet.setSeqNum(update.getSeqNo());
		packet.setJsonObject(update.getJSONObject());
		
		String queueName = IoTUtils.getMyClientFacingIp()+":"+IoTUtils.getMyClientFacingPort()+"-"+update.getClientIp()+":"+update.getClientPort();
		PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().pushToQueue(packet);
	}
	
	public static void sendFirstUpdate(IoTPSSubscribeObject subs) {
		String data = getLatestData(subs.getDeviceId());
		if (data == null) return;
		
		IoTPSUpdateObject update = new IoTPSUpdateObject();
		update.setAckSupport(subs.getAckSupport());
		update.setClientIp(subs.getIp());
		update.setClientPort(subs.getPort());
		update.setDeviceId(subs.getDeviceId());
		update.setSensorData(data);
		update.setSeqNo(subs.getSeqNo());
		update.setSubSeqNo(subs.getSubSeqNo());
		update.setTimestamp(System.currentTimeMillis() / 1000);
		update.setVersion(subs.getVersion());
		
		sendUpdate(update);
	}
	
	public static String getLatestData(String dev_id) {
		String latestDataQuery = "select * from sensor_table where device_id = '" + dev_id + "'";
		List<IoTPSObject> clients = DBEngine.executeQuery(latestDataQuery, DBEngine.CLIENT_OBJECT);
		
		IoTPSSensorObject sensor = (IoTPSSensorObject)clients.get(0);
		return sensor.getLatestData();
	}
}










