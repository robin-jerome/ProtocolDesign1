package com.aalto.protocol.design.iotps.json.engine;

import java.util.ArrayList;

public class JSON_Object {
	// A class to contain and parse JSON strings into structured objects and vice versa.
	
	private String raw_string;
	private boolean IsValid;
	private ArrayList<Pair> Items = new ArrayList<Pair>();
	
	
	// ==============
	// Constructors
	// ==============
	
	public JSON_Object() {}
	
	public JSON_Object(String raw_string) {
		this.InsertJSONString(raw_string);
	}
	
	// ==============
	//     Input
	// ==============
	
	public void InsertJSONString (String raw_string) {
		this.raw_string = raw_string;
		this.IsValid = true;
		try {
			this.Items = JSONEngine.ValidateAndParse(this.raw_string);
		} catch (Exception e) {
			//System.out.println("Parsing was unsuccessfull: " + e.getMessage());
			this.IsValid = false;
		}
	}
	
	public void AddItem(String key, String value) {
		this.Items.add(new Pair(key, value));
	}
	
	// ===============
	//     Output
	// ===============
	
	public String toJSONString() {
		String s = "{";
		char strC = '\'';
		for (Pair pair : this.Items) {
			s += strC + pair.Key.trim() + strC + ":";
			if (pair.type == Pair.data_type.STRING)
				s += strC + pair.Value.trim() + strC + ",";
			else if (pair.type == Pair.data_type.OBJECT) {
				JSON_Object o = new JSON_Object(pair.Value.trim());
				s += o.toString() + ",";
			}
			else if (pair.type == Pair.data_type.ARRAY) {
				String[] array;
				try {
					array = JSONEngine.ParseStringToArray(pair.Value);
				} catch (Exception e) {
					break;
				}
				s += "[";
				int len = array.length;
				for (int i = 0; i < len; i++) {
					s += array[i].trim() + ",";
				}
				s = s.substring(0, s.length() - 1);
				s += "],";
			}
			else
				s += pair.Value.trim() + ",";
		}
		
		s = s.substring(0, s.length() - 1);
		s += "}";
		return s;
	}
	
	
	public int Count() {
		return this.Items.size();
	}
	
	
	// ===============
	//     Getters
	// ===============
	
	public String GetValue(String key) {
		for (Pair pair : this.Items) {
			if (pair.Key.trim().equals(key)) {
				if (pair.type == Pair.data_type.OBJECT) {
					JSON_Object o = new JSON_Object(pair.Value);
					return o.toString();
				}
				return pair.Value.trim();
			}
		}
		
		// Didn't find it here --> next level
		JSON_Object o;
		for (Pair pair : this.Items) {
			if (pair.type == Pair.data_type.OBJECT) {
				o = new JSON_Object(pair.Value);
				
				String value = o.GetValue(key);
				if (value == null) continue;
				
				return value.trim();
			}
		}
		return null;
	}
	
	
	public boolean GetBooleanValue(String key) throws Exception {
		return Boolean.parseBoolean(this.GetValue(key));
	}
	
	public long GetNumberValue (String key) throws Exception {
		return Long.parseLong(this.GetValue(key));
	}
	
	public String[] GetArrayValue(String key) throws Exception {
		return JSONEngine.ParseStringToArray(this.GetValue(key));
	}
}