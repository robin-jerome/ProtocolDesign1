package com.aalto.protocol.design.iotps.objects;

public class IoTPSAckObject {

	private int sub_seq_no;
	private int seq_no;
	
	private String fromIp;
	private int fromPort;
	
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
	public String getFromIp() {
		return fromIp;
	}
	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}
	public int getFromPort() {
		return fromPort;
	}
	public void setFromPort(int fromPort) {
		this.fromPort = fromPort;
	}
}
