package unidue.ub.statistics.frontend;

import org.jdom2.Element;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.analysis.EventAnalysis;
import unidue.ub.statistics.analysis.EventAnalysisDAO;
import unidue.ub.statistics.stockcontrol.StockControlCache;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;

/**
 * Retrieves and displays the analysis of a set of documents as defined by a <code>StockControlProperties</code> with respect to possible purchases.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/purchaseAssistant")
public class PurchaseAssistantServlet extends FachRefServlet {

    private static final long serialVersionUID = 1;

    /**
     * reads the necessary parameters from the http request and retrieves the <code>EventAnalysis</code> objects from the database. 
     * The results regarding the proposal of purchases are assembled into an xml file, which is displayed as web page by XSLT transformations.
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        String who = job.getRequest().getUserPrincipal().getName();
        Element output = prepareOutput(job,"purchaseList","profile","purchaseAssistant");
        StockControlProperties scp = StockControlProperties.buildSCPFromRequest(job.getRequest());
        StockControlCache.store(scp);
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        List<EventAnalysis> analyses = EventAnalysisDAO.getEventAnalyses(scp, who, em);
        em.close();
        for (EventAnalysis analysis : analyses) {
            analysis.addAnalysisToOutput(output);
        }
        scp.addStockControlToOutput(output);
        MCRSessionMgr.getCurrentSession().put(scp.getStockControl(), output);
        sendOutput(job,output);
    }
}
