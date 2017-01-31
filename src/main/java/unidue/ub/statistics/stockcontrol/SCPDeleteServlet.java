package unidue.ub.statistics.stockcontrol;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Deletes the stock control properties file.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/scpDelete")
public class SCPDeleteServlet extends FachRefServlet {
		
	private static final long serialVersionUID = 1L;
    
    private final static String userDir;
    
    private static final Logger LOGGER = Logger.getLogger(SCPManagementServlet.class);
    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    /**
     * takes a stock control key from the request and deletes the corresponding file on disk
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
            String who = job.getRequest().getUserPrincipal().getName();
    String stockControl = job.getRequest().getParameter("stockControl");
    File toBeDeleted = new File(userDir + "/" + who + "/stockControl", stockControl + ".xml");
    try {toBeDeleted.delete();
    LOGGER.info(stockControl + ".xml deleted!");
    } catch(Exception e) {
        e.printStackTrace();
    }
    LOGGER.info(stockControl + ".xml deleted!");
    job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL()+"fachref/profile");
    }
        Element output = new Element("error");
        output.addContent((new Element("message")).addContent("error.noPermission"));
        sendOutput(job,output);
    }
}
