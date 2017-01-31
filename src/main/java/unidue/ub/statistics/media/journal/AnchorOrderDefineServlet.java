/**
 * 
 */
package unidue.ub.statistics.media.journal;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Servlet saving the submitted relation between anchor and order number to the
 * database
 * 
 * @author Eike Spielberg
 * @version 1
 *
 */
@WebServlet("/fachref/eMedia/anchorOrderDefine")
public class AnchorOrderDefineServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

	public EntityManager em;

	/**
	 * takes the parameters from the HTTP post request and stores them in the
	 * database
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * @exception IOException
	 *                thrown upon the writing the result to the output
	 * @exception TransformerException
	 *                thrown upon rendering the output xml
	 * @exception SAXException
	 *                thrown upon building the output xml
	 */
	protected void doPost(MCRServletJob job) throws Exception {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
		em = emf.createEntityManager();
		Element output;
		if (currentUser.isAuthenticated()) {
			String anchor = getParameter(job, "name");
			if (AnchorOrderDAO.getAnchorOrder(anchor, em) == null) {
				buildAnchorOrderFromRequest(job);
				job.getResponse().sendRedirect("anchorOrderManagement");
			} else {
				output = new Element("error").addContent((new Element("message")).addContent("error.anchorOrderExistsAlready"));
				sendOutput(job, output);
			}
		} else {
			output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
			sendOutput(job, output);
		}
		em.close();

	}

	private void buildAnchorOrderFromRequest(MCRServletJob job) {
		String anchor = getParameter(job, "anchor");
		String name = getParameter(job, "name");
		String orderNumber = getParameter(job, "orderNumber");
		String runtime = getParameter(job, "runtime");

		AnchorOrder anchorOrder = new AnchorOrder();
		anchorOrder.setAnchor(anchor).setName(name).setOrderNumber(orderNumber).setRuntime(runtime);
		AnchorOrderDAO.persistAnchorOrder(anchorOrder, em);
	}

}
