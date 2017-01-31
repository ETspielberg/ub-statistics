package unidue.ub.statistics.csvHandling;

import java.io.File;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * Deletes a stored csv file.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/csvDelete")
@MultipartConfig
public class CSVDeleteServlet extends MCRServlet {

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	private static final Logger LOGGER = Logger.getLogger(CSVDeleteServlet.class);

	private static final long serialVersionUID = 1;

	/**
	 * reads the necessary parameters from the http request, and deletes the
	 * corresponding file in the user directory.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest req = job.getRequest();
		String who = req.getUserPrincipal().getName();
		String filename = job.getRequest().getParameter("file");

		File toBeDeleted = new File(userDir + "/" + who + "/upload", filename);
		try {
			toBeDeleted.delete();
			LOGGER.info(filename + " deleted!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/profile");
	}
}
