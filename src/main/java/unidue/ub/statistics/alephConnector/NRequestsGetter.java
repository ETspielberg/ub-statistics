package unidue.ub.statistics.alephConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import unidue.ub.statistics.admin.Notation;
import unidue.ub.statistics.admin.NotationDAO;
import unidue.ub.statistics.analysis.NRequests;

/**
 * Retrieves the number of requests from the Aleph database.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class NRequestsGetter {

	private PreparedStatement psGetNRequestsSingle;

	private PreparedStatement psGetNRequestsMulti;
	
	private PreparedStatement psGetNRequestsAll;
    
    private static final Logger LOGGER = Logger.getLogger(NRequestsGetter.class);

	/**
	 * uses a given connection to Aleph database to build an instance of the
	 * <code>NRequestsGetter</code>-object
	 * 
	 * @param connection
	 *            an <code>AlephConnection</code>-object
	  * @exception SQLException
	 *                exception connecting to the Aleph database
	 */
	public NRequestsGetter(AlephConnection connection) throws SQLException {
		String sql = "select z30reckey, z30_call_no, anzahl_vor, anzahl_ex, anzahl_aus, anz_auslstatus, quotient from edu50.z30, edu50.vortab_1a, edu50.vortab_2,edu50.vortab_3, edu50.vortab_4, edu50.vortab_5 where z37reckey = z30reckey and z30reckey = z36reckey and z37reckey = z30reckey2 and z37reckey = z30reckey3 and z30reckey = substr(z30_rec_key,1,9) and substr(z30_rec_key,10,6) = '000010' and anzahl_vor > 1 and z30_call_no like ?";
		psGetNRequestsSingle = connection.prepareStatement(sql);

		sql = "select z30reckey, z30_call_no, anzahl_vor, anzahl_ex, anzahl_aus, anz_auslstatus, quotient from edu50.z30, edu50.vortab_1a, edu50.vortab_2,edu50.vortab_3, edu50.vortab_4, edu50.vortab_5 where z37reckey = z30reckey and z30reckey = z36reckey and z37reckey = z30reckey2 and z37reckey = z30reckey3 and z30reckey = substr(z30_rec_key,1,9) and substr(z30_rec_key,10,6) = '000010' and anzahl_vor > 1 and z30_call_no between ? and ? order by z30_call_no";
		psGetNRequestsMulti = connection.prepareStatement(sql);
		
		sql = "select z30reckey, z30_call_no, anzahl_vor, anzahl_ex, anzahl_aus, anz_auslstatus, quotient from edu50.z30, edu50.vortab_1a, edu50.vortab_2,edu50.vortab_3, edu50.vortab_4, edu50.vortab_5 where z37reckey = z30reckey and z30reckey = z36reckey and z37reckey = z30reckey2 and z37reckey = z30reckey3 and z30reckey = substr(z30_rec_key,1,9) and substr(z30_rec_key,10,6) = '000010'".toUpperCase();
		psGetNRequestsAll = connection.prepareStatement(sql);
	}

	/**
	 * retrieves the number of Requests for the documents in the region defined
	 * by the notations
	 * 
	 * @param notations
	 *            a region of notations separated by '-'
	 * @return nRequests the list of <code>NRequest</code>
	 * @exception Exception
	 *                general exception
	 */
	public List<NRequests> getNRequestsForRange(String notations) throws Exception {
		ResultSet rs;
		psGetNRequestsMulti.setString(1, notations.substring(0, notations.indexOf("-")).trim() + "%");
		LOGGER.info("starting notation: " + notations.substring(0, notations.indexOf("-")).trim() + "%");
		psGetNRequestsMulti.setString(2, notations.substring(notations.indexOf("-") + 1).trim() + "%");
		LOGGER.info("ending notation: " + notations.substring(notations.indexOf("-") + 1).trim() + "%");
		rs = psGetNRequestsMulti.executeQuery();
		List<NRequests> nRequests = new ArrayList<>();
		while (rs.next()) {
			NRequests nRequest = new NRequests();
			nRequest.setDocNumber(rs.getString("z30reckey"));
			nRequest.setCallNo(rs.getString("z30_call_no"));
			nRequest.setNRequests(Integer.parseInt(rs.getString("anzahl_vor")));
			nRequest.setNItems(Integer.parseInt(rs.getString("anzahl_ex")));
			nRequest.setNLoans(Integer.parseInt(rs.getString("anzahl_aus")));
			nRequest.setNLendable(Integer.parseInt(rs.getString("anz_auslstatus")));
			nRequest.setRatio(Double.parseDouble(rs.getString("quotient")));
			nRequests.add(nRequest);
		}
		rs.close();
		return nRequests;
	}

	/**
	 * retrieves the number of Requests for the documents with the defined
	 * notations
	 * 
	 * @param notation
	 *            a single notation
	 * @return nRequest the list of <code>NRequest</code>
	 * @exception Exception
	 *                general exception
	 */
	public List<NRequests> getNRequestsForNotation(String notation) throws Exception {
		psGetNRequestsSingle.setString(1, notation + "%");
		ResultSet rs = psGetNRequestsSingle.executeQuery();
		List<NRequests> nRequests = new ArrayList<>();
		while (rs.next()) {
			NRequests nRequest = new NRequests();
			nRequest.setDocNumber(rs.getString("z30reckey"));
			nRequest.setCallNo(rs.getString("z30_call_no"));
			nRequest.setNRequests(Integer.parseInt(rs.getString("anzahl_vor")));
			nRequest.setNItems(Integer.parseInt(rs.getString("anzahl_ex")));
			nRequest.setNLoans(Integer.parseInt(rs.getString("anzahl_aus")));
			nRequest.setNLendable(Integer.parseInt(rs.getString("anz_auslstatus")));
			nRequest.setRatio(Double.parseDouble(rs.getString("quotient")));
			nRequests.add(nRequest);
		}
		rs.close();
		return nRequests;
	}

	/**
	 * retrieves the number of Requests for the documents with an arbitrary
	 * notations string
	 * 
	 * @param notations
	 *            a string holding notations and notations ranges, separated by
	 *            ','
	 * @return nRequest the list of <code>NRequest</code>
	 * @exception Exception
	 *                general exception
	 */
	public List<NRequests> getNRequests(String notations) throws Exception {
		List<NRequests> nRequests = new ArrayList<>();
		List<Notation> notationsList = NotationDAO.getNotationsList(notations);
		for (Notation notation : notationsList)
			nRequests.addAll(getNRequestsForNotation(notation.getNotation()));
		return nRequests;
	}
	
	/**
	 * retrieves all requests
	 * 
	 * @return the list of <code>NRequest</code>
	 * @exception Exception
	 *                general exception
	 */
	public List<NRequests> getAllNRequests() throws Exception {
        ResultSet rs = psGetNRequestsAll.executeQuery();
        List<NRequests> nRequests = new ArrayList<>();
        while (rs.next()) {
            try {
            NRequests nRequest = new NRequests();
            nRequest.setDocNumber(rs.getString("z30reckey").trim());
            nRequest.setCallNo(rs.getString("z30_call_no").trim());
            nRequest.setNRequests(Integer.parseInt(rs.getString("anzahl_vor").trim()));
            nRequest.setNItems(Integer.parseInt(rs.getString("anzahl_ex").trim()));
            nRequest.setNLoans(Integer.parseInt(rs.getString("anzahl_aus").trim()));
            nRequest.setNLendable(Integer.parseInt(rs.getString("anz_auslstatus").trim()));
            nRequest.setRatio(Double.parseDouble(rs.getString("quotient").trim()));
            nRequests.add(nRequest);
            } catch (Exception e) {
                LOGGER.info("problem occured with docNumber " +  rs.getString("z30reckey"));
            }
        }
        rs.close();
        return nRequests;
    }

}
