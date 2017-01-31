package unidue.ub.statistics.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.DocumentCache;
import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.analysis.EventAnalysis;
import unidue.ub.statistics.analysis.EventAnalyzer;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.stockcontrol.StockControlCache;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

/**
 * Performs and displays the analysis of one or some documents as defined by their document numbers.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/protokoll/analytics")
public class DocumentServlet extends FachRefServlet {

    private static final long serialVersionUID = 1;

    /**
    * reads the necessary document numbers from the http request, retrieves the documents from the document cache and calculates the loan and request times for each document. 
    * The results are assembled into an xml file, which is displayed as web page by XSLT transformations.
    * 
    * 
    * @param job
    *            <code>MCRServletJob</code>
    */
    protected void doGetPost(MCRServletJob job) throws Exception {
        Element output = prepareOutput(job, "document", "protokoll", "analytics");
        List<Event> events = new ArrayList<>();
        HttpServletRequest req = job.getRequest();
        List<String> docNumbers = Arrays.asList(job.getRequest().getParameterValues("docNumber"));
        boolean exact = "true".equals(getParameter(job, "exact"));
        if (docNumbers == null) {
            output = new Element("error").addContent(new Element("message").setText("error.message.noDocNumbersGiven"));
            sendOutput(job, output);
        } else {
            StockControlProperties scp = StockControlProperties.buildSCPFromRequest(req);
            scp.setSCSystemCode("individual");
            scp.setSCSubjectID("00");
            if (!req.getParameter("yearsToAverage").isEmpty())
                scp.setSCYearsToAverage(Integer.parseInt(req.getParameter("yearsToAverage")));
            ;

            List<Manifestation> documents = new ArrayList<>();
            AlephConnection connection = new AlephConnection();
            for (String docNumber : docNumbers) {
                if (DocumentCache.get(docNumber) != null)
                    documents.add(DocumentCache.get(docNumber));
                else {
                    ItemFilter filter = new ItemFilter(scp.getSCCollections(), scp.getSCMaterials());
                    ItemEventCollector collector = new ItemEventCollector(connection, exact, filter);
                    Manifestation document = new Manifestation(docNumber);
                    collector.collectByDocument(document);
                    documents.add(document);
                }
            }
            List<String> callNos = new ArrayList<>();
            String description = "";
            for (Manifestation document : documents) {
                events.addAll(document.getEvents());
                callNos.add(document.getCallNo());
                description = description + document.getCallNo();
            }
            scp.setStockControl(description);
            Collections.sort(events);
            Element analysisPerformed = new Element("eventAnalysis");
            output.addContent(analysisPerformed);
            EventAnalyzer ea = new EventAnalyzer(events, description, scp);
            EventAnalysis analysis = ea.getEventAnalysis();
            analysis.addAnalysisToOutput(analysisPerformed);
            scp.addStockControlToOutput(analysisPerformed);
            Element documentsAnalyzed = new Element("documentsAnalyzed");
            output.addContent(documentsAnalyzed);
            for (int i = 0; i < docNumbers.size(); i++) {
                Element documentAnalyzed = new Element("documentAnalyzed");
                documentAnalyzed.setAttribute("key", docNumbers.get(i));
                documentAnalyzed.addContent(callNos.get(i));
                documentsAnalyzed.addContent(documentAnalyzed);
            }
            StockControlCache.store(scp);
            connection.disconnect();
            sendOutput(job, output);
        }
    }
}
