package unidue.ub.statistics.alephConnector;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.Namespace;
import org.jdom2.Verifier;

import unidue.ub.statistics.analysis.NRequests;
import unidue.ub.statistics.media.monographs.Manifestation;

/**
 * Retrieves the MAB-data from the Aleph database.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class MABGetter {

	private final static Namespace NSMABXML = Namespace
			.getNamespace("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd");

	private final static String sql = "select z00_data, z00_data_len from edu01.z00 where z00_doc_number = ?";

	private final static String sqlRecKey = "select distinct substr(z103_rec_key_1,6,9) from edu50.z103 where z103_lkr_library = 'EDU50' and z103_rec_key like ?";

	// private final static String sqlSuper = "select z11_doc_number from
	// edu01.z11 where substr(z11_rec_key,1,5) = 'IDN ' and
	// substr(z11_rec_key,6,11) = ?";

	private PreparedStatement ps;

	private PreparedStatement psGetRecKey;

	// private PreparedStatement psSuper;

	// private boolean getSuper;

	// private String superHTNumber;

	/**
	 * uses a given connection to Aleph database to build an instance of the
	 * <code>MABGetter</code>-object
	 * 
	 * @param connection
	 *            an <code>AlephConnection</code>-object
	 * @exception SQLException exception querying the Aleph database 
	 */

	public MABGetter(AlephConnection connection) throws SQLException {
		// initilaize the requests
		ps = connection.prepareStatement(sql);
		psGetRecKey = connection.prepareStatement(sqlRecKey);
		// psSuper = connection.prepareStatement(sqlSuper);
	}

	/**
	 * retrieves the bibliographic information as MAB-fields for a given
	 * document from the Aleph database
	 * 
	 * @param document
	 *            the document the bibliographics information are retrieved for
	 * @exception Exception general exception
	 * @return Element org.jdom2.element holding the bibliographic information in MAB format
	 */
	public Element getMAB(Manifestation document) throws Exception {
		// set flag to false, so that no �berordnung exists. flag will be
		// changed, if a field "010" is found.
		// getSuper = false;
		Element mabXML = buildMAB(getRecKey(document));
		// if the flag has been changed, get also the MAB for the �berordnung
		// and append it to the resulting XML.
		/*
		 * if (getSuper) { String recKeySuper = getSuperRecKey(superHTNumber);
		 * LOGGER.info(recKeySuper); if (recKeySuper != "") { Element
		 * mabXMLSuper = buildMAB(recKeySuper); mabXML.addContent(mabXMLSuper);
		 * } }
		 */
		return mabXML;

	}
	
	/**
	 * retrieves the bibliographic information as MAB-fields for a given
	 * <code>NRequests</code> from the Aleph database
	 * 
	 * @param nRequests
	 *            the <code>NRequests</code> the bibliographics information are retrieved for
	 * @exception Exception general exception
	 * @return Element org.jdom2.element holding the bibliographic information in MAB format
	 */
	public Element getMAB(NRequests nRequests) throws Exception {
		// set flag to false, so that no �berordnung exists. flag will be
		// changed, if a field "010" is found.
		// getSuper = false;
	    psGetRecKey.setString(1, "EDU50" + nRequests.getDocNumber() + "%");
        ResultSet rs = psGetRecKey.executeQuery();
        String docNumber="";
        if (rs.next()) {
            docNumber = rs.getString(1);
        }
        rs.close();
		Element mabXML = buildMAB(docNumber);
		// if the flag has been changed, get also the MAB for the �berordnung
		// and append it to the resulting XML.
		/*
		 * if (getSuper) { String recKeySuper = getSuperRecKey(superHTNumber);
		 * LOGGER.info(recKeySuper); if (recKeySuper != "") { Element
		 * mabXMLSuper = buildMAB(recKeySuper); mabXML.addContent(mabXMLSuper);
		 * } }
		 */
		return mabXML;

	}

	private String getRecKey(Manifestation document) throws Exception {
		String docNumber = document.getDocNumber();
		psGetRecKey.setString(1, "EDU50" + docNumber + "%");
		ResultSet rs = psGetRecKey.executeQuery();
		if (rs.next()) {
			docNumber = rs.getString(1);
		}
		rs.close();
		return docNumber;
	}

	private Element buildMAB(String recKey) throws SQLException, UnsupportedEncodingException {
		ps.setString(1, recKey);
		ResultSet rs = ps.executeQuery();

		if (!rs.next())
			return null;
		int id = Integer.parseInt(recKey);
		byte[] blob = rs.getBytes(1);
		int dlen = rs.getInt(2);
		Element mabXML = buildXML(blob, dlen, id);
		rs.close();

		return mabXML;
	}

	private Element buildXML(byte[] blob, int dlen, int id) throws UnsupportedEncodingException {
		Element datensatz = new Element("datensatz", NSMABXML);
		datensatz.setAttribute("id", String.valueOf(id));

		int offset = 0;
		while (offset < dlen) {
			byte[] tmp = new byte[4];
			System.arraycopy(blob, offset, tmp, 0, 4);
			String slen = new String(tmp, "UTF8");
			int len = Integer.parseInt(slen);
			offset += 4;

			tmp = new byte[len];
			System.arraycopy(blob, offset, tmp, 0, len);
			String field = new String(tmp, "UTF8");
			offset += len;

			String fieldname = field.substring(0, 3);
			String fieldind = field.substring(3, 4);

			if (fieldname.equals("LDR")) {
				String satzkennung = field.substring(6).trim();
				if (satzkennung.length() >= 25)
					datensatz.setAttribute("typ", satzkennung.substring(23, 24));
				else
					datensatz.setAttribute("typ", "h");
				if (satzkennung.length() >= 7)
					datensatz.setAttribute("status", satzkennung.substring(5, 6));
				if (satzkennung.length() >= 11)
					datensatz.setAttribute("mabVersion", satzkennung.substring(6, 10));
				continue;
			}

			if (fieldname.equals("CAT"))
				continue;

			Element feld = new Element("feld", NSMABXML);
			feld.setAttribute("nr", fieldname);
			feld.setAttribute("ind", fieldind);

			// change flag to indicate that an �berordnung exists.

			datensatz.addContent(feld);

			String fieldcont = field.substring(6).trim();
			if (!fieldcont.startsWith("$$")) {
				if (fieldcont.length() > 0)
					setFieldText(feld, fieldcont);
			} else {
				int dd1 = 0, dd2 = 0;

				while (dd2 < fieldcont.length()) {
					dd1 = fieldcont.indexOf("$$", dd2);
					String ufk = fieldcont.substring(dd1 + 2, dd1 + 3);

					Element uf = new Element("uf", NSMABXML);
					uf.setAttribute("code", ufk);
					feld.addContent(uf);

					dd2 = fieldcont.indexOf("$$", dd1 + 3);
					if (dd2 < 0)
						dd2 = fieldcont.length();

					String cont = fieldcont.substring(dd1 + 3, dd2).trim();

					if (fieldname.equals("010")) {
						// getSuper = true;
						// superHTNumber = cont;
					}

					setFieldText(uf, cont);
				}
			}
		}
		return datensatz;
	}

	private void setFieldText(Element field, String text) {
		int pos1 = text.indexOf("<<");
		int pos2 = text.indexOf(">>");

		if ((pos1 >= 0) && (pos2 > pos1)) {
			String s1 = text.substring(0, pos1).trim();
			String s2 = text.substring(pos1 + 2, pos2).trim();
			String s3 = text.substring(pos2 + 2).trim();

			if (s1.length() > 0)
				setFieldText(field, s1);
			Element ns = new Element("ns", NSMABXML);
			setFieldText(ns, s2);
			field.addContent(ns);
			if (s3.length() > 0)
				setFieldText(field, s3);
		} else {
			try {
				field.addContent(text);
			} catch (IllegalDataException idx) {
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < text.length(); i++)
					if (Verifier.isXMLCharacter(text.charAt(i)))
						sb.append(text.charAt(i));

				field.addContent(sb.toString());
			}
		}
	}
	/*
	 * private String getSuperRecKey(String superHTNumber) throws SQLException {
	 * String recKey = ""; //query Z11 to retrieve the recKey of the
	 * �berordnung. LOGGER.info("querying " + superHTNumber.replace("HT","ht"));
	 * psSuper.setString(1, superHTNumber.replace("HT","ht")); ResultSet rsSuper
	 * = psSuper.executeQuery(); LOGGER.info(rsSuper); while (rsSuper.next()) {
	 * recKey = rsSuper.getString("z11_doc_number"); LOGGER.info(recKey); }
	 * rsSuper.close(); return recKey; }
	 */
}
