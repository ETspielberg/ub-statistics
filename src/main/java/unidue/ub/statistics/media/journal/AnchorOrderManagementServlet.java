/**
 * 
 */
package unidue.ub.statistics.media.journal;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet listing all the relations between anchors and order numbers and sends them to the output. 
 * @author Eike Spielberg
 *
 */
@WebServlet("/fachref/eMedia/anchorOrderManagement")
public class AnchorOrderManagementServlet extends FachRefServlet {
	
	 private static final long serialVersionUID = 1L;
	 
	 protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {

	        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
	   Element output;
	        if (currentUser.isAuthenticated()) {
	        	output =prepareOutput(job,"anchorOrderManagement","eMedia","anchorOrderManagement");
	            List<AnchorOrder> anchorOrders = AnchorOrderDAO.getAnchorOrders();
	            for (AnchorOrder anchorOrder : anchorOrders)
	            	anchorOrder.addToOutput(output);
	            sendOutput(job,output);
	        } else
	            output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
	        sendOutput(job,output);
	    }

}
