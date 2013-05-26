package com.aalto.protocol.design.iotps.objects;

class IoTPSPendingAcksObject extends IoTPSObject {

	private long sub_seq_no;
	private long seq_no;
	
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
}
