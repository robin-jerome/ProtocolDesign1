package com.aalto.protocol.design.iotps.objects;

public class IoTPSPendingAcksObject extends IoTPSObject {

	private double sub_seq_no;
	private double seq_no;
	
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
}
