package com.aalto.protocol.design.iotps.objects;

public class IoTPSSensorObject extends IoTPSObject {

	private String deviceId;

	private double latestSeqNum;
	private String latestData;
	
	public String getLatestData() {
		return latestData;
	}
	public void setLatestData(String latestData) {
		this.latestData = latestData;
	}

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
}
