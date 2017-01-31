package unidue.ub.statistics.eUsage;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;

/**
 * Servlet to delete an <code>JournalCollection</code> object from the database.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/journals/packageDelete")
public class PackageDeleteServlet extends FachRefServlet {
    
    private static final long serialVersionUID = 1L;

    /**
     * reads the anchor from the HTTP request and deletes the entry from the database 
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
            String name = getParameter(job, "anchor");
            JournalCollectionDAO.deleteCollection(name);
            job.getResponse().sendRedirect("packageManagement");
        } else {
            Element output = new Element("error");
            output.addContent((new Element("message")).addContent("error.noPermission"));
            sendOutput(job,output);
        }
    }
}
