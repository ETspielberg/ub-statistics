package unidue.ub.statistics.frontend;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.blacklist.Ignored;
import unidue.ub.statistics.blacklist.IgnoredDAO;

/**
 * Retrieves and displays the ignored documents .
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/blacklist")
public class IgnoredManagementServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(NRequestsServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the necessary parameters from the http request and displays the 
     * <code>Ignored</code> objects stored in the database.
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        Element output;
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
        	output = prepareOutput(job,"ignoredManagement","profile","blacklist");
            List<Ignored> allIgnored = IgnoredDAO.listIgnored();
            if (allIgnored != null) {
                for (Ignored ignored : allIgnored) {
                    ignored.addToOutput(output);
                }
            }
            
            LOGGER.info("Finished output.");
        } else
            output = new Element("error").addContent(new Element("message").addContent("noPermission"));
        sendOutput(job,output);
    }
}
