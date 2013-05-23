package com.aalto.protocol.design.datastructure;

import com.aalto.protocol.design.iotps.json.engine.JSON_Object;

public class Packet {

	private double seqNum = 0;
	
	private boolean sent = false;
	
	private JSON_Object jsonObject = new JSON_Object();

	public double getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(double seqNum) {
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
		return this.seqNum == packet.seqNum ;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
}
