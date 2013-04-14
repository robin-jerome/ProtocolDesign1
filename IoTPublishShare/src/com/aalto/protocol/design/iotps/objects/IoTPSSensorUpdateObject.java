package com.aalto.protocol.design.iotps.objects;


public class IoTPSSensorUpdateObject {
	private String devId;
	private int devNumber;
	private int seqNo;
	private String data;
	private Double timeStamp;
	private int dataSize;

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public int getDevNumber() {
		return devNumber;
	}

	public void setDevNumber(int devNumber) {
		this.devNumber = devNumber;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Double getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Double timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	public void print(){
		System.out.printf("devId : %s \n",this.devId );
		System.out.printf("devNumber : %d \n",this.devNumber );
		System.out.printf("data : %s \n",this.data );
		System.out.printf("dataSize : %d \n",this.dataSize );
		System.out.printf("seqNo : %d \n",this.seqNo );
		System.out.printf("timeStamp : %e \n",this.timeStamp );
	}
}
