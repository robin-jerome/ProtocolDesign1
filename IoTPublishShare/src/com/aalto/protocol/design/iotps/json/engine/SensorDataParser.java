package com.aalto.protocol.design.iotps.json.engine;

import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;

public class SensorDataParser {

	private static String extractFieldWithName(String fieldName, String revdMsg){//extract data field of every strF 
		fieldName="'"+fieldName+"': '";
		
		int id_index0 = revdMsg.indexOf(fieldName);
		
		if (id_index0!=-1){
			id_index0+=fieldName.length();
			int id_index1 = revdMsg.indexOf("'",id_index0);
			if(id_index1!=-1)
				return(revdMsg.substring(id_index0, id_index1));
		}
		return ""+-1; //if fieldName was not found or its data field does not end with ' 
	}


	private static String extractDeviceId(String revdMsg){//returns dev_id field
		return extractFieldWithName("dev_id", revdMsg);
	}


	private static  String extractSensorData(String revdMsg){//returns sensor_data field or "parsingError"
		return extractFieldWithName("sensor_data", revdMsg);
	}


	private static  Integer extractSequenceNumber(String revdMsg){//returns seq_no field or -1 for errors
		String seqNumString = extractFieldWithName("seq_no", revdMsg);
		if( seqNumString == null) {
			return null;
		} else {
			try {
				return(Integer.parseInt(extractFieldWithName("seq_no",revdMsg)));	
			}
			catch (NumberFormatException ex ){
				return null;
			}
		}
	}
		

	private static  Long extractTimeStamp(String revdMsg) {//returns ts field or -1 for errors
		String timeStampString = (extractFieldWithName("ts",revdMsg));
		if(timeStampString == ""+-1){
			return -1L;
		} else {
			try{
				return(Long.parseLong(extractFieldWithName("ts",revdMsg).substring(0, extractFieldWithName("ts",revdMsg).indexOf("."))));	
			} catch (NumberFormatException ex ){
				return -1L;
			}
		}
	}

	private static  Integer extractDataSize(String revdMsg) {//returns data_size field or -1 for errors
		
		String dataSizeString = extractFieldWithName("data_size",revdMsg);
		if(dataSizeString == null) {
			return null;
		} else {
			try {
				return(Integer.parseInt(extractFieldWithName("data_size",revdMsg)));	
			}
			catch (NumberFormatException ex ){
				return null;
			}
		}
	}

	public static IoTPSSensorUpdateObject generateSensorObject(String rcvMsg){

		IoTPSSensorUpdateObject tempObject = new IoTPSSensorUpdateObject();

		if (extractDeviceId(rcvMsg)==null)
			return null;
		else tempObject.setDevId(extractDeviceId(rcvMsg));

		if (extractSensorData(rcvMsg)==null)
			return null;
		else tempObject.setData(extractSensorData(rcvMsg));

		if (extractDataSize(rcvMsg)==-1) {
			if(tempObject.getDevId().startsWith("camera")) { // Handling Camera data segmentation
				tempObject.setDataSize(-1);
			} else {
				return null;
			}
		} else tempObject.setDataSize(extractDataSize(rcvMsg));

		if (extractSequenceNumber(rcvMsg)==-1) {
			if(tempObject.getDevId().startsWith("camera")) { // Handling Camera data segmentation
				tempObject.setSeqNo(-1);
			} else {
				return null;
			}
		} else tempObject.setSeqNo(extractSequenceNumber(rcvMsg));

		if (extractTimeStamp(rcvMsg)==-1) {
			if(tempObject.getDevId().startsWith("camera")) { // Handling Camera data segmentation
				tempObject.setTimeStamp(0);
			} else {
				return null;
			}
		}  else tempObject.setTimeStamp(extractTimeStamp(rcvMsg));

		String deviceNameString=extractDeviceId(rcvMsg);//set devNo in tempObject
		int id_index0 = deviceNameString.indexOf("_");
		if (id_index0!=-1){
			try{
				tempObject.setDevNumber(Integer.parseInt(deviceNameString.substring(id_index0+1)));	
			}
			catch (NumberFormatException ex ){
				return null;
			}//set devNo in tempObject
		}

		return tempObject;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sampleUpdate= "'dev_id': 'camera_1', 'sensor_data': 'NO_MOTION', 'seq_no': '1', 'ts': '1361706055.55', 'data_size': '9'";
		IoTPSSensorUpdateObject tempMyObject = SensorDataParser.generateSensorObject(sampleUpdate);
		if(tempMyObject != null){
			tempMyObject.print();
		}
		else 
			System.out.println("error in string");

	}

}
