package com.aalto.protocol.design.iotps.objects;

public class IoTPSSensorObject extends IoTPSObject {

	private String deviceId;

	private long latestSeqNum;
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
	public long getLatestSeqNum() {
		return latestSeqNum;
	}
	public void setLatestSeqNum(long latestSeqNum) {
		this.latestSeqNum = latestSeqNum;
	}
	
	@Override
	public String toString() {
		return deviceId+" "+latestSeqNum+" "+latestData;
	}
}
