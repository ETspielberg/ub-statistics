package unidue.ub.statistics.frontend;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.analysis.EventAnalysis;
import unidue.ub.statistics.blacklist.IgnoredDAO;
import unidue.ub.statistics.stockcontrol.StockControlCache;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

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
 * Retrieves and displays the analysis of a set of documents as defined by a <code>StockControlProperties</code> with respect to possible deletions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/deletionAssistant")
public class DeletionAssistantServlet extends FachRefServlet {
	
    private static final Logger LOGGER = Logger.getLogger(DeletionAssistantServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the necessary parameters from the http request and retrieves the <code>EventAnalysis</code> objects from the database. 
     * The results regarding the proposal of deletions are assembled into an xml file, which is displayed as web page by XSLT transformations.
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        HttpServletRequest req = job.getRequest();
        String who = job.getRequest().getUserPrincipal().getName();
        Element output = prepareOutput(job,"deletionList","profile","deletionAssistant");
        StockControlProperties scp;
        scp = StockControlProperties.buildSCPFromRequest(req);
        StockControlCache.store(scp);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("eventAnalysis");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EventAnalysis> q = cb.createQuery(EventAnalysis.class);
        Root<EventAnalysis> c = q.from(EventAnalysis.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.get("stockControl"), scp.getStockControl()));
        predicates.add(cb.gt(c.<Integer> get("proposedDeletion"),scp.getSCThreshold()));
        predicates.add(cb.equal(c.get("author"), who));
        q.select(c).where(predicates.toArray(new Predicate[]{}));
        List<EventAnalysis> analyses = em.createQuery(q).getResultList();
        EntityManager emTools = Persistence.createEntityManagerFactory("tools").createEntityManager();
        for (EventAnalysis analysis : analyses) {
            if (IgnoredDAO.contains(analysis.getDescription(), emTools))
                continue;
            analysis.addAnalysisToOutput(output);
        }
        scp.addStockControlToOutput(output);
        MCRSessionMgr.getCurrentSession().put(scp.getStockControl(), output);
        sendOutput(job, output);
        LOGGER.info("Finished output.");
    }
}
