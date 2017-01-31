/**
 * 
 */
package unidue.ub.statistics.eUsage;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollection;

/**
 * Returns the contained journal titles for a given journal collection as taken from the EZB import.
 * @author Eike Spielberg
 *
 */
@WebServlet("/fachref/journals/journalTitleManagement")
public class JournalTitleManagementServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(JournalTitleManagementServlet.class);
	
	/**
     * reads the anchor from the HTTP request and adds all contained journal titles to the output. 
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws SAXException thrown upon rendering the HTML output
     * @throws TransformerException thrown upon rendering the HTML output
     * @throws IOException thrown upon sending the HTML output
     */
	protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			Element output = prepareOutput(job,"journalTitleManagement","journals","journalTitleManagement");
            String anchor = getParameter(job,"anchor");
        	output.setAttribute("anchor",anchor);
        	int year = LocalDate.now().getYear();
        	String yearString = getParameter(job,"year");
        	if (!yearString.isEmpty())
        	    year = Integer.parseInt(yearString);
            JournalCollection collection = JournalCollectionDAO.getCollection(anchor,year);
            if (collection != null) {
            collection.addToOutput(output);
            List<JournalTitle> journalTitles = collection.getLatestJournals();
            LOGGER.info("found " + journalTitles.size() + " journal titles in the collection " + anchor);
            for (JournalTitle journalTitle : journalTitles) {
                journalTitle.addToOutput(output);
            }
            }
            sendOutput(job,output);
        } else {
            Element output = new Element("error");
            output.addContent((new Element("message")).addContent("error.noPermission"));
            sendOutput(job,output);
        }
	}
}
