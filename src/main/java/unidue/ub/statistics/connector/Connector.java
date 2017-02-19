package unidue.ub.statistics.connector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Connector {
	
	public PreparedStatement prepareStatement(String sql) throws SQLException;
	
	public void disconnect();

}
