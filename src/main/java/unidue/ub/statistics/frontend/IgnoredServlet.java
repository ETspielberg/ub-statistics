package unidue.ub.statistics.frontend;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.blacklist.Ignored;
import unidue.ub.statistics.blacklist.IgnoredDAO;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

/**
 * Adds documents or works to the blacklist of documents or works which are not to be considered for further analysis.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/ignore")
public class IgnoredServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(IgnoredServlet.class);
    
    private static final long serialVersionUID = 1L;

	private static final long millisPerYear = 31557600000L;

	private String who;

	/**
     * reads the parameters from the http GET request and displays a form for entering extended data for adding documents or works to the blacklist. 
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     */
	protected void doGet(MCRServletJob job) throws IOException, TransformerException, SAXException {
        Element output;
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.hasRole("fachreferent")) {
			output = prepareOutput(job,"Ignored","profile","ignore");
			String identifier = getParameter(job, "identifier");
			String stockControl = getParameter(job, "stockControl");
			String shelfmark = getParameter(job,"shelfmark");
			output.setAttribute("identifier", identifier);
			output.setAttribute("stockControl", stockControl);
			output.setAttribute("shelfmark", shelfmark);
			LOGGER.info("prepared output for " + identifier + " and " + stockControl);
		} else {
			output = new Element("error").addContent((new Element("message")).addContent("noPermission"));
		}
		sendOutput(job,output);
		LOGGER.info("done!");
	}

    /**
     * reads the parameters from the http POST request and adds the document or work to the blacklist. 
     * 
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     */
	protected void doPost(MCRServletJob job) throws Exception {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		HttpServletRequest req = job.getRequest();
		who = req.getUserPrincipal().getName();
		LOGGER.info("Ignored Servlet called by " + who);
        if (currentUser.hasRole("fachreferent")) {
			String identifier = getParameter(job, "identifier");
			LOGGER.info("----------------------------------------------");
            LOGGER.info("adding title to blacklist");
            LOGGER.info("----------------------------------------------");
            String shelfmark = getParameter(job,"shelfmark");
            LOGGER.info(shelfmark);
			String type = getParameter(job, "type");
			LOGGER.info(type);
			String stockControl = getParameter(job, "stockControl");
			LOGGER.info(stockControl);
			String comment = getParameter(job, "comment");
			LOGGER.info(comment);
			String expire = getParameter(job, "expire");
			long timestamp = System.currentTimeMillis();
			long expireTimestamp = 0;
			boolean infiniteExpire = "true".equals(getParameter(job, "infiniteExpire"));
			if (infiniteExpire)
				expireTimestamp = timestamp + 100 * millisPerYear;
			else {
				if (!expire.isEmpty())
					expireTimestamp = timestamp + Long.parseLong(expire) * millisPerYear;
				else {
					try {
					StockControlProperties scp = new StockControlProperties(stockControl, who);
					expireTimestamp = (long) (timestamp + scp.getBlacklistExpire() * millisPerYear);
					} catch (Exception e) {
						expireTimestamp = timestamp + 2 * millisPerYear;
					}
				}
			}
			LOGGER.info(expireTimestamp);
			if (identifier.contains(" ")) {
				String[] identifiers = identifier.split(" ");
				for (String id : identifiers) {
					Ignored ignored = new Ignored();
					if (type.equals("deletion"))
					    ignored.setType("aleph.eventType.deletion");
					else if (type.equals("purchase"))
					    ignored.setType("aleph.eventType.inventory");
					LOGGER.info("building entry " + comment + " " + expireTimestamp + " " + who + " " + id + " " + shelfmark);
					ignored.setComment(comment).setExpire(expireTimestamp).setTimestamp(timestamp).setWho(who).setIdentifier(id).setShelfmark(shelfmark);
					IgnoredDAO.persistIgnorance(ignored);
				}
			} else {
				Ignored ignored = new Ignored();
				ignored.setComment(comment).setExpire(expireTimestamp).setTimestamp(timestamp).setWho(who).setIdentifier(identifier).setShelfmark(shelfmark);
				IgnoredDAO.persistIgnorance(ignored);
				LOGGER.info("building entry " + comment + " " + expireTimestamp + " " + who + " " + identifier + " " + shelfmark);
			}
			job.getResponse().sendRedirect("deletionAssistant?stockControl=" + stockControl);
		} else {
			Element output = new Element("error").addContent((new Element("message")).addContent("noPermission"));
			sendOutput(job,output);
		}
	}

}
