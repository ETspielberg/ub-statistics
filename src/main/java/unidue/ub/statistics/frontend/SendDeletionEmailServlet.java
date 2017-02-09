package unidue.ub.statistics.frontend;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.blacklist.Ignored;
import unidue.ub.statistics.blacklist.IgnoredDAO;

/**
 * Prepares an email to be send to the library staff.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/sendDeletionEmail")
public class SendDeletionEmailServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private final static String userDir;
	
	private final static String archiveDir;

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final static LocalDate TODAY = LocalDate.now();
	
	private static final long millisPerYear = 31557600000L;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
		archiveDir = config.getString("ub.statistics.archiveDir");
	}

	/**
	 * receives xml file with deletions from the XEditor, prepares an email to
	 * be send to the library staff, which is opened in the local mail client.
	 * 
	 * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		org.jdom2.Document xmlJDOM = (org.jdom2.Document) job.getRequest().getAttribute("MCRXEditorSubmission");
		org.jdom2.Document xmlArchive = xmlJDOM.clone();

		Element unfilteredList = xmlJDOM.detachRootElement().clone();
		String collections = unfilteredList.getChild("stockControlProperties").getChildText("collections");
		String stockControl = unfilteredList.getChild("stockControlProperties").getChildText("stockControl");

		String who = job.getRequest().getUserPrincipal().getName();
		if (job.getRequest().isUserInRole("fachreferent")) {

			File file = new File(userDir + "/" + who, "user_data.xml");
			SAXBuilder builder = new SAXBuilder();

			org.jdom2.Document document = (org.jdom2.Document) builder.build(file);
			Element rootNode = document.getRootElement();
			Element details = rootNode.getChild("details");
			String senderName = details.getChildText("name");
			String interval = rootNode.getChildText("interval");
			String subject = "Bitte um Aussonderung";
			String recipient = unfilteredList.getChildText("recipient");
			
			String deletionList = "";
			int totalNumber = 0;
			for (Element documentAnalysis : unfilteredList.getChild("deletions").getChildren("documentAnalysis")) {
				String toBeDeleted = documentAnalysis.getChild("analysis").getChildText("finalDeletion");
				String proposedDeletion = documentAnalysis.getChild("analysis").getChildText("proposedDeletion");
				Ignored ignored = new Ignored();
				ignored.setComment("deleted " + toBeDeleted + " items; proposed " + proposedDeletion + "items.");
				if (interval.contains(","))
					interval.replace(",", ".");
				Double yearsOfInterval;
				try {
					yearsOfInterval = Double.parseDouble(interval);
				} catch (Exception e) {
					yearsOfInterval = 5.;
				}
				ignored.setExpire((long) (System.currentTimeMillis() + yearsOfInterval * millisPerYear));
				ignored.setIdentifier(documentAnalysis.getAttributeValue("key"));
				ignored.setShelfmark(documentAnalysis.getAttributeValue("shelfmark"));
				ignored.setComment("Aussonderung von " + toBeDeleted + " Exemplaren");
				ignored.setWho(who);
				ignored.setType("aleph.eventType.deletion");
				IgnoredDAO.persistIgnorance(ignored);
				if (toBeDeleted != null) {
					String shelf = documentAnalysis.getAttributeValue("shelfmark");

					String comment = documentAnalysis.getChild("analysis").getChildText("comment");
					deletionList = deletionList + toBeDeleted + " Exemplar(e) von " + collections + " : " + shelf;
					if (comment != null)
						deletionList = deletionList + " (" + comment + ")";
					deletionList = deletionList + " \n";

					totalNumber++;
				}
			}
			
			String greeting = "\n" + "Vielen herzlichen Dank.\n" + "\n Beste Grüße, \n" + senderName;
			String mailContents = "";
			if (totalNumber == 1) {
				String pledge = "Liebe Kolleginnen und Kollegen,\n" + "\n"
						+ "bitte sondern Sie folgendes Exemplar aus: \n";
				mailContents = pledge + deletionList + greeting;
			} else if (totalNumber > 1) {
				String pledge = "Liebe Kolleginnen und Kollegen,\n" + "\n"
						+ "bitte sondern Sie folgende Exemplare aus: \n";
				mailContents = pledge + deletionList + greeting;
			} else {
			}
			String uriStr = String.format("mailto:%s?subject=%s&body=%s", recipient, urlEncode(subject),
					urlEncode(mailContents));
			job.getResponse().sendRedirect(uriStr);

			String filenameArchive = "Aussonderung_" + stockControl + TODAY.format(dtf);
			
			File archiveFile = new File(archiveDir, filenameArchive);
			new MCRJDOMContent(xmlArchive).sendTo(archiveFile);
		}
	}

	private static final String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
