package unidue.ub.statistics.stockcontrol;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Saves the stock control properties file to disk.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class SCPDefineServlet extends FachRefServlet {
	
    private static final Logger LOGGER = Logger.getLogger(SCPDefineServlet.class);

    private final static String userDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    private static final long serialVersionUID = 1L;

    /**
     * Takes a XEditor submission and writes the corresponding stock control properties file as xml to disk
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    
    protected void doGetPost(MCRServletJob job) throws ServletException, IOException, TransformerException, SAXException {
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
            org.jdom2.Document xmlJDOM = (org.jdom2.Document) job.getRequest().getAttribute("MCRXEditorSubmission");
            DocType doctype = new DocType("properties", "http://java.sun.com/dtd/properties.dtd");
            xmlJDOM.setDocType(doctype);
            MCRJDOMContent xml = new MCRJDOMContent(xmlJDOM);
            
            String id = getParameter(job, "id");
            
            Properties stockControlProperties = new Properties();
            
            stockControlProperties.loadFromXML(xml.getInputStream());

            String stockControl = stockControlProperties.getProperty("subjectID") + "_"+ String.valueOf(System.currentTimeMillis());
                    
            String staticBuffer = stockControlProperties.getProperty("staticBuffer");
            if (staticBuffer.contains(",")) stockControlProperties.setProperty("staticBuffer", staticBuffer.replace(",", "."));
            
            String variableBuffer = stockControlProperties.getProperty("variableBuffer");
            if (variableBuffer.contains(",")) stockControlProperties.setProperty("variableBuffer", variableBuffer.replace(",", "."));
            
            String who = job.getRequest().getUserPrincipal().getName();
            String filename = ""; 
            if (!id.isEmpty())
                filename = id + ".xml";
            else if (stockControlProperties.getProperty("type")!= null)
                filename = stockControlProperties.getProperty("type") + ".xml";
            else 
                filename =  stockControl.replace(" ", "_") + ".xml";

            File outputFile = new File(userDir + "/" + who + "/stockControl", filename);

            if (!outputFile.exists())
                outputFile.createNewFile();

            xml.sendTo(outputFile);

            LOGGER.info("written File stockControlFiles/" + stockControl + ".xml");

            job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/profile");
        }
        Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
        sendOutput(job,output);
    }
}
