package net.thread.log;

public class ErrorLog {

	private final String error;
	private final StackTraceElement[] trace;
	
	public ErrorLog(String error, StackTraceElement[] trace)
	{
		this.error = error;
		this.trace = trace;
	}
	
	public ErrorLog(String error)
	{
		this (error, null);
	}
	
	public String getError()
	{
		return (this.error);
	}
	
	public StackTraceElement[] getTrace()
	{
		return (this.trace);
	}
	
	@Override
	public String toString()
	{
		int i = 0;
		
		StringBuilder builder = new StringBuilder();
		builder.append(this.error + System.lineSeparator());
		builder.append("Function stacktrace:" + System.lineSeparator());
		while (++i < this.trace.length)
			if (i < this.trace.length - 1)
				builder.append("\t at ").append(this.trace[i]).append(System.lineSeparator());
			else
				builder.append("\t at ").append(this.trace[i]);
		return (builder.toString());
	}
}
