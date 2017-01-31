package unidue.ub.statistics.frontend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.transformer.MCRXSLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.analysis.EventAnalysis;
import unidue.ub.statistics.analysis.EventAnalyzer;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

/**
 * Performs and displays the analysis of a set of documents related to a given budget code.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/budgetCollector")
public class BudgetCollectorServlet extends FachRefServlet {
	
    private LocalDate TODAY = LocalDate.now();

    private static final Logger LOGGER = Logger.getLogger(BudgetCollectorServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the necessary parameters from the http request, retrieves the document numbers from the Aleph database and  calculates the loan and request times for each document. 
     * The results are assembled into an xml file, which is displayed as web page by XSLT transformations.
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        String who = job.getRequest().getUserPrincipal().getName();
        Element output = prepareOutput(job,"budgetCollector","profile","budgetCollector");
        Integer yearStart = TODAY.getYear() - 2;
        if (!getParameter(job, "yearStart").isEmpty()) {
            yearStart = Integer.parseInt(getParameter(job, "yearStart"));
        }
        String subject = getParameter(job, "subject");
        String type = getParameter(job, "type");
        String special = getParameter(job, "special");
        String collections = getParameter(job, "collections");
        String materials = getParameter(job, "materials");
        List<String> budgets = new ArrayList<>();
        String budgetRoot = subject + "-" + type;
        if (!special.isEmpty())
            budgetRoot = budgetRoot + "-" + special;
        
        if (!getParameter(job, "yearEnd").isEmpty()) {
            Integer yearEnd =  Integer.parseInt(getParameter(job, "yearEnd"));
            int year = yearStart;
            while (year <= yearEnd) {
                budgets.add(budgetRoot + "-" + String.valueOf(year));
                year++;
            }
        } else
            budgets.add(budgetRoot +  "-" + String.valueOf(yearStart));
        if (!collections.isEmpty())
            output.setAttribute("collections", collections);
        if (!materials.isEmpty())
            output.setAttribute("materials", materials);
        if (budgets.size() != 0) {
            String budgetName = budgets.get(0);
            if (budgets.size() > 1)
                for (int i = 1; i < budgets.size(); i++) {
                    budgetName = budgetName + " " + budgets.get(i);
                }
            output.setAttribute("budget", budgetName);
            LOGGER.info(budgetName + " " + " " + collections + " " + materials);
            ItemFilter filter = new ItemFilter(collections, materials);
            StockControlProperties scp = new StockControlProperties("csv", who);
            for (String budget : budgets) {
                AlephConnection connection = new AlephConnection();
                ItemEventCollector collector = new ItemEventCollector(connection, budget, true, filter, "etat");
                collector.collect();
                connection.disconnect();
                for (Manifestation document : collector.getDocuments()) {
                    if (document.getEvents().size() > 0) {
                    EventAnalyzer analyzer = new EventAnalyzer(document.getEvents(),document.getDocNumber(),scp);
                    EventAnalysis analysis = analyzer.getEventAnalysis();
                    analysis.setShelfmark(document.getCallNo());
                    try {
                        MCRJDOMContent mab = new MCRJDOMContent(document.getMAB());
                        MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
                        String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
                        analysis.setMab(mabText);
                        }catch (Exception e) {
                            LOGGER.info("couldn't get MAB data.");
                        }
                    analysis.addAnalysisToOutput(output);
                    connection.disconnect();
                    }
                }
            }
        }
        sendOutput(job,output);
        LOGGER.info("Finished output.");
    }
}
