package jdo.wrapper;

import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.JDO;

/**
 * 
 * @author Jukino
 *
 */
public class MySQL extends JDO {
	
	/**
	 * Create a connection to a MySQL server
	 * 
	 * @param host
	 * @param database
	 * @param user
	 * @param password
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public MySQL(final String host, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		this(host, (short)3306, database, user, password);
	}
	
	/**
	 * Create a connection to a MySQL server
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public MySQL(final String host, final int port, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		this.connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database, user, password);
	}
	
}