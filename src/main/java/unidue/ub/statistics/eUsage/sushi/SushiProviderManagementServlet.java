package unidue.ub.statistics.eUsage.sushi;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.Publisher;
import unidue.ub.statistics.media.journal.PublisherDAO;

/**
 * Servlet summarizing the defined SUSHI providers from the database
 * @author Spielberg
 *
 */
@WebServlet("/fachref/eMedia/publisherManagement")
public class SushiProviderManagementServlet extends FachRefServlet {

    private static final long serialVersionUID = 1L;

    /**
     * reads the SUSHI providers stored in the database and returns a XML list to be rendered
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     * 
     */
    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {

        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        Element output;
   
        if (currentUser.isAuthenticated()) {
        	output = prepareOutput(job,"publisherManagement","eMedia","publisherOverview");
        	List<Publisher> publishers = PublisherDAO.getPublishers();
            for (Publisher publisher : publishers)
                publisher.addToOutput(output);
           
        } else
            output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
        sendOutput(job,output);
    }

}
