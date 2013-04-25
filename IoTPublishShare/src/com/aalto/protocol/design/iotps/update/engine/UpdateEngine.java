package com.aalto.protocol.design.iotps.update.engine;

import java.util.ArrayList;
import java.util.List;

import com.aalto.protocol.design.iotps.db.engine.DBEngine;
import com.aalto.protocol.design.iotps.json.engine.JSON_Object;
import com.aalto.protocol.design.iotps.objects.IoTPSClientObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;
import com.aalto.protocol.design.iotps.objects.IoTPSUpdateObject;

public class UpdateEngine {
	
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
			u.setData(sensorUpdate.getData());
			updates.add(u);
		}
		return updates;
	}
}
