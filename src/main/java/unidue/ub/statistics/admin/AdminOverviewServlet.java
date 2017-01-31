package unidue.ub.statistics.admin;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;
/**
 * Allows a personalized overview page to administer users and general data.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/admin")

public class AdminOverviewServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(AdminOverviewServlet.class);

	/**
     * reads the user credentials and prepares a simple xml file that allows a welcome page.
     * @param job
     *            <code>MCRServletJob</code>
     * @exception IOException exception while rendering output
     * @exception TransformerException exception while rendering output
     * @exception SAXException exception while rendering output
     */
	protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		Element output;
		if (currentUser.hasRole("userAdmin")) {
			output = prepareOutput(job,"adminOverview","admin").setAttribute("userAdmin","");
			String message = getParameter(job,"message");
			if (!message.isEmpty())
				output.addContent(new Element("message").setText(message));
			output.addContent(new Element("userAdmin"));
	        output.addContent("Collections.xed");
	        output.addContent("buildCollectionIndex");
	        output.addContent("buildNotationIndex");
			LOGGER.info("done.");
		} else
			output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
		sendOutput(job,output);
	}
}
