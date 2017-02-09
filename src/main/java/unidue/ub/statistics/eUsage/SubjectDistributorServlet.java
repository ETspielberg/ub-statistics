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

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalTitle;

/**
 * Goes through all stored journal collections, takes the usage data and the prices and distributes them according to the subject categories.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/subjectDistributor")
@MultipartConfig
public class SubjectDistributorServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(SubjectDistributorServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * started by a HTTP request this servlet goes through all stored journal collections, takes the usage data and the prices and distributes them according to the subject categories.
     * the result is stored as a <code>CollectionUsagePerSubject</code>
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        List<String> anchors = JournalCollectionDAO.listCollections(em);
        for (String anchor : anchors) {
            analyzeAnchor(em,anchor);
        }
        em.close();
        LOGGER.info("done.");
        job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia");
    }
    
    private void analyzeAnchor(EntityManager em, String anchor) {
        List<JournalCollection> collections = JournalCollectionDAO.getCollections(em,anchor);
        for (JournalCollection collection : collections) {
            long totalUsage = 0;
            double totalPrice = 0.0;
            Hashtable<String, Integer> subjects = new Hashtable<String, Integer>();
            Hashtable<String, List<String>> journalsPerSubject = new Hashtable<String, List<String>>();
            List<JournalTitle> journalTitles = collection.getJournalsForYear(em,collection.getYear());
            totalPrice = collection.getPrice();
            if (journalTitles == null) {
                continue;
            }
            for (JournalTitle journalTitle : journalTitles) {
                if (journalTitle == null) {
                    continue;
                }
                if (journalTitle.getType().equals("print"))
                    continue;
                long totalRequests = CounterDAO.getYearlyTotalRequests(journalTitle.getIssn(), journalTitle.getYear(), em);
                totalUsage += totalRequests;
                List<String> subjectList = journalTitle.getSubjectList();
                for (String subjectInd : subjectList) {
                    int countPart = (int) (totalRequests / subjectList.size());
                    if (subjects.containsKey(subjectInd)) {
                        subjects.replace(subjectInd,subjects.get(subjectInd) + countPart);
                        List<String> issnsPerSubject = journalsPerSubject.get(subjectInd);
                        issnsPerSubject.add(journalTitle.getIssn());
                    } else {
                        subjects.put(subjectInd, (Integer) countPart);
                        List<String> issnsPerSubject = new ArrayList<String>();
                        issnsPerSubject.add(journalTitle.getIssn());
                        journalsPerSubject.put(subjectInd, issnsPerSubject);
                    }
                }
            }
            Enumeration<String> enumeration = subjects.keys();
            List<CollectionUsagePerSubject> cupss = new ArrayList<>();
            while (enumeration.hasMoreElements()) {
                String subjectInd = enumeration.nextElement();
                Integer usage = subjects.get(subjectInd);
                CollectionUsagePerSubject cups = new CollectionUsagePerSubject();
                cups.setCollection(anchor).setSubject(subjectInd).setUsagePerSubject(usage).setYear(collection.getYear()).setPricePerSubject(totalPrice * ((double) subjects.get(subjectInd)) / ((double) totalUsage));
                cupss.add(cups);
            }
            CollectionUsagePerSubjectDAO.deleteSingleCollectionUsagePerSubject(anchor, collection.getYear(), em);
            CollectionUsagePerSubjectDAO.persistCollectionUsagePerSubjects(cupss);
            for (JournalTitle journalTitle : journalTitles) {
                double priceCalculated = CounterDAO.getYearlyTotalRequests(journalTitle.getIssn(), journalTitle.getYear(), em) / totalUsage * totalPrice;
                journalTitle.setCalculatedPrice(priceCalculated);
            }
        }
    }
}
