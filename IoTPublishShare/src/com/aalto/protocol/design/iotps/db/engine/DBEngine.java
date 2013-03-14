package com.aalto.protocol.design.iotps.db.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aalto.protocol.design.iotps.objects.IoTPSObject;
import com.aalto.protocol.design.iotps.objects.IoTPSSensorObject;


public class DBEngine {
	
	private static final String DB_USER_NAME = "root";
	private static final String DB_PASSWORD = "root";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/iotps";
	private static final String SENSOR_OBJECT = "sensor";
	private static final String CLIENT_OBJECT = "client";
	private static final String PENDING_ACK_OBJECT = "pending_acks";
	
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
	
		String updateQuery = "insert into sensor_table (device_id,latest_seq_num) values ('my_crap', 1); ";
		String selectQuery = "select * from sensor_table; ";
		executeUpdate(updateQuery);
		List <IoTPSObject> sensorObjList = executeQuery(selectQuery, SENSOR_OBJECT);
		for (Iterator iterator = sensorObjList.iterator(); iterator.hasNext();) {
			IoTPSSensorObject ioTPSObject = (IoTPSSensorObject) iterator.next();
			System.out.println("--"+ioTPSObject.getDeviceId());
		}
		System.out.println("Query executed");
	}




	private static List<IoTPSObject> executeQuery(String selectQuery, String objectType) {

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
						returnObjectList.add(sensorObj);
					} else if (CLIENT_OBJECT.equals(objectType)){
						/*
						 * Do something else
						 */
						
						
					} else if (PENDING_ACK_OBJECT.equals(objectType)){
						/*
						 * Do something else
						 */
						
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




	private static void executeUpdate(String updateQuery) {
		
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
		
	

}
