/**
 * 
 */
package unidue.ub.statistics.frontend;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.eUsage.CounterDAO;
import unidue.ub.statistics.media.journal.Journal;
import unidue.ub.statistics.media.journal.JournalDAO;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;
import unidue.ub.statistics.media.journal.JournalTools;

/**
 * Servlet displaying information for one Journal (price, usage, SNIP)
 * @author Eike Spielberg
 *
 */
@WebServlet("/fachref/journals/journalMetrics")
public class JournalMetricsServlet extends FachRefServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(JournalMetricsServlet.class);

    /**
     * reads the necessary parameters from the http request and displays collects information about a particular journal.
     * 
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     */

    protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
        Element output = prepareOutput(job, "journalMetrics", "journals", "journalMetrics");

        String issn = getParameter(job, "issn");
        String type = JournalTools.determineType(issn);
        if (type.equals("collection")) {
            output.addContent(new Element("error").setText("noIssnsGiven"));
        } else {
            int year = LocalDate.now().getYear();
            try {
                year = Integer.parseInt(getParameter(job, "year"));
            } catch (Exception e) {
            }

            output.setAttribute("year", String.valueOf(year));
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
            EntityManager em = emf.createEntityManager();
            JournalTitle journalTitle = JournalTitleDAO.getJournalTitle(em, issn, year);
            String zdbID = journalTitle.getZDBID();
            List<JournalTitle> journalTitles = JournalTitleDAO.getJournalTitlesByZDBID(em, zdbID, year);
            LOGGER.info("retrieved ZDB ID : " + zdbID);
            Journal journal = JournalDAO.getJournalByZDBID(em, zdbID);
            journal.addToOutput(output);
            double price = 0.0;
            double priceCalculated = 0.0;
            double snip = 1.0;
            long totalUsage = 0;
            Element journalTitlesXML = new Element("journalTitles");
            for (JournalTitle journalTitleInd : journalTitles) {
                price += journalTitleInd.getPrice();
                priceCalculated += journalTitleInd.getCalculatedPrice();
                if (journalTitleInd.getSNIP() != 1.0)
                    snip = journalTitleInd.getSNIP();
                totalUsage += CounterDAO.getYearlyTotalRequests(journalTitleInd.getIssn(), year, em);
                journalTitleInd.addToOutput(journalTitlesXML);
            }
            output.addContent(journalTitlesXML);
            output.addContent(new Element("price").setText(String.valueOf(price)));
            output.addContent(new Element("priceCalculated").setText(String.valueOf(priceCalculated)));
            output.addContent(new Element("snip").setText(String.valueOf(snip)));
            output.addContent(new Element("totalUsage").setText(String.valueOf(totalUsage)));
        }
        sendOutput(job, output);
        LOGGER.info("done.");
    }
}
