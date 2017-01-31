package unidue.ub.statistics.frontend;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.admin.Notation;
import unidue.ub.statistics.admin.NotationDAO;
import unidue.ub.statistics.alert.AlertControl;
import unidue.ub.statistics.analysis.NRequests;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Retrieves and displays the analysis of a set of documents as defined by a
 * <code>StockControlProperties</code> with respect to possible deletions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/hitlists/hitlist")
public class NRequestsServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(NRequestsServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the necessary parameters from the http request and retrieves the
     * <code>NRequests</code> objects from the database. The results regarding
     * the ratio of requests per lendable item are assembled into an xml file,
     * which is displayed as web page by XSLT transformations.
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        HttpServletRequest req = job.getRequest();
        String who = req.getUserPrincipal().getName();

        Element output;
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
        	output = prepareOutput(job,"nRequests","hitlists","hitlist");
            String readerControl = getParameter(job, "alertControl");
            AlertControl ac = new AlertControl();
            List<NRequests> nRequests = new ArrayList<>();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("tools");
            EntityManager em = emf.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NRequests> q = cb.createQuery(NRequests.class);
            Root<NRequests> c = q.from(NRequests.class);
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (readerControl.isEmpty()) {
                ac.setAlertControl("");
                ac.setNotationRange("A-Z");
                predicates.add(cb.equal(c.get("callNo"), ac.getNotationRange()));
                q.select(c).where(predicates.toArray(new Predicate[] {}));
                nRequests = em.createQuery(q).getResultList();
            } else {
                ac.readFromDisk(readerControl, who);
                LOGGER.info("read readerControl for subject ID " + ac.getSubjectID() + " and notations " + ac.getNotationRange());
                List<Notation> notations = NotationDAO.getNotationsList(ac.getNotationRange());
                for (Notation notation : notations) {
                    Predicate predicate = cb.like(c.<String>get("callNo"), notation.getNotation() + "%");
                    q.select(c).where(predicate);
                    nRequests.addAll(em.createQuery(q).getResultList());
                }
                ac.addToOutput(output);
            }
            em.close();
            for (NRequests nRequest : nRequests) {
                if (nRequest.getRatio() >= ac.getThresholdQuotient()) {
                    nRequest.addOutput(output);
                }
            }
            output.setAttribute("readerControl", ac.getAlertControl());
            LOGGER.info("Finished output.");
        } else
            output = new Element("error").addContent(new Element("message").addContent("noPermission"));
        sendOutput(job,output);
    }
}
