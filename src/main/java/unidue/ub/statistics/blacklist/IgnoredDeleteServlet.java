package unidue.ub.statistics.blacklist;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet to delete an <code>Ignored</code> object from the blacklist.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/ignoredDelete")
public class IgnoredDeleteServlet extends FachRefServlet {

    private static final long serialVersionUID = 1L;

    /**
     * takes an identifier from the request and deletes the ignored work from the database 
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws SAXException thrown upon rendering the HTML output
     * @throws TransformerException thrown upon rendering the HTML output
     * @throws IOException thrown upon sending the HTML output
     */
    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
        if (job.getRequest().isUserInRole("fachreferent")) {
            String identifier = job.getRequest().getParameter("identifier");
            IgnoredDAO.removeIgnored(identifier);
            job.getResponse().sendRedirect("blacklist");
        } else {
        Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
        sendOutput(job,output);
        }
    }

}
