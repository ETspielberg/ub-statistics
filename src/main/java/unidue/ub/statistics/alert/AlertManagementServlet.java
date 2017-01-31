/**
 * 
 */
package unidue.ub.statistics.alert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.stockcontrol.SCPManagementServlet;

/**
 * Servlet summarizing the user defined alerts and reader digest
 * @author Spielberg
 *
 */
@WebServlet("/fachref/hitlists")
public class AlertManagementServlet extends FachRefServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static String userDir;

    private static final Logger LOGGER = Logger.getLogger(SCPManagementServlet.class);

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    /**
     * reads in the alert files in the user directory and writes the found information into an xml file to be display by XSLT transformation
     * 
     * 
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon writing the response to the output or reading the file from disk
     * @exception JDOMException thrown upon parsing the file from disk
     * @exception TransformerException thrown upon rendering the output
     * @exception SAXException thrown upon preparing the output
     *    
     */
    protected void doGetPost(MCRServletJob job) throws IOException, JDOMException, TransformerException, SAXException {
        String who = job.getRequest().getUserPrincipal().getName();
        Element output = prepareOutput(job,"nRequestsManagement","hitlists");
        File alertControlDirectory = new File(userDir + "/" + who + "/alert");
        if (alertControlDirectory.exists()) {
            Element readers = new Element("readers");
            Element alerts = new Element("alerts");
            for (String name : Arrays.asList(alertControlDirectory.list())) {
                AlertControl ac = new AlertControl().readFromDisk(name.substring(0, name.indexOf(".")), who);
                ac.addToOutput(alerts);
            }
            output.addContent(alerts);
            output.addContent(readers);
        }
        sendOutput(job,output);
        LOGGER.info("Finished output.");
    }

}
