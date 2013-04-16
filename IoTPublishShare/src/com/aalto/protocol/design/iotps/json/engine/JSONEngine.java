package com.aalto.protocol.design.iotps.json.engine;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JSONEngine {

	public static ArrayList<Pair> ValidateAndParse(String raw_string) throws Exception {
		String str = raw_string.trim();
		if (str.charAt(0) != '{' || str.charAt(str.length()-1) != '}') 
			throw new Exception("FAIL: Curly bracket missing from beginning or end");
		
		try {
			return ParseObject(str, '"');
		} catch (UnsupportedEncodingException ue) {
			try {
				return ParseObject(str, '\'');
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}

	
			private static ArrayList<Pair> ParseObject(String str, char strC) throws UnsupportedEncodingException, Exception{
				int beginning = 1; 
				int end = 1;
				int len = str.length();
				Pair pair;
				ArrayList<Pair> list = new ArrayList<Pair>();
				while(true){
					pair = ExtractNextKeyValuePair(str, beginning, strC);
					list.add(pair);
					end = pair.index;
					
					beginning = NextNonWhitespace(str, end+1);
					if (beginning >= len)
						break;
				}
				return list;
			}

			
			// Return the index of the last character of Value.
			private static Pair ExtractNextKeyValuePair(String str, int i, char strC) throws Exception {
				Pair pair = new Pair();
				int j = NextNonWhitespace (str, i);
				if (j > 0) {
					
					// Inspect Key: it should always be a string
					if (str.charAt(j) != strC) throw new UnsupportedEncodingException ("FAIL: Key is not a string.");
					pair.Key = JSON_Helpers.ExtractString(str.substring(j), strC);
					j += pair.Key.length()+2;
					
					// Inspect intermediary character ':'
					j = NextNonWhitespace(str, j);
					if (str.charAt(j) != ':') throw new Exception ("FAIL: No colon between Key and Value");
					j++;
					
					// Inspect Value
					j = NextNonWhitespace(str, j);
					if (str.charAt(j) == strC) {
						pair.Value = JSON_Helpers.ExtractString(str.substring(j), strC);
						pair.type = pair.type.STRING;
						j += 2;
					}
					else if (str.charAt(j) == '[') {
						pair.Value = JSON_Helpers.ExtractArray(str.substring(j), strC);
						pair.type = pair.type.ARRAY;
					}
					else if (str.charAt(j) == '{') {
						pair.Value = JSON_Helpers.ExtractObject(str.substring(j), strC);
						pair.type = pair.type.OBJECT;
					}
					else if (Character.isDigit(str.charAt(j)) || str.charAt(j) == '-' || str.charAt(j) == '+') {
						pair.Value = JSON_Helpers.ExtractNumber(str.substring(j));
						pair.type = pair.type.NUMBER;
					}
					else if (Character.toLowerCase(str.charAt(j)) == 'n' || Character.toLowerCase(str.charAt(j)) == 't' || Character.toLowerCase(str.charAt(j)) == 'f') {
						pair.Value = JSON_Helpers.ExtractTrueFalseNull(str.substring(j));
						pair.type = pair.type.TFN;
					}
					
					if (pair.Value == null) throw new Exception ("FAIL: Unknown value type.");
					j += pair.Value.length();
					j = NextNonWhitespace (str, j);
					pair.index = j;
					return pair;
				}
				
				return null;
			}
			
			
			public static String[] ParseStringToArray(String array) throws Exception {
				String str = array.trim();
				int len = str.length();
				if (str.charAt(0) != '[' || str.charAt(len-1) != ']')
					throw new Exception ("FAIL: string is not an array, cannot parse");
				if (str.contains(","))
					return str.substring(1, len-1).split(",");
				else {
					String[] ret = { str.substring(1, len-1) };
					return ret;
				}
			}
			
			
			public static int NextNonWhitespace (String str, int i) {
				int len = str.length();
				int j = i;
				while (j < len && Character.isWhitespace(str.charAt(j)) == true)
					j++;
				if (j > len) return -1;
				return j;
			}

}
