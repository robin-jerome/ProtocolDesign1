package com.aalto.protocol.design.iotps.objects;

public class IoTPSAckObject {

	private double sub_seq_no;
	private double seq_no;
	
	private String fromIp;
	private int fromPort;
	
	public double getSubSeqNo() {
		return sub_seq_no;
	}
	public void setSubSeqNo(double sub_seq_no) {
		this.sub_seq_no = sub_seq_no;
	}
	public double getSeqNo() {
		return seq_no;
	}
	public void setSeqNo(double seq_no) {
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
	
	@Override
	public String toString() {
		return "Subscription ID:"+sub_seq_no+" Sequence Number:"+seq_no+" From IP:"+fromIp+" From Port:"+fromPort;
	}
}
