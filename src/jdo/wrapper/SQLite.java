package jdo.wrapper;

import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.JDO;

/**
 * 
 * @author Jukino
 *
 */
public class SQLite extends JDO {
	
	/**
	 * Create SQLite connection to file
	 * 
	 * @param file
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public SQLite(final String file) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	Class.forName("org.sqlite.JDBC").newInstance();
		this.connection = DriverManager.getConnection("jdbc:sqlite:"+file);
	}
	
}