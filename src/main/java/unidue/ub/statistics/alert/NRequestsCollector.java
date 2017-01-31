package unidue.ub.statistics.alert;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.transformer.MCRXSLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.MABGetter;
import unidue.ub.statistics.alephConnector.NRequestsGetter;
import unidue.ub.statistics.analysis.NRequests;
import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Collects for a given subset of notations the number of requests.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/hitlists/completeHitlist")
public class NRequestsCollector extends FachRefServlet implements Job {

	private static final Logger LOGGER = Logger.getLogger(NRequestsCollector.class);

	private static final long serialVersionUID = 1;

	/**
	 * reads the necessary parameters from the http-request and prepares a
	 * org.jdom2.document to be displayed.
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * 
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest req = job.getRequest();

		Element output = prepareOutput(job,"nRequests","hitlists","completehitlist");
		output.setAttribute("now", String.valueOf(System.currentTimeMillis()));
		
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		String friendlyName = (String) req.getSession().getAttribute("friendlyName");
		if (currentUser.hasRole("fachreferent"))
			output.setAttribute("loggedInAs", friendlyName);
		output.setAttribute("name", friendlyName);
		AlephConnection connection = new AlephConnection();
		NRequestsGetter getter = new NRequestsGetter(connection);
		MABGetter mabGetter = new MABGetter(connection);
		List<NRequests> nRequests = getter.getAllNRequests();
		for (NRequests nRequest : nRequests) {
			try {
				MCRJDOMContent mab = new MCRJDOMContent(mabGetter.getMAB(nRequest));
				MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
				String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
				if (mabText.length() == 0)
					LOGGER.info("no MAB data.");
				nRequest.setMab(mabText);
			} catch (Exception e) {
				LOGGER.info("could not get MAB data.");
			}
			nRequest.addOutput(output);
		}
		connection.disconnect();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("tools");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.createQuery("DELETE FROM NRequests").executeUpdate();
        for (NRequests nRequest : nRequests) {
            em.persist(nRequest);
        }
        tx.commit();
        em.close();
		sendOutput(job,output);
		LOGGER.info("Finished output.");
	}

	/**
	 * execute method to be called by the quartz framework.
	 * 
	 * @param context
	 *            job execution context from the quartz framework
	 * 
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			AlephConnection connection = new AlephConnection();
			NRequestsGetter getter = new NRequestsGetter(connection);
			MABGetter mabGetter = new MABGetter(connection);
			List<NRequests> nRequests = getter.getNRequests("A-Z");
			for (NRequests nRequest : nRequests) {
				try {
					MCRJDOMContent mab = new MCRJDOMContent(mabGetter.getMAB(nRequest));
					MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
					String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
					if (mabText.length() == 0)
						LOGGER.info("no MAB data.");
					nRequest.setMab(mabText);
				} catch (Exception e) {
					LOGGER.info("could not get MAB data.");
				}
			}
			connection.disconnect();
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("tools");
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.createQuery("DELETE FROM NRequests").executeUpdate();
			for (NRequests nRequest : nRequests) {
				em.persist(nRequest);
			}
			tx.commit();
			em.close();
			
		} catch (Exception e) {
			LOGGER.info("could not update nRequests table");
		}

	}
}
