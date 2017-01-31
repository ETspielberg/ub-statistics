package unidue.ub.statistics.eUsage.sushi;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.PublisherDAO;

/**
 * Deletes a stored SUSHI Provider.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/publisherDelete")
public class SushiProviderDeleteServlet extends FachRefServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * reads the necessary parameters from the http request, and deletes the
	 * corresponding SUSHI provider in the database.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            String name = getParameter(job, "name");
            PublisherDAO.deletePublisher(name);
            job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia/publisherManagement");
        } else {
            Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
            sendOutput(job,output);
        }
    }
}
