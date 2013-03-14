package com.aalto.protocol.design.iotps.objects;

public class IoTPSAckObject {

	private int sub_seq_no;
	private int seq_no;
	
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
}
