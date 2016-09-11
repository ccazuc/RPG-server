package jdo.wrapper;

import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.JDO;

/**
 * 
 * @author Jukino
 *
 */
public class MariaDB extends JDO {
	
	/**
	 * Create a connection to a MariaDB server
	 * 
	 * @param host - The host name (domain or IP)
	 * @param database - The mariaDB database
	 * @param user - The account username
	 * @param password - The account password
	 * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.ExceptionInInitializerError - if the initialization provoked by this method fails.
	 * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
	 * @throws ClassNotFoundException if the class cannot be located
	 * @throws SQLException if the class cannot be located
	 */
	public MariaDB(final String host, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		this(host, (short)3306, database, user, password);
	}
	
	/**
	 * Create a connection to a MariaDB server
	 * 
	 * @param host - The host name (domain or IP)
	 * @param port - The host port
	 * @param database - The mariaDB database
	 * @param user - The account username
	 * @param password - The account password
	 * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.ExceptionInInitializerError - if the initialization provoked by this method fails.
	 * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
	 * @throws ClassNotFoundException if the class cannot be located
	 * @throws SQLException if the class cannot be located
	 */
	public MariaDB(final String host, final int port, final String database, final String user, final String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	Class.forName("org.mariadb.jdbc.Driver").newInstance(); 
		this.connection = DriverManager.getConnection("jdbc:mariadb://"+host+":"+port+"/"+database+"?useConfigs=maxPerformance", user, password);
	}
	
}