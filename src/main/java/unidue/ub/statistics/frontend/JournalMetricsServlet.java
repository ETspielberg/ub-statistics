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
import org.json.JSONArray;
import org.json.JSONObject;
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
 * 
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
	 * reads the necessary parameters from the http request and displays
	 * collects information about a particular journal.
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * @exception IOException
	 *                thrown upon the writing the result to the output
	 * @exception TransformerException
	 *                thrown upon rendering the output xml
	 * @exception SAXException
	 *                thrown upon building the output xml
	 */

	protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException {
		Element output = prepareOutput(job, "journalMetrics", "journals", "journalMetrics");

		String issn = getParameter(job, "issn");
		String type = JournalTools.determineType(issn);
		if (type.equals("collection")) {
			output.addContent(new Element("error").setText("noIssnsGiven"));
		} else {
			int thisYear = LocalDate.now().getYear();
			int years = 5;
			try {
				years = Integer.parseInt(getParameter(job, "years"));
			} catch (Exception e) {
			}

			output.setAttribute("years", String.valueOf(years));
			output.setAttribute("issn", String.valueOf(issn));
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
			EntityManager em = emf.createEntityManager();
			List<JournalTitle> initalJournalTitle = JournalTitleDAO.getJournalTitlesByIssn(em, issn);
			String zdbID = "";
			for (JournalTitle journalTitle : initalJournalTitle) {
				zdbID = journalTitle.getZDBID();
				if (!zdbID.isEmpty())
					break;
			}
			LOGGER.info("found ZDB ID " + zdbID);
			Journal journal = JournalDAO.getJournalByZDBID(em, zdbID);
			journal.addToOutput(output);
			JSONArray jsonYears = new JSONArray();
			JSONArray jsonSnips = new JSONArray();
			JSONArray jsonPrices = new JSONArray();
			JSONArray jsonPricesCalculated = new JSONArray();
			JSONArray jsonUsage = new JSONArray();
			Element journalTitlesXML = new Element("journalTitles");
			for (int year = thisYear - years; year <= thisYear; year++) {
				LOGGER.info("querying year " + year);
				List<JournalTitle> journalTitles = JournalTitleDAO.getJournalTitlesByZDBID(em, zdbID, year);
				double price = 0.0;
				double priceCalculated = 0.0;
				double snip = 1.0;
				long totalUsage = 0;
				Element journalTitleXML = new Element("journalTitlesPerYear");
				if (journalTitles != null) {
					LOGGER.info("found " + journalTitles.size() + " journal titles");
					for (JournalTitle journalTitleInd : journalTitles) {
						price += journalTitleInd.getPrice();
						priceCalculated += journalTitleInd.getCalculatedPrice();
						if (journalTitleInd.getSNIP() != 1.0)
							snip = journalTitleInd.getSNIP();
						totalUsage += CounterDAO.getYearlyTotalRequests(journalTitleInd.getIssn(), year, em);
						journalTitleInd.addToOutput(journalTitleXML);
					}
				}
				journalTitleXML.addContent(new Element("year").setText(String.valueOf(year)));
				jsonYears.put(year);
				journalTitleXML.addContent(new Element("price").setText(String.valueOf(price)));
				jsonPrices.put(price);
				journalTitleXML.addContent(new Element("priceCalculated").setText(String.valueOf(priceCalculated)));
				jsonPricesCalculated.put(priceCalculated);
				journalTitleXML.addContent(new Element("snip").setText(String.valueOf(snip)));
				jsonSnips.put(snip);
				journalTitleXML.addContent(new Element("totalUsage").setText(String.valueOf(totalUsage)));
				jsonUsage.put(totalUsage);
				journalTitlesXML.addContent(journalTitleXML);
			}
			Element json = new Element("json");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("categories", jsonYears);
			JSONObject snipSeries = new JSONObject().put("name", "SNIP").put("data", jsonSnips);
			JSONObject usageSeries = new JSONObject().put("name", "Nutzung").put("data", jsonUsage);
			JSONObject priceSeries = new JSONObject().put("name", "Preis").put("data", jsonPrices);
			JSONObject priceCalculatedSeries = new JSONObject().put("name", "Preis (berechnet)").put("data",
					jsonPricesCalculated);
			JSONArray jsonSeries = new JSONArray();
			jsonSeries.put(snipSeries).put(usageSeries).put(priceSeries).put(priceCalculatedSeries);
			jsonObject.put("series", jsonSeries);
			json.addContent(jsonObject.toString());
			output.addContent(json);
			output.addContent(journalTitlesXML);
		}
		sendOutput(job, output);
		LOGGER.info("done.");
	}
}
