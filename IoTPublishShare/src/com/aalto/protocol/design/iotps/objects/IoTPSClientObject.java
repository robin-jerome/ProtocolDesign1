package com.aalto.protocol.design.iotps.objects;

public class IoTPSClientObject extends IoTPSObject {

	private String deviceId;
	private int port;
	private long sub_seq_no;
	private long seq_no;
	private int version;
	private int ack_support;
	private String ip;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getSubSeqNo() {
		return sub_seq_no;
	}
	public void setSubSeqNo(long sub_seq_no) {
		this.sub_seq_no = sub_seq_no;
	}
	public long getSeqNo() {
		return seq_no;
	}
	public void setSeqNo(long seq_no) {
		this.seq_no = seq_no;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getAckSupport() {
		return ack_support;
	}
	public void setAckSupport(int ack_support) {
		this.ack_support = ack_support;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Override
	public String toString() {
		return deviceId+" "+ip+" "+port+" "+version;
	}
}
