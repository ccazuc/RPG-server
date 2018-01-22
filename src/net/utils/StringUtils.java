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
	
	public static boolean isInteger(char c)
	{
		return (c >= '0' && c <= '9');
	}
	
	public static String removeSpacesDuplicate(String str)
	{
		StringBuilder builder = new StringBuilder();
		int i = -1;
		while (++i < str.length())
		{
			if (i < str.length() - 1 && str.charAt(i) == ' ' && str.charAt(i + 1) == ' ')
			{
				i++;
				continue;
			}
			if (i == str.length() && str.charAt(i) == ' ')
				break;
			builder.append(str.charAt(i));
		}
		return (builder.toString());
	}
	
	public static String generateRandomString(int length)
	{
		char[] result = new char[length];
		int i = -1;
		while (++i < length)
			result[i] = (char)(33 + Math.random() * 94);
		return (new String(result));
	}
	
	public static String convertTimeToStringSimple(long delta)
	{
		String result = null;
		if (delta >= Timer.MS_IN_A_YEAR)
		{
			result = (delta / Timer.MS_IN_A_YEAR) + " year";
			if (delta / Timer.MS_IN_A_YEAR > 1)
				result += "s";
		}
		else if (delta >= Timer.MS_IN_A_MONTH)
		{
			result = (delta / Timer.MS_IN_A_MONTH) + " month";
			if (delta / Timer.MS_IN_A_MONTH > 1)
				result += "s";
		}
		else if (delta >= Timer.MS_IN_A_WEEK)
		{
			result = (delta / Timer.MS_IN_A_WEEK) + " week";
			if (delta / Timer.MS_IN_A_WEEK > 1)
				result += "s";
		}
		else if (delta >= Timer.MS_IN_A_DAY)
		{
			result = (delta / Timer.MS_IN_A_DAY) + " day";
			if (delta / Timer.MS_IN_A_DAY > 1)
				result += "s";
		}
		else if (delta >= Timer.MS_IN_AN_HOUR)
		{
			result = (delta / Timer.MS_IN_AN_HOUR) + " hour";
			if (delta / Timer.MS_IN_AN_HOUR > 1)
				result += "s";
		}
		else if (delta >= Timer.MS_IN_A_MINUTE)
		{
			result = (delta / Timer.MS_IN_A_MINUTE)+" minute";
			if (delta / Timer.MS_IN_A_MINUTE > 1)
				result += "s";
		}
		else
			result = "Less than a minute ago";
		return (result);
	}
	
	public static String convertTimeToString(long delta) {
		StringBuilder result = new StringBuilder();
		boolean written = false;
		if (delta >= Timer.MS_IN_A_YEAR)
		{
			result.append(delta / Timer.MS_IN_A_YEAR).append(" year");
			if(delta / Timer.MS_IN_A_YEAR > 1)
				result.append('s');
			delta %= Timer.MS_IN_A_YEAR;
			written = true;
		}
		if (delta >= Timer.MS_IN_A_MONTH)
		{
			if (written)
				result.append(", ");
			result.append(delta / Timer.MS_IN_A_MONTH).append(" month");
			if(delta / Timer.MS_IN_A_MONTH > 1)
				result.append('s');
			delta %= Timer.MS_IN_A_MONTH;
			written = true;
		}
		if (delta >= Timer.MS_IN_A_WEEK)
		{
			if (written)
				result.append(", ");
			result.append(delta / Timer.MS_IN_A_WEEK).append(" week");
			if(delta / Timer.MS_IN_A_WEEK > 1)
				result.append('s');
			delta %= Timer.MS_IN_A_WEEK;
			written = true;
		}
		if (delta >= Timer.MS_IN_A_DAY)
		{
			if (written)
				result.append(", ");
			result.append(delta / Timer.MS_IN_A_DAY).append(" day");
			if(delta / Timer.MS_IN_A_DAY > 1)
				result.append('s');
			delta %= Timer.MS_IN_A_DAY;
			written = true;
		}
		if (delta >= Timer.MS_IN_AN_HOUR)
		{
			if (written)
				result.append(", ");
			result.append(delta / Timer.MS_IN_AN_HOUR).append(" hour");
			if(delta / Timer.MS_IN_AN_HOUR > 1)
				result.append('s');
			delta %= Timer.MS_IN_AN_HOUR;
			written = true;
		}
		if (delta >= Timer.MS_IN_A_MINUTE)
		{
			if (written)
				result.append(", ");
			result.append(delta / Timer.MS_IN_A_MINUTE).append(" minute");
			if(delta / Timer.MS_IN_A_MINUTE > 1)
				result.append('s');
			delta %= Timer.MS_IN_A_MINUTE;
			written = true;
		}
		if (delta >= 1000)
		{
			if (written)
				result.append(", ");
			result.append(delta / 1000).append(" second");
			if (delta / 1000> 1)
				result.append('s');
		}
		return (result.toString());
	}
}
