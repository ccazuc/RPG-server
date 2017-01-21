package net.utils;

public class StringUtils {

	public static boolean isInteger(String str) {
		if(str.length() == 0) {
			return false;
		}
		int i = -1;
		while(++i < str.length()) {
			char c = str.charAt(i);
			if(c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
	
	public static String toUpperCase(String str) {
		if(str.length() == 0) {
			return str;
		}
		final char[] table = new char[str.length()];
		int i = -1;
		char c;
		while(++i < str.length()) {
			c = str.charAt(i);
			if(c >= 'a' && c <= 'z') {
				c-= 32;
			}
			table[i] = c;
		}
		return new String(table);
	}
	
	public static String toLowerCase(String str) {
		if(str.length() == 0) {
			return str;
		}
		final char[] table = new char[str.length()];
		int i = -1;
		char c;
		while(++i < str.length()) {
			c = str.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				c+= 32;
			}
			table[i] = c;
		}
		return new String(table);
	}
	
	public static boolean isInteger(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static String removeSpacesDuplicate(String str) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while(i < str.length()) {
			if(i < str.length()-1 && str.charAt(i) == ' ' && str.charAt(i+1) == ' ') {
				i++;
				continue;
			}
			if(i == str.length() && str.charAt(i) == ' ') {
				break;
			}
			builder.append(str.charAt(i));
			i++;
		}
		return builder.toString();
	}
}
