package jdo.wrapper;

import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.JDO;

/**
 * 
 * @author Jukino
 *
 */
public class PostgreSQL extends JDO {
	
	/**
	 * Create a connection to a PostgreSQL server
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
	public PostgreSQL(final String host, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		this(host, (short)5432, database, user, password);
	}
	
	/**
	 * Create a connection to a PostgreSQL server
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
	public PostgreSQL(final String host, int port, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	Class.forName("org.postgresql.Driver").newInstance(); 
		this.connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+database, user, password);
	}
	
}
