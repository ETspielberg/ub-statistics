package unidue.ub.statistics.media.journal;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet deleting a stored relation between anchor and order number from the database
 * @author Eike Spielberg
 * @version 1
 *
 */
@WebServlet("/fachref/eMedia/anchorOrderDelete")
public class AnchorOrderDeleteServlet extends FachRefServlet {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    /**
		 * reads the anchor from the HTTP post request and deletes the corresponding anchor order relation from the database
		 * @param job <code>MCRServletJob</code>
	     * @exception IOException thrown upon the writing the result to the output
	     * @exception TransformerException thrown upon rendering the output xml
	     * @exception SAXException thrown upon building the output xml
		 */
	    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
	        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
	        if (currentUser.isAuthenticated()) {
	            String anchor = getParameter(job, "anchor");
	            AnchorOrderDAO.deleteAnchorOrder(anchor);
	            job.getResponse().sendRedirect("anchorOrderManagement");
	        } else {
	            Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
	            sendOutput(job,output);
	        }
	    }
}
