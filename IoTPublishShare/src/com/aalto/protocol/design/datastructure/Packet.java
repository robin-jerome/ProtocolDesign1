package com.aalto.protocol.design.datastructure;

import org.json.simple.JSONObject;

public class Packet {

	private int seqNum = 0;
	
	private JSONObject jsonObject = new JSONObject();

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	
	@Override
    public boolean equals(Object obj) {
		Packet packet = (Packet) obj;
		return this.seqNum == packet.seqNum ;
	}
}
