package com.aalto.protocol.design.iotps.objects;

public class IoTPSSensorObject extends IoTPSObject {

	private String deviceId;
	private double latestSeqNum;
	private String latest_data;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public double getLatestSeqNum() {
		return latestSeqNum;
	}
	public void setLatestSeqNum(double latestSeqNum) {
		this.latestSeqNum = latestSeqNum;
	}
	
	public String getLatestData() {
		return latest_data;
	}
	
	public void setLatestData(String data) {
		this.latest_data = data;
	}
}
