package com.aalto.protocol.design.iotps.json.engine;

public class JSON_Helpers {

	// Is the char escaped or not?
		// Examples (is 'o' escaped?):
		// 		"o"  	-- false
		//		"\o" 	-- true
		//		"\\o"	-- false ('\' is escaped and thus does not escape 'o')
		//		"\\\o"	-- true
	public static boolean IsEscaped(String str, int index) {
//		if (index > 0 && index < str.length() && str.charAt(index-1) == '\\') {
//			if (IsEscaped(str, index-1) == false) {
//				return true;
//			}
//		}
		return false;
	}
	
	
	// Return the index of next non-escaped occurrence of c in str after index.
	// If not found, return -1.
	public static int IndexOfNextOccurrence(String str, char c, int index) {
		int i = index + 1;
		int len = str.length();
		if (i >= len-1) return -1;
		
		char temp;
		while (i < len) {
			temp = str.charAt(i);
			if (temp == c && !IsEscaped(str, i)) return i;
			i++;
		}
		
		return -1;
	}
	
	
	public static String ExtractString(String str, char strC) throws Exception {
		int len = str.length();
		for (int i = 1; i < len; i++) {
			if (str.charAt(i) == strC && !IsEscaped(str, i)) {
				return str.substring(1, i);
			}
		}
		throw new Exception ("FAIL: String does not end properly");
	}
	
	
	public static String ExtractNumber(String str) {
		int len = str.length();
		
		for (int i = 0; i < len; i++) {
			if (i == 0 && (str.charAt(0) == '-' || str.charAt(0) == '+'))
				continue;
			if (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')
				continue;
			return str.substring(0, i);
		}
		return str.substring(0, len);
	}
	
	
	public static String ExtractObject(String str, char strC) throws Exception {
		int len = str.length();
		int level = 1;
		boolean WithinString = false;
		
		for (int i = 1; i < len; i++) {
			if (str.charAt(i) == strC && !IsEscaped(str, i)) {
				WithinString ^= WithinString; // Toggle boolean (XOR-operation)
			}
			if (WithinString == false && str.charAt(i) == '}')
				level--;
			else if (WithinString == false && str.charAt(i) == '{')
				level++;
			if (level == 0)
				return str.substring(0, i+1);
		}
		throw new Exception ("FAIL: Object was not ended properly.");
	}
	
	
	public static String ExtractArray(String str, char strC) throws Exception {
		int len = str.length();
		int level = 1;
		boolean WithinString = false;
		
		for (int i = 1; i < len; i++) {
			if (str.charAt(i) == strC && !IsEscaped(str, i)) {
				WithinString ^= WithinString; // Toggle boolean (XOR-operation)
			}
			if (WithinString == false && str.charAt(i) == ']')
				level--;
			else if (WithinString == false && str.charAt(i) == '[')
				level++;
			if (level == 0)
				return str.substring(0, i+1);
		}
		throw new Exception ("FAIL: Array was not ended properly.");
	}
	
	
	public static String ExtractTrueFalseNull(String substring) throws Exception {
		if (substring.startsWith("true"))
			return "true";
		else if (substring.startsWith("false"))
			return "false";
		else if (substring.startsWith("null"))
			return "null";
		
		throw new Exception ("FAIL: Not true, false nor null --> unknown value");
	}	
}
