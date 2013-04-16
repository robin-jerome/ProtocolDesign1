package com.aalto.protocol.design.iotps.json.engine;

public class Pair {
	public String Key;
	public String Value;
	public int index;
	public enum data_type { STRING, ARRAY, OBJECT, NUMBER, TFN };
	public data_type type;
	
	public Pair() { 
		this.Key = null;
		this.Value = null;
		this.index = -1;
		this.type = null;
	}
	
	public Pair(String key, String value) {
		this.Key = key;
		this.Value = value;
	}
}