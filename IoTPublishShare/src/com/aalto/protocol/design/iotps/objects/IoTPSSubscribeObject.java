package com.aalto.protocol.design.iotps.objects;

public class IoTPSSubscribeObject {

	private String deviceId;
	private int port;
	private int sub_seq_no;
	private int seq_no;
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
	public int getSubSeqNo() {
		return sub_seq_no;
	}
	public void setSubSeqNo(int sub_seq_no) {
		this.sub_seq_no = sub_seq_no;
	}
	public int getSeqNo() {
		return seq_no;
	}
	public void setSeqNo(int seq_no) {
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
}
