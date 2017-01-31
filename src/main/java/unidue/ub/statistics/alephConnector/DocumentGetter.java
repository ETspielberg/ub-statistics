package unidue.ub.statistics.alephConnector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import unidue.ub.statistics.media.monographs.Manifestation;

/**
 * Retrieves the documents from the Aleph database.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class DocumentGetter {

	private static final Logger LOGGER = Logger.getLogger(DocumentGetter.class);

	private PreparedStatement psGetDocumentsByShelfmarkExact;

	private PreparedStatement psGetDocumentsByShelfmarkExpanded;

	private PreparedStatement psGetDocumentsByNotation;

	private PreparedStatement psGetDocumentsByEtat;

	private final static String SUB_D = "_d";

	private Set<Manifestation> documents = new HashSet<Manifestation>();

	/**
	 * uses a given connection to Aleph database to build an instance of the
	 * <code>DocumentGetter</code>-object
	 * 
	 * @param connection
	 *            an <code>AlephConnection</code>-object
	 * @exception SQLException exception querying the Aleph database 
	 */

	public DocumentGetter(AlephConnection connection) throws SQLException {
		String select = "select distinct substr(z30_rec_key,1,9) as docNumber from edu50.z30 where ( z30_call_no = ?) ";
		String like = "or ( z30_call_no like ?) ";
		String orderBy = "order by docNumber";

		String selectNotation = "select distinct substr(z30_rec_key,1,9) as docNumber from edu50.z30 where ( z30_call_no like ?)";
		String sql = "select distinct substr(z75_rec_key,1,9) as docNumber from edu50.z601, edu50.z75 where z601_rec_key_2 = z75_rec_key_2 and z601_rec_key like ? and z601_type = 'INV'";

		psGetDocumentsByShelfmarkExact = connection.prepareStatement(select + like + orderBy);
		psGetDocumentsByShelfmarkExpanded = connection.prepareStatement(select + like + like + like + orderBy);
		psGetDocumentsByNotation = connection.prepareStatement(selectNotation + orderBy);
		psGetDocumentsByEtat = connection.prepareStatement(sql + orderBy);
	}

	/**
	 * retrieves the documents connected to a shelfmark
	 * 
	 * @param shelfmark
	 *            the shelfmark of the documents retrieved from the Aleph
	 *            database
	 * @param exact
	 *            boolean indicating whether other editions are to be retrieved
	 *            as well
	 * @return documents set of documents
	 * @exception SQLException exception querying the Aleph database 
	 */
	public Set<Manifestation> getDocumentsByShelfmark(String shelfmark, Boolean exact) throws SQLException {
		String suffix = "";
		if (shelfmark.endsWith(SUB_D)) {
			shelfmark = shelfmark.substring(0, shelfmark.length() - SUB_D.length());
			suffix = SUB_D;
		}
		shelfmark = shelfmark.toUpperCase();

		PreparedStatement ps = exact ? psGetDocumentsByShelfmarkExact : psGetDocumentsByShelfmarkExpanded;
		ps.setString(1, shelfmark + suffix);
		ps.setString(2, shelfmark + "+%" + suffix);
		if (!exact) {
			ps.setString(3, shelfmark + "-%" + suffix);
			ps.setString(4, shelfmark + "(%" + suffix);
		}
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			String docNumber = rs.getString(1);
			Manifestation doc = new Manifestation(docNumber);
			if (!documents.contains(doc)) {
				LOGGER.info("Found document " + docNumber + " with shelfmark " + shelfmark);
				documents.add(doc);
			}
		}
		rs.close();
		return documents;
	}

	/**
	 * retrieves the documents connected to a budget code
	 * 
	 * @param etat
	 *            the budget code of the documents retrieved from the Aleph
	 *            database
	 * @return documents set of documents
	 * @exception SQLException exception querying the Aleph database 
	 */
	public Set<Manifestation> getDocumentsByEtat(String etat) throws SQLException {
		PreparedStatement ps = psGetDocumentsByEtat;
		ps.setString(1, etat + "%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String docNumber = rs.getString(1);
			LOGGER.debug("Found document " + docNumber + " with budget " + etat);
			Manifestation doc = new Manifestation(docNumber);
			if (!documents.contains(doc)) {
				LOGGER.debug("Found document " + docNumber + " with budget " + etat);
				documents.add(doc);
			}
		}
		rs.close();
		return documents;
	}

	/**
	 * retrieves the documents connected to a whole notation
	 * 
	 * @param notation
	 *            the notation of the documents retrieved from the Aleph
	 *            database
	 * @return documents set of documents
	 * @exception SQLException exception querying the Aleph database 
	 */
	public Set<Manifestation> getDocumentsByNotation(String notation) throws SQLException {
		PreparedStatement ps = psGetDocumentsByNotation;
		ps.setString(1, notation + "%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String docNumber = rs.getString(1);
			Manifestation doc = new Manifestation(docNumber);
			if (!documents.contains(doc)) {
				documents.add(doc);
			}
		}
		rs.close();
		return documents;
	}

	/**
	 * builds a set of documents from a given csv file.
	 * 
	 * @param csvFile
	 *            a csv file as input containing the document numbers for the
	 *            documents to be build
	 * @return documents set of documents
	 * @exception SQLException exception querying the Aleph database 
	 */
	public Set<Manifestation> getDocumentsByCsv(InputStream csvFile) throws SQLException {
		Scanner inputStream = new Scanner(new InputStreamReader(csvFile));
		String docNumber = "";
		int admColumn = 0;
		boolean data = false;
		while (inputStream.hasNext()) {
			String line = inputStream.nextLine();
			if (!line.contains(";")) {
				if (line.startsWith("ADM"))
					continue;
				docNumber = line.substring(0, 9);
			} else {
				if (!data) {
					String[] parts = line.split(";");
					for (int i = 0; i < parts.length; i++) {
						if (parts[i].startsWith("ADM"))
							admColumn = i;
					}
					data = true;
					LOGGER.info(data);
					continue;
				}

				else {
					String[] parts = line.split(";");
					String rawDocNumber = parts[admColumn];
					while (rawDocNumber.length() < 13)
						rawDocNumber = "0" + rawDocNumber;
					docNumber = rawDocNumber.substring(0, 9);
				}
			}
			Manifestation doc = new Manifestation(docNumber);
			if (!documents.contains(doc)) {
				LOGGER.debug("Added document " + docNumber + " from csv-upload.");
				documents.add(doc);
				LOGGER.info("added document with docNumber " + doc.getDocNumber());
			}
		}
		inputStream.close();
		return documents;
	}

}
