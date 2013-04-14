package com.aalto.protocol.design.iotps.json.engine;

import com.aalto.protocol.design.iotps.objects.IoTPSSensorUpdateObject;

public class SensorDataParser {

	private String extractFieldWithName(String fieldName, String revdMsg){//extract data field of every strF 
		fieldName="'"+fieldName+"': '";
		
		int id_index0 = revdMsg.indexOf(fieldName);
		
		if (id_index0!=-1){
			id_index0+=fieldName.length();
			int id_index1 = revdMsg.indexOf("'",id_index0);
			if(id_index1!=-1)
				return(revdMsg.substring(id_index0, id_index1));
		}
		return null; //if fieldName was not found or its data field does not end with ' 
	}


	private String extractDeviceId(String revdMsg){//returns dev_id field
		return extractFieldWithName("dev_id", revdMsg);
	}


	private String extractSensorData(String revdMsg){//returns sensor_data field or "parsingError"
		return extractFieldWithName("sensor_data", revdMsg);
		
	}


	private Integer extractSequenceNumber(String revdMsg){//returns seq_no field or -1 for errors
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
		

	private Double extractTimeStamp(String revdMsg) {//returns ts field or -1 for errors
		String timeStampString = (extractFieldWithName("ts",revdMsg));
		if(timeStampString == null){
			return null;
		} else {
			try{
				return(Double.parseDouble(extractFieldWithName("ts",revdMsg)));	
			} catch (NumberFormatException ex ){
				return null;
			}
		}
	}

	private Integer extractDataSize(String revdMsg) {//returns data_size field or -1 for errors
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

	public IoTPSSensorUpdateObject generateSensorObject(String rcvMsg){

		IoTPSSensorUpdateObject tempObject = new IoTPSSensorUpdateObject();

		if (extractDeviceId(rcvMsg)==null)
			return null;
		else tempObject.setDevId(extractDeviceId(rcvMsg));

		if (this.extractSensorData(rcvMsg)==null)
			return null;
		else tempObject.setData(extractSensorData(rcvMsg));

		if (this.extractDataSize(rcvMsg)==-1)
			return null;
		else tempObject.setDataSize(extractDataSize(rcvMsg));

		if (this.extractSequenceNumber(rcvMsg)==-1)
			return null;
		else tempObject.setSeqNo(extractSequenceNumber(rcvMsg));

		if (this.extractTimeStamp(rcvMsg)==-1)
			return null;
		else tempObject.setTimeStamp(extractTimeStamp(rcvMsg));

		String deviceNameString=this.extractDeviceId(rcvMsg);//set devNo in tempObject
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

}
