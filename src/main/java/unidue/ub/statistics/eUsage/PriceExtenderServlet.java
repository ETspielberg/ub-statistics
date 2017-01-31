package unidue.ub.statistics.eUsage;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.JournalPriceGetter;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;

/**
 * Collects the prices for stored journal titles.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/priceExtender")
@MultipartConfig
public class PriceExtenderServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(PriceExtenderServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the year from the http request, and collects the price data and saves them into the database. If no year is provided, a general initialization is run and the prices are collected for all stored journal titles and collections
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        String yearString = getParameter(job, "year");
        AlephConnection connection = new AlephConnection();
        JournalPriceGetter getter = new JournalPriceGetter(connection);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        List<String> issns = JournalTitleDAO.listAllIssns();
        if (yearString.isEmpty()) {
            for (String issn : issns) {
                LOGGER.info("retrieving price for ISSN " + issn);
                em.getTransaction().begin();
                List<JournalTitle> journalTitles = JournalTitleDAO.getJournalTitlesByIssn(em, issn);
                Hashtable<Integer, Double> prices = getter.getJournalPrice(issn);
                if (journalTitles.size() != 0) {
                    for (JournalTitle journalTitle : journalTitles) {
                        int yearOfPrice = journalTitle.getYear();
                        if (prices.containsKey(yearOfPrice)) {
                            journalTitle.setPrice(prices.get(yearOfPrice));
                        }
                    }
                }
                em.getTransaction().commit();
            }
            List<JournalCollection> collections = JournalCollectionDAO.getCollections(em);
            for (JournalCollection collection : collections) {
                List<JournalCollection> pricedJournalCollections = new ArrayList<>();
                Hashtable<Integer,Double> collectionPrices = getter.getCollectionPrice(collection);
                if (collectionPrices != null) {
                    Enumeration<Integer> enumerator = collectionPrices.keys();
                    em.getTransaction().begin();
                    while (enumerator.hasMoreElements()) {
                        Integer year = enumerator.nextElement();
                        if (collection.getYear() == year)
                            collection.setPrice(collectionPrices.get(year));
                        else
                            pricedJournalCollections.add(collection.clone().setPrice(collectionPrices.get(year)));
                    }
                    em.getTransaction().commit();
                    JournalCollectionDAO.persistCollections(em, pricedJournalCollections);
                }
                
            }
        } else {
            try {
                int year = Integer.parseInt(yearString);
                for (String issn : issns) {
                    em.getTransaction().begin();
                    JournalTitle journalTitle = JournalTitleDAO.getJournalTitle(em, issn, year);
                    Hashtable<Integer, Double> prices = getter.getJournalPrice(issn);
                    if (prices.containsKey(year))
                        journalTitle.setPrice(prices.get(year));
                    em.getTransaction().commit();
                }
                List<JournalCollection> collections = JournalCollectionDAO.getCollections(em, year);
                em.getTransaction().begin();
                for (JournalCollection collection : collections) {
                    Hashtable<Integer,Double> prices = getter.getCollectionPrice(collection);
                    if (prices.containsKey(year))
                        collection.setPrice(prices.get(year));
                }
                em.getTransaction().commit();
            } catch (Exception e) {

            }
        }
        em.close();
        connection.disconnect();
        job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia");
    }
}
