package net.utils;

public class StringUtils {

	public static String formatPlayerName(String str) {
		if(str == null || str.length() == 0)
			return str;
		final char[] table = new char[str.length()];
		int i = table.length;
		table[0] = toUpperCase(str.charAt(0));
		char tmp;
		while(--i >= 1) {
			tmp = str.charAt(i);
			if(tmp >= 'A' && tmp <= 'Z')
				tmp += 32;
			table[i] = tmp;
		}
		return new String(table);
	}
	
	public static boolean checkPlayerNameLength(String str) {
		return str.length() >= 3 && str.length() <= 10;
	}
	
	public static boolean isInteger(String str) {
		if(str == null || str.length() == 0)
			return false;
		int i = -1;
		char c;
		while(++i < str.length()) {
			c = str.charAt(i);
			if(c < '0' || c > '9')
				return false;
		}
		return true;
	}
	
	public static boolean containsOnlySpace(String str) {
		if(str.length() == 0)
			return false;
		int i = str.length();
		while(--i >= 0) {
			if(str.charAt(i) != ' ')
				return true;
		}
		return false;
 	}
	
	public static char toUpperCase(char c) {
		return c >= 'a' && c <= 'z' ? (char)(c-32) : c;
	}
	
	public static char toLowerCase(char c) {
		return c >= 'A' && c <= 'Z' ? (char)(c+32) : c;
	}
	
	public static String toUpperCase(String str) {
		int i = str.length();
		char c;
		scan : {
			while(--i >= 0) {
				c = str.charAt(i);
				if(c != toUpperCase(c)) {
					break scan;
				}
			}
			return str;
		}
		final char[] table = new char[str.length()];
		i = table.length;
		while(--i >= 0) {
			c = str.charAt(i);
			if(c >= 'a' && c <= 'z') {
				c-= 32;
			}
			table[i] = c;
		}
		return new String(table);
	}
	
	public static String toLowerCase(String str) {
		int i = str.length();
		char c;
		scan : {
			while(--i >= 0) {
				c = str.charAt(i);
				if(c != toLowerCase(c)) {
					break scan;
				}
			}
			return str;
		}
		final char[] table = new char[str.length()];
		i = table.length;
		while(--i >= 0) {
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