package net.thread.sql;

import net.connection.Connection;

public class Who {

	private String word;
	private Connection connection;
	
	public Who(String word, Connection connection) {
		this.word = word;
		this.connection = connection;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
}
