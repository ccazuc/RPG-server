package jdo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * @author Jukino
 *
 */
public class JDO {
	
	protected Connection connection;
	
	/**
	 * Get the JDO <code>Connection</code> Object
	 * 
	 * @return - The JDO <code>Connection</code> Object
	 * @throws SQLException if a database access error occurs
	 */
	public final Connection getConnection() throws SQLException {
		if(!this.connection.isClosed()) {
			return this.connection;
		}
		throw new SQLException("Connection is closed");
	}
	
	/**
	 * Close the SQL connection
	 * 
	 * @throws SQLException if a database access error occurs
	 */
	public final void close() throws SQLException {
		this.connection.close();
	}
	
	/**
	 * Create a new <code>JDOStatement</code>
	 * 
	 * @param request - an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @return The created JDO Statement
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	public final JDOStatement prepare(final String request) throws SQLException {
		synchronized(this.connection) {
			return new JDOStatement(request, this.connection);
		}
	}
	
}
