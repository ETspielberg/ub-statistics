package unidue.ub.statistics.admin;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet for defining collections of place codings.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet(value = "/fachref/collectionDefine", name = "CollectionDefine")
public class CollectionDefineServlet extends FachRefServlet {

	private static final Logger LOGGER = Logger.getLogger(CollectionDefineServlet.class);

	private final static String dataDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		dataDir = config.getString("ub.statistics.localResourcesDir");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Takes a XEditor submission and writes the corresponding collection list
	 * file as xml to disk
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job)
			throws ServletException, IOException, TransformerException, SAXException {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.hasRole("fachreferent")) {
			org.jdom2.Document xmlJDOM = (org.jdom2.Document) job.getRequest().getAttribute("MCRXEditorSubmission");
			MCRJDOMContent xml = new MCRJDOMContent(xmlJDOM);
		File outputFile = new File(dataDir, "collections.xml");
			if (!outputFile.exists())
				outputFile.createNewFile();
			xml.sendTo(outputFile);
			LOGGER.info("written collections.xml");
			job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "admin?message=savedCollections");
		} else {
			Element output = new Element("error");
			output.addContent((new Element("message")).addContent("error.noPermission"));
			getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(output));
		}
	}
}
