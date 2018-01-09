package net.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hash {
	
	private static MessageDigest digest;
	
	public static final String hash(String input, String salt) {
		input = salt+input;
		if(digest == null) {
			try {
				digest = MessageDigest.getInstance("SHA1");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		final byte[] result = digest.digest(input.getBytes());
		final StringBuilder sb = new StringBuilder();
		int i = -1;
		while(++i < result.length) {
			sb.append(Integer.toString((result[i]&0xff)+0x100, 16).substring(1));
		}
		return sb.toString();
	}
	
	public static final String generateSalt(int salt_size) {
		int i = 0;
		final StringBuilder builder = new StringBuilder();
		while(i < salt_size) {
			builder.append((char)(33+(int)(Math.random()*(94))));
			i++;
		}
		return builder.toString();
	}
	
	public static void generateDBPassword(String password)
	{
		String salt = generateSalt(9);
		System.out.println("Password: [" + hash(password, salt)+"], salt: [" + salt + "]");
	}
}
