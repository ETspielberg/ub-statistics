package unidue.ub.statistics.userauth;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * Saves the user properties file to disk.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet(value = "/fachref/userDefine", name = "UserDefine")
public class UserDefineServlet extends MCRServlet {

	private static final Logger LOGGER = Logger.getLogger(UserDefineServlet.class);

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Takes a XEditor submission and writes the corresponding user properties
	 * file as xml to disk
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws ServletException, IOException {
		org.jdom2.Document xmlJDOM = (org.jdom2.Document) job.getRequest().getAttribute("MCRXEditorSubmission");

		MCRJDOMContent xml = new MCRJDOMContent(xmlJDOM);

		String who = job.getRequest().getUserPrincipal().getName();

		File outputFile = new File(userDir + "/" + who, "user_data.xml");

		if (!outputFile.exists())
			outputFile.createNewFile();

		xml.sendTo(outputFile);

		LOGGER.info("written user configuration to user_data.xml");

		job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/start");

	}

}
