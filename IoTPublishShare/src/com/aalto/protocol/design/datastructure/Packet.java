package com.aalto.protocol.design.datastructure;

import com.aalto.protocol.design.iotps.json.engine.JSON_Object;

public class Packet {

	private long seqNum = 0L;
	
	private boolean sent = false;
	
	private long timeStamp;
	
	private JSON_Object jsonObject = new JSON_Object();

	public long getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(long seqNum) {
		this.seqNum = seqNum;
	}

	public JSON_Object getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSON_Object jsonObject) {
		this.jsonObject = jsonObject;
	}
	
	
	@Override
    public boolean equals(Object obj) {
		Packet packet = (Packet) obj;
		return this.seqNum == packet.seqNum && this.sent == packet.sent;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	@Override
	public String toString() {
		
		return "SN "+seqNum+"  "+sent+ "";
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
