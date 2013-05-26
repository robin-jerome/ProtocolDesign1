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
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;

public class SQLiteDBEngine {
	
	public static String DB_URL = "jdbc:sqlite:D:\\Software\\sqlite-shell-win32-x86-3071700\\iotps";
	public static final String SENSOR_OBJECT = "sensor";
	public static final String CLIENT_OBJECT = "client";
	
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Loaded SQLite JDBC Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws SQLException {
	
		String updateQuerySensor = "insert into sensor_table (device_id,latest_seq_num) values ('my_crap1', 2); ";
		executeUpdate(updateQuerySensor);
		String selectClientQuery = "select * from sensor_table; ";
		List <IoTPSObject> clientObjList = executeQuery(selectClientQuery, SENSOR_OBJECT);
		for (Iterator iterator = clientObjList.iterator(); iterator.hasNext();) {
			IoTPSSensorObject ioTPSObject = (IoTPSSensorObject) iterator.next();
			System.out.println("--"+ioTPSObject);
		}
		
		System.out.println("Query executed");

	}




	public static List<IoTPSObject> executeQuery(String selectQuery, String objectType) {

		Connection connection = null;
		PreparedStatement ps = null;
		List <IoTPSObject> returnObjectList = new ArrayList<IoTPSObject>();
		try {
			connection = DriverManager.getConnection(DB_URL);
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
						clientObj.setSeqNo(resultSet.getLong("seq_no"));
						clientObj.setIp(resultSet.getString("ip"));
						clientObj.setAckSupport(resultSet.getInt("ack_support"));
						clientObj.setPort(resultSet.getInt("port"));
						clientObj.setVersion(resultSet.getInt("version"));
						clientObj.setSubSeqNo(resultSet.getLong("sub_seq_no"));
						returnObjectList.add(clientObj);
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
			connection = DriverManager.getConnection(DB_URL);
			if(null != connection)
			{
				ps = connection.prepareStatement(selectQuery);
				ResultSet resultSet = ps.executeQuery();
				while(resultSet.next()){
					returnVal = resultSet.getString(1);
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
			connection = DriverManager.getConnection(DB_URL );
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
	
	public static void executeUpdate(String updateQuery, long latestSeqNum, String jsonData, String deviceId) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(DB_URL);
			if(null != connection)
			{
				ps = connection.prepareStatement(updateQuery);
				ps.setLong(1, latestSeqNum);
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
