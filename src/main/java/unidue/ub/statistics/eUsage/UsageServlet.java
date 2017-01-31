package unidue.ub.statistics.eUsage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalTools;

/**
 * Delivers the JSON arrays containing the SUSHI usage data.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/journals/usage")
public class UsageServlet extends FachRefServlet {

    private static final long serialVersionUID = 1;

    private static final Logger LOGGER = Logger.getLogger(UsageServlet.class);

    /**
     * reads a collection anchor, an ISSN or a list of ISSNs from the http request, retrieves the documents from the cache and builds the corresponding JSON arrays. 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGet(MCRServletJob job) throws ServletException, IOException {
        HttpServletRequest req = job.getRequest();
        String issns = getParameter(req, "issn");
        String type = JournalTools.determineType(issns);
        JSONObject json = new JSONObject();
        List<Counter> counters = new ArrayList<>();
        if (type.equals("issn")) {
            LOGGER.info("retrieving usage data for ISSN " + issns);
            counters = CounterDAO.getCounters(issns);
            if (counters.size() == 0)
                LOGGER.info("No counter statistics available");
            else {
                List<Counter> cleanedCounters = cleanCounters(counters);
                json = buildJSON(cleanedCounters);
                sendJSON(job.getResponse(), json);
            }
        } else {
            json.put("issn", issns);
            if (type.equals("collection")) {
                JournalCollection collection = JournalCollectionDAO.getCollections(issns).get(0);
                issns  = collection.getIssns();
            }
            String[] issnInd = issns.split(",");
            json.put("name", "Nutzungsverteilung");
            JSONArray seriesList = new JSONArray();
            Hashtable<String, List<Counter>> allResults = new Hashtable<String, List<Counter>>();
            String issnWithMaxData = "";
            List<String> allDates = new ArrayList<>();
            int dataSize = 0;
            for (String issn : issnInd) {
                counters = CounterDAO.getCounters(issn);
                if (counters.size() == 0)
                    LOGGER.info("No counter statistics available");
                else {
                    List<Counter> cleanedCounters = cleanCounters(counters);
                    allResults.put(issn, cleanedCounters);
                    if (cleanedCounters.size() > dataSize) {
                        dataSize = counters.size();
                        issnWithMaxData = issn;
                    }
                }
            }
            if (allResults != null) {
            for (Counter counter : allResults.get(issnWithMaxData))
                allDates.add(counter.getMonth() + "-" + counter.getYear());
            }
            Iterator<List<Counter>> iteration = allResults.values().iterator();
            while (iteration.hasNext()) {
                List<Counter> cleanedCounters = iteration.next();
                JSONArray requestsTotal = new JSONArray();
                
                for (Counter counter : cleanedCounters) {
                    String dateInd = counter.getMonth() + "-" + counter.getYear();
                    if (allDates.contains(dateInd)) {
                        requestsTotal.put(counter.getTotalRequests());
                    } else {
                        Integer i = null;
                        requestsTotal.put(i);
                    }
                }
                seriesList.put(new JSONObject().put("data", requestsTotal).put("name", cleanedCounters.get(0).getFullName()));
            }
            JSONArray categories = new JSONArray();
            for (String date : allDates)
                categories.put(date);
            json.put("series", seriesList).put("categories", categories);
        }
        sendJSON(job.getResponse(), json);
    }

    private void sendJSON(HttpServletResponse res, JSONObject json) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        out.println(json.toString());
        out.close();
    }

    private JSONObject buildJSON(List<Counter> counters) throws IOException {
        JSONObject json = new JSONObject();
        json.put("name", counters.get(0).getFullName());
        json.put("issn", counters.get(0).getOnlineISSN());

        JSONArray categories = new JSONArray();
        JSONArray requestPdf = new JSONArray();
        JSONArray requestPdfMobile = new JSONArray();
        JSONArray requestHtml = new JSONArray();
        JSONArray requestHtmlMobile = new JSONArray();
        JSONArray requestPs = new JSONArray();
        JSONArray requestPsMobile = new JSONArray();
        JSONArray requestsTotal = new JSONArray();
        for (Counter counter : counters) {
            categories.put(String.valueOf(counter.getMonth()) + "-" + String.valueOf(counter.getYear()));
            requestPdf.put(counter.getPdfRequests());
            requestPdfMobile.put(counter.getPdfRequestsMobile());
            requestPs.put(counter.getPsRequests());
            requestPsMobile.put(counter.getPsRequestsMobile());
            requestHtml.put(counter.getHtmlRequests());
            requestHtmlMobile.put(counter.getHtmlRequestsMobile());
            requestsTotal.put(counter.getTotalRequests());
        }
        json.put("categories", categories);

        JSONArray seriesList = new JSONArray();

        JSONObject seriesPdf = new JSONObject().put("name", "PDF");
        seriesPdf.put("data", requestPdf);
        seriesList.put(seriesPdf);

        JSONObject seriesPdfMobile = new JSONObject().put("name", "PDF mobile");
        seriesPdfMobile.put("data", requestPdfMobile);
        seriesList.put(seriesPdfMobile);

        JSONObject seriesPs = new JSONObject().put("name", "PS");
        seriesPs.put("data", requestPs);
        seriesList.put(seriesPs);

        JSONObject seriesPsMobile = new JSONObject().put("name", "PS mobile");
        seriesPsMobile.put("data", requestPsMobile);
        seriesList.put(seriesPsMobile);

        JSONObject seriesHtml = new JSONObject().put("name", "HTML");
        seriesHtml.put("data", requestHtml);
        seriesList.put(seriesHtml);

        JSONObject seriesHtmlMobile = new JSONObject().put("name", "HTML mobile");
        seriesHtmlMobile.put("data", requestHtmlMobile);
        seriesList.put(seriesHtmlMobile);

        json.put("series", seriesList);

        return json;
    }

    private List<Counter> cleanCounters(List<Counter> counters) {
        Hashtable<String, Counter> allDates = new Hashtable<String, Counter>();
        Collections.sort(counters);
        for (Counter counter : counters) {
            String date = counter.getMonth() + "-" + counter.getYear();
            if (allDates.containsKey(date)) {
                Counter counterSum = allDates.get(date).add(counter);
                allDates.replace(date, counterSum);
            } else
                allDates.put(date, counter);
        }
        List<Counter> cleanedCounters = Collections.list(allDates.elements());
        Collections.sort(cleanedCounters);
        return cleanedCounters;
    }

    private String getParameter(HttpServletRequest req, String name) {
        // read parameter "name" from URL
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

}
