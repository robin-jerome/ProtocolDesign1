package com.aalto.protocol.design.iotps.db.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aalto.protocol.design.iotps.objects.IoTPSClientObject;
import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSPendingAcksObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;

public class SQLiteDBEngine {
	
	public static final String DB_USER_NAME = "root";
	public static final String DB_PASSWORD = "root";
	public static final String DB_URL = "jdbc:mysql://localhost:3306/iotps";
	public static final String SENSOR_OBJECT = "sensor";
	public static final String CLIENT_OBJECT = "client";
	public static final String PENDING_ACK_OBJECT = "pending_acks";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Loaded MySQL JDBC Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
	
		String updateQuerySensor = "insert into sensor_table (device_id,latest_seq_num) values ('my_crap', 1); ";
		executeUpdate(updateQuerySensor);
		String updateQueryClient = "insert into client_table (ip, port, sub_seq_no, seq_no, device_id, ack_support, version) values ('127.0.0.1', 5060, 1, 1, 'my_test_device', 0, 1); ";
		executeUpdate(updateQueryClient);
		String updateQueryPendingAck = "insert into pending_acks_table (sub_seq_no, seq_no) values (1, 1); ";
		executeUpdate(updateQueryPendingAck);
		
		String selectSensorQuery = "select * from sensor_table; ";
		List <IoTPSObject> sensorObjList = executeQuery(selectSensorQuery, SENSOR_OBJECT);
		for (Iterator iterator = sensorObjList.iterator(); iterator.hasNext();) {
			IoTPSSensorObject ioTPSObject = (IoTPSSensorObject) iterator.next();
			System.out.println("--"+ioTPSObject.getDeviceId());
		}
		
		String selectClientQuery = "select * from client_table; ";
		List <IoTPSObject> clientObjList = executeQuery(selectClientQuery, CLIENT_OBJECT);
		for (Iterator iterator = clientObjList.iterator(); iterator.hasNext();) {
			IoTPSClientObject ioTPSObject = (IoTPSClientObject) iterator.next();
			System.out.println("--"+ioTPSObject.getIp());
		}
		
		String selectPendingAcksQuery = "select * from pending_acks_table; ";
		List <IoTPSObject> pendingAcksObjList = executeQuery(selectPendingAcksQuery, PENDING_ACK_OBJECT);
		for (Iterator iterator = pendingAcksObjList.iterator(); iterator.hasNext();) {
			IoTPSPendingAcksObject ioTPSObject = (IoTPSPendingAcksObject) iterator.next();
			System.out.println("--"+ioTPSObject.getSubSeqNo());
		}
		
		System.out.println("Query executed");
	}




	public static List<IoTPSObject> executeQuery(String selectQuery, String objectType) {

		Connection connection = null;
		PreparedStatement ps = null;
		List <IoTPSObject> returnObjectList = new ArrayList<IoTPSObject>();
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD );
			if(null != connection)
			{
				ps = connection.prepareStatement(selectQuery);
				ResultSet resultSet = ps.executeQuery();
				
				while(resultSet.next()){
					if(SENSOR_OBJECT.equals(objectType)){
						IoTPSSensorObject sensorObj = new IoTPSSensorObject();
						sensorObj.setDeviceId(resultSet.getString("device_id"));
						sensorObj.setLatestSeqNum(resultSet.getInt("latest_seq_num"));
						sensorObj.setLatestData(resultSet.getString("latest_json_data"));
						returnObjectList.add(sensorObj);
					} else if (CLIENT_OBJECT.equals(objectType)){
						IoTPSClientObject clientObj = new IoTPSClientObject();
						clientObj.setDeviceId(resultSet.getString("device_id"));
						clientObj.setSeqNo(resultSet.getInt("seq_no"));
						clientObj.setIp(resultSet.getString("ip"));
						clientObj.setAckSupport(resultSet.getInt("ack_support"));
						clientObj.setPort(resultSet.getInt("port"));
						clientObj.setVersion(resultSet.getInt("version"));
						clientObj.setSubSeqNo(resultSet.getInt("sub_seq_no"));
						returnObjectList.add(clientObj);
					} else if (PENDING_ACK_OBJECT.equals(objectType)){
						IoTPSPendingAcksObject pendingAcksObj = new IoTPSPendingAcksObject();
						pendingAcksObj.setSeqNo(resultSet.getInt("seq_no"));
						pendingAcksObj.setSubSeqNo(resultSet.getInt("sub_seq_no"));
						returnObjectList.add(pendingAcksObj);
					}
					
				}
			}
	 
		} catch (SQLException e) {
			System.out.println("DriverManager.getConnection Failed!");
			e.printStackTrace();
			
		} finally {
		
			if (null != ps){
			try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return returnObjectList;
	}


	public static String executeQuery(String selectQuery) {

		Connection connection = null;
		PreparedStatement ps = null;
		String returnVal = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD );
			if(null != connection)
			{
				ps = connection.prepareStatement(selectQuery);
				ResultSet resultSet = ps.executeQuery();
				while(resultSet.next()){
					returnVal = resultSet.getString("latest_json_data");
				}
			}

		} catch (SQLException e) {
			System.out.println("DriverManager.getConnection Failed!");
			e.printStackTrace();

		} finally {

			if (null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return returnVal;
	}

	public static void executeUpdate(String updateQuery) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD );
			if(null != connection)
			{
				ps = connection.prepareStatement(updateQuery);
				ps.executeUpdate();
			}
	 
		} catch (SQLException e) {
			System.out.println("DriverManager.getConnection Failed!");
			e.printStackTrace();
			
		} finally {
		
			if (null != ps){
			try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void executeUpdate(String updateQuery, double latestSeqNum, String jsonData, String deviceId) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD );
			if(null != connection)
			{
				ps = connection.prepareStatement(updateQuery);
				ps.setDouble(1, latestSeqNum);
				ps.setString(2, jsonData);
				ps.setString(3, deviceId);
				ps.executeUpdate();
			}
	 
		} catch (SQLException e) {
			System.out.println("DriverManager.getConnection Failed!");
			e.printStackTrace();
			
		} finally {
		
			if (null != ps){
			try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
