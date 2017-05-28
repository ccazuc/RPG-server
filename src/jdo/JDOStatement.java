package jdo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

/**
 * 
 * @author Jukino
 *
 */
public class JDOStatement {
	
	private PreparedStatement statement;
	private ResultSet result;
	private int index;
	
	/**
	 * Create new JDO Statement
	 * 
	 * @param request an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @param connection a JDO object
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	protected JDOStatement(final String request, final Connection connection) throws SQLException {
		this.statement = connection.prepareStatement(request);
	}
	
	public final String GetStatement() {
		return this.statement.toString();
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>boolean</code> value.
     * The driver converts this to an SQL <code>TINYINT</code> value when it sends it to the database.
     *
	 * @param b the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putBoolean(final boolean b) throws SQLException {
		this.statement.setByte(++this.index, (byte)(b?1:0));
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>byte</code> value.
     * The driver converts this to an SQL <code>TINYINT</code> value when it sends it to the database.
     *
	 * @param b the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putByte(final byte b) throws SQLException {
		this.statement.setByte(++this.index, b);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>short</code> value.
     * The driver converts this to an SQL <code>SMALLINT</code> value when it sends it to the database.
     *
	 * @param s the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putShort(final short s) throws SQLException {
		this.statement.setShort(++this.index, s);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>int</code> value.
     * The driver converts this to an SQL <code>INTEGER</code> value when it sends it to the database.
     *
	 * @param i the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putInt(final int i) throws SQLException {
		this.statement.setInt(++this.index, i);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>long</code> value.
     * The driver converts this to an SQL <code>BIGINT</code> value when it sends it to the database.
     * 
	 * @param l the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putLong(final long l) throws SQLException {
		this.statement.setLong(++this.index, l);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>float</code> value.
     * The driver converts this  to an SQL <code>REAL</code> value when it sends it to the database.
     * 
	 * @param f the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putFloat(float f) throws SQLException {
		this.statement.setFloat(++this.index, f);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>double</code> value.
     * The driver converts this to an SQL <code>DOUBLE</code> value when it sends it to the database.
     * 
	 * @param d the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putDouble(final double d) throws SQLException {
		this.statement.setDouble(++this.index, d);
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>char</code> value.
     * The driver converts this to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value (depending on the argument's size relative to the driver's limits on <code>VARCHAR</code> values) when it sends it to the database.
     * 
	 * @param c the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putChar(final char c) throws SQLException {
		this.statement.setString(++this.index, Character.toString(c));
	}
	
	/**
	 * Sets the designated parameter to the given Java <code>String</code> value.
     * The driver converts this to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value (depending on the argument's size relative to the driver's limits on <code>VARCHAR</code> values) when it sends it to the database.
     * 
	 * @param s the parameter value
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final void putString(final String s) throws SQLException {
		this.statement.setString(++this.index, s);
	}
	
	/**
	 * Executes the SQL query in this <code>JDOStatement</code> object.
	 * 
	 * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or the SQL statement does not return a ResultSet object
	 * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
	 */
	public final void execute() throws SQLException, SQLTimeoutException {
		synchronized(this.statement.getConnection()) {
			if(this.result != null) {
				this.result.close();
			}
			this.result = this.statement.executeQuery();
		}
	}
	
	/**
     * Executes the SQL statement in this <code>JDOStatement</code> object, which must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code>; or an SQL statement that returns nothing, such as a DDL statement.
     *
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @exception SQLException if a database access error occurs; this method is called on a closed  <code>PreparedStatement</code> or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was specified by the {@code setQueryTimeout} method has been exceeded and has at least attempted to cancel the currently running {@code Statement}
     */
	public final void executeUpdate() throws SQLException, SQLTimeoutException {
		synchronized(this.statement.getConnection()) {
			this.statement.executeUpdate();
		}
	}
	
	/**
     * Adds a set of parameters to this <code>JDOStatement</code> object's batch of commands.
     *
     * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
     */
	public final void batch() throws SQLException {
		synchronized(this.statement.getConnection()) {
			this.statement.addBatch();
		}
	}
	
    /**
     * Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts.
     * The <code>int</code> elements of the array that is returned are ordered to correspond to the commands in the batch, which are ordered according to the order in which they were added to the batch.
     * 
     * @throws SQLException if a database access error occurs, this method is called on a closed <code>Statement</code> or the driver does not support batch statements.
     */
	public final void executeBatch() throws SQLException {
		synchronized(this.statement.getConnection()) {
			this.statement.executeBatch();
		}
	}
	
	/**
	 * Clears the current parameter values immediately.
	 * In general, parameter values remain in force for repeated use of a statement. Setting a parameter value automatically clears its previous value. However, in some cases it is useful to immediately release the resources used by the current parameter values; this can be done by calling the method clearParameters.
	 */
	public final void clear() {
		this.index = 0;
	}
	
	/**
	 * Return if there is a next result
	 * @return
	 * @throws SQLException
	 */
	public final boolean fetch() throws SQLException {
		this.index = 0;
		return this.result.next();
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>boolean</code> in the Java programming language.
     * 
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final boolean getBoolean() throws SQLException {
		return this.result.getByte(++this.index) == 1;
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>byte</code> in the Java programming language.
     * 
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final byte getByte() throws SQLException {
		return this.result.getByte(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>short</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final short getShort() throws SQLException {
		return this.result.getShort(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as an <code>int</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final int getInt() throws SQLException {
		return this.result.getInt(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>long</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final long getLong() throws SQLException {
		return this.result.getLong(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>float</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final float getFloat() throws SQLException {
		return this.result.getFloat(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>double</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final double getDouble() throws SQLException {
		return this.result.getDouble(++this.index);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>char</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final char getChar() throws SQLException {
		return this.result.getString(++this.index).charAt(0);
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this <code>JDOStatement</code> object as a <code>String</code> in the Java programming language.
     *
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>0</code>
	 * @throws SQLException if a database access error occurs or this method is called on a closed <code>JDOStatement</code>
	 */
	public final String getString() throws SQLException {
		return this.result.getString(++this.index);
	}
	
	/**
	 * Close the statement
	 * 
	 * @throws SQLException
	 */
	public final void close() throws SQLException {
		this.statement.close();
		this.result.close();
	}
	
}
