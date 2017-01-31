package unidue.ub.statistics.alephConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.config.MCRConfigurationException;

/**
 * Establishes and closes the connection to the Aleph database and registers the
 * necessary drivers.
 * 
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class AlephConnection {

	private final static Logger LOGGER = Logger.getLogger(AlephConnection.class);

	private final static String userID;

	private final static String passwd;

	private final static String dburi;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		String prefix = "aleph.oracle.";
		userID = config.getString(prefix + "userID");
		passwd = config.getString(prefix + "passwd");
		dburi = config.getString(prefix + "databaseURI");

		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (Exception ex) {
			String msg = "Error while registering Oracle JDBC driver";
			throw new MCRConfigurationException(msg, ex);
		}
	}

	private Connection connection;

	/**
	 * builds a connection to the Aleph database
	 * @exception SQLException exception querying the Aleph database 
	 * 
	 */
	public AlephConnection() throws SQLException {
		LOGGER.info("Connecting to Aleph...");
		connection = DriverManager.getConnection(dburi, userID, passwd);
	}

	/**
	 * builds a <code>PreparedStatement</code> for the Aleph connection
	 * 
	 * @param sql
	 *            the sql statement to be executed
	 * @return PreparedStatement <code>PreparedStatement</code> for the Aleph connection
	 * @exception SQLException exception querying the Aleph database 
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	/**
	 * closes the connection to the Aleph database
	 * 
	 */
	public void disconnect() {
		LOGGER.info("Closing connection to Aleph.");

		if (connection == null)
			return;

		try {
			connection.close();
			connection = null;
		} catch (Exception ignored) {
		}
	}
}
