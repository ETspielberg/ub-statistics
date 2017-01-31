/**
 * 
 */
package unidue.ub.statistics.eUsage;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollection;

/**
 * Servlet to display the <code>JournalCollection</code> objects in the database.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/journals/packageManagement")
public class PackageManagementServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;
	
	/**
     * retrieves all journal collections from the database and sends them to the output. 
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
		Element output;
        if (currentUser.isAuthenticated()) {
        	output = prepareOutput(job,"packageManagement","journals","packageManagement");
        	int year = LocalDate.now().getYear();
            List<JournalCollection> packs = JournalCollectionDAO.getCollections(year);
            if (packs != null) {
            	for (JournalCollection pack : packs)
            		pack.addToOutput(output);
            }
            sendOutput(job,output);
        } else
            output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
        sendOutput(job,output);
	}
}
