package unidue.ub.statistics.alert;

import static org.quartz.TriggerKey.triggerKey;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Saves the alert control file to disk.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/hitlists/alertDelete")
public class AlertDeleteServlet extends FachRefServlet {

	private static final Logger LOGGER = Logger.getLogger(AlertDeleteServlet.class);

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Takes a XEditor submission and deletes the corresponding alert control file from disk
	 * 
	 * @throws ServletException 
     * @throws TransformerException 
     * @throws IOException 
     * @throws SAXException 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */

	protected void doGetPost(MCRServletJob job)
			throws ServletException, IOException, TransformerException, SAXException {
		if (job.getRequest().isUserInRole("fachreferent")) {
			String who = job.getRequest().getUserPrincipal().getName();
			String alertControl = job.getRequest().getParameter("alertControl");
			File toBeDeleted = new File(userDir + "/" + who + "/alert", alertControl + ".xml");
			try {
			    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	            scheduler.unscheduleJob(triggerKey(alertControl, who));
				toBeDeleted.delete();
				LOGGER.info(alertControl + ".xml deleted!");
			} catch (Exception e) {
				LOGGER.info("could not delete " + alertControl + ".xml");
			}
			job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/hitlists");
		}
		Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
		sendOutput(job,output);
	}
}
