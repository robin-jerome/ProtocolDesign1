package com.aalto.protocol.design.iotps.update.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import com.aalto.protocol.design.datastructure.Packet;
import com.aalto.protocol.design.iotps.db.engine.SQLiteDBEngine;
import com.aalto.protocol.design.iotps.objects.IoTPSClientObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSubscribeObject;
import com.aalto.protocol.design.iotps.objects.IoTPSUpdateObject;
import com.aalto.protocol.design.iotps.packet.sender.PacketSender;
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
			out.write("receive_"+System.currentTimeMillis()+" \t" + logData+"\n");
			out.close();
		} catch (Exception e) {System.err.println("Error: " + e.getMessage());}
		// ---------------------------------------------------
		
		/*
		 * 1.Check for sensor in DB
		 * 2.If not present, create new entry in table with correct values
		 * 3.If present, update the existing sequence number and latest Json data
		 */
		
		String sensorSelectQuery = "select * from sensor_table where device_id = '" + sensorUpdate.getDevId() + "'";
		List<IoTPSObject> sensorObjList = SQLiteDBEngine.executeQuery(sensorSelectQuery, SQLiteDBEngine.SENSOR_OBJECT);
		if(null!=sensorObjList && sensorObjList.size()>0) {
//			System.out.println("Sensor update:"+sensorUpdate.getDevId()+"SeqNum"+sensorUpdate.getSeqNo());
			String updateSensorQuery = "update sensor_table set latest_seq_num = ?, latest_json_data = ? where device_id = ?";
			if(sensorUpdate.getDataSize() == -1) {
				updateSensorQuery = "update sensor_table set latest_json_data = ? where device_id = ?";
			} 
			SQLiteDBEngine.executeUpdate(updateSensorQuery, sensorUpdate.getSeqNo(), sensorUpdate.getData(), sensorUpdate.getDevId());
//			System.out.println("Sensor Value updation Successful");
		} else {
			System.out.println("Sensor details no present in the server - Values will be inserted");
			String insertSensorQuery = "insert into sensor_table (latest_seq_num, latest_json_data, device_id) values (?,?,?)";
			
			if(sensorUpdate.getDataSize() == -1L) {
				insertSensorQuery = "insert into sensor_table (latest_seq_num, latest_json_data, device_id) values (?,?,?)";
			}
			
			SQLiteDBEngine.executeUpdate(insertSensorQuery, 0L, sensorUpdate.getData(), sensorUpdate.getDevId());
//			System.out.println("Sensor Value insertion Successful");
		}
		
		// logic to Update the new Sequence number in the client table for every update received from sensor
		// This new 
		String updateClientSeqNumQuery = "update client_table set seq_no = (seq_no + 1) where device_id = '"+sensorUpdate.getDevId()+"'";
		SQLiteDBEngine.executeUpdate(updateClientSeqNumQuery);
		
		List<IoTPSUpdateObject> updates = getUpdateObjects(sensorUpdate);
		for (IoTPSUpdateObject o : updates) {
			sendUpdate(o);
		}
	}
	
	public static List<IoTPSUpdateObject> getUpdateObjects(IoTPSSensorUpdateObject sensorUpdate) {
		String selectClientQuery = "select * from client_table where device_id = '" + sensorUpdate.getDevId() + "'";
		List<IoTPSObject> clients = SQLiteDBEngine.executeQuery(selectClientQuery, SQLiteDBEngine.CLIENT_OBJECT);
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
		if(PacketSenderRepo.packetSenderMap.containsKey(queueName)){
			PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().pushToQueue(packet);
		} else {
			System.out.println("Queue Name to be added: "+queueName);
			PacketSender ps = new PacketSender(IoTUtils.getMyClientFacingIp(), update.getClientIp(), IoTUtils.getMyClientFacingPort(), update.getClientPort());
			PacketSenderRepo.packetSenderMap.put(queueName, ps);
			PacketSenderRepo.packetSenderMap.get(queueName).startProcess();
			System.out.println("Created and Started the Queue: "+queueName);
			PacketSenderRepo.packetSenderMap.get(queueName).getMyQueue().pushToQueue(packet);
		}
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
		List<IoTPSObject> clients = SQLiteDBEngine.executeQuery(latestDataQuery, SQLiteDBEngine.CLIENT_OBJECT);
		
		IoTPSSensorObject sensor = (IoTPSSensorObject)clients.get(0);
		return sensor.getLatestData();
	}
}










