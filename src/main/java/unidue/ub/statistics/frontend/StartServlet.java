package unidue.ub.statistics.frontend;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

/**
 * Allows a personalized start page.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/start")
public class StartServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

    private final static String userDir;

    private static final Logger LOGGER = Logger.getLogger(StartServlet.class);

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    /**
     * reads the user credentials and prepares a simple xml file that allows a personlized welcome page.
     * @param job
     *            <code>MCRServletJob</code>
     * @exception IOException exception while reading systematik.xml file from disk
     * @exception JDOMException exception upon parsing the systematik.xml file
     * @exception TransformerException exception while rendering output
     * @exception SAXException exception while rendering output
     */
    protected void doGetPost(MCRServletJob job) throws ServletException, IOException, JDOMException, TransformerException, SAXException {
        Element output = prepareOutput(job,"start");

        String who = job.getRequest().getUserPrincipal().getName();
        File userFile = new File(userDir + "/" + who, "user_data.xml");
        if (userFile.exists()) {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document document;
            document = (org.jdom2.Document) builder.build(userFile);

            Element rootNode = document.getRootElement();
            String username = rootNode.getChild("details").getChild("fullname").getValue();
            output.setAttribute("loggedInAs", username);
            output.setAttribute("email", who);
            LOGGER.info("prepared start page for " + username);
         }
       sendOutput(job,output);
     }
}
