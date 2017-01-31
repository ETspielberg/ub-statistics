package unidue.ub.statistics.help;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

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

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet displaying the help contents
* @author Eike Spielberg
* @version 1
*/
@WebServlet("/help/*")
public class HelpStartServlet extends FachRefServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static String helpDir;
	
	private final static Logger LOGGER = Logger.getLogger(HelpStartServlet.class);
    
    static {
        MCRConfiguration config = MCRConfiguration.instance();
        helpDir = config.getString("ub.statistics.helpDir");
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
        Element output;
        String lang = getParameter(job,"lang");
        if (lang.isEmpty())
        	lang = "de";
        File helpFile = new File(helpDir, "start_" + lang + ".xml");
        String path = job.getRequest().getPathInfo();
        if (path == null) {
        	output = prepareOutput(job,"help");
        } else {
			StringTokenizer tok = new StringTokenizer(path, "/");
			String module = tok.nextToken();
			if (tok.hasMoreElements()){
				String function = tok.nextToken();
				output = prepareOutput(job,"help",module,function);
				helpFile = new File(helpDir, function + "_" + lang + ".xml");
			} else {
				output = prepareOutput(job,"help",module);
				helpFile = new File(helpDir, module + "_" + lang + ".xml");
			}
        }
        LOGGER.info(helpFile);
        SAXBuilder builder = new SAXBuilder();
        Element contents = builder.build(helpFile).detachRootElement().clone();
        output.addContent(contents);
        sendOutput(job,output);
    }
}
