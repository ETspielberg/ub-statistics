/**
 * 
 */
package unidue.ub.statistics.frontend;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.eUsage.CollectionUsagePerSubject;
import unidue.ub.statistics.eUsage.CollectionUsagePerSubjectDAO;
import unidue.ub.statistics.eUsage.CounterDAO;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;
import unidue.ub.statistics.media.journal.JournalTools;

/**
 * Servlet showing the collections present in the database
 * 
 * @author Eike Spielberg
 *
 */
@WebServlet("/fachref/journals/journalCollectionMetrics")
public class JournalCollectionMetricsServlet extends FachRefServlet {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(JournalCollectionMetricsServlet.class);

    /**
     * reads the necessary parameters from the http request and displays collects information about a particular journal.
     * 
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     * 
     */
    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
        Element output = prepareOutput(job, "journalCollectionMetrics", "journals", "journalCollectionMetrics");
        String anchor = getParameter(job, "anchor");
        String type = JournalTools.determineType(anchor);
        if (!type.equals("collection"))
            output.addContent(new Element("error").setText("noanchorGiven"));
        else {
            JSONObject json = new JSONObject();
            JSONArray series = new JSONArray();

            int year = LocalDate.now().getYear();
            try {
                year = Integer.parseInt(getParameter(job, "year"));
            } catch (Exception e) {
            }
            long totalUsage = 0;
            double price = 0.0;

            output.setAttribute("year", String.valueOf(year));
            JSONObject data = new JSONObject();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
            EntityManager em = emf.createEntityManager();

            JournalCollection collection = JournalCollectionDAO.getCollection(em, anchor, year);
            if (collection == null) {
                List<JournalCollection> collections = JournalCollectionDAO.getCollections(em, anchor);
                Collections.sort(collections);
                collection = collections.get(collections.size() - 1);
            }
            collection.addToOutput(output);
            List<String> issnsList = collection.getIssnsList();

            JSONArray issnListJSON = new JSONArray();
            Element journalTitles = new Element("journaltitles");
            for (String issn : issnsList) {
                JournalTitle journalTitle = JournalTitleDAO.getJournalTitle(em, issn, year);
                if (journalTitle == null)
                    continue;
                journalTitle.addToOutput(journalTitles);
                if (journalTitle.getType().equals("print"))
                    continue;
                long totalRequests = CounterDAO.getYearlyTotalRequests(issn, year, em);
                price += journalTitle.getPrice();
                if (totalRequests != 0) {
                    totalUsage += totalRequests;
                    JSONObject issnJSON = new JSONObject();
                    issnJSON.put("name", journalTitle.getName());
                    issnJSON.put("y", totalRequests);
                    issnListJSON.put(issnJSON);
                }
            }
            data.put("data", issnListJSON);

            data.put("name", "Anteil Zeitschriften");
            data.put("colorByPoint", true);
            series.put(data);
            json.put("series", series);

            Element subjectsXML = new Element("subjects");
            JSONArray subjectSeries = new JSONArray();
            JSONArray subjectListJSON = new JSONArray();
            JSONObject subjectData = new JSONObject();
            List<CollectionUsagePerSubject> cupss = CollectionUsagePerSubjectDAO.getCollectionUsagePerSubjects(anchor, year, em);
            for (CollectionUsagePerSubject cups : cupss) {
                JSONObject subjectJSON = new JSONObject();
                cups.addToOutput(subjectsXML);
                subjectJSON.put("name", cups.getSubject());
                subjectJSON.put("y", cups.getUsagePerSubject());
                subjectListJSON.put(subjectJSON);
            }
            subjectData.put("data", subjectListJSON);
            subjectData.put("name", "Anteil Fächer");
            subjectData.put("colorByPoint", true);
            subjectSeries.put(subjectData);
            Element jsonElement = new Element("json");
            Element jsonFractionJournals = new Element("fractionJournals");
            Element jsonUsagePerSubjects = new Element("fractionUsage");

            jsonFractionJournals.setText(series.toString());
            jsonUsagePerSubjects.setText(subjectSeries.toString());
            jsonElement.addContent(jsonFractionJournals);
            jsonElement.addContent(jsonUsagePerSubjects);
            output.addContent(jsonElement);
            output.addContent(subjectsXML);
            output.addContent(new Element("price").setText(String.valueOf(price)));
            output.addContent(new Element("totalUsage").setText(String.valueOf(totalUsage)));
            em.close();
        }
        sendOutput(job, output);
        LOGGER.info("done.");
    }
}
