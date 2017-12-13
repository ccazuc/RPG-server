package net.utils;

public class DebugUtils {

	public static void printStackTrace(String msg)
	{
		StackTraceElement[] trace = new Throwable().getStackTrace();
		int i = 0;
		System.out.println("---------------------------------------------------");
		System.out.println("StackTrace for "+msg+" :");
		while (++i < trace.length)
			System.out.println("\t at "+trace[i]);
		System.out.println("---------------------------------------------------");
	}
}
