package unidue.ub.statistics.eUsage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRSourceContent;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;
import unidue.ub.statistics.media.journal.JournalTools;

/**
 * Collects the coverage data from Journal Online and Print (JOP) and extends journal titles and journal collections.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/yearExtender")
@MultipartConfig
public class YearExtenderServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(YearExtenderServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * collects the intialized data from the database and uses the coverage data from Journal Online und Print to extend them across the whole coverage range.
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        List<String> issns = JournalTitleDAO.listAllIssns(em);
        for (String issn : issns) {
            List<JournalTitle> titles = JournalTitleDAO.getJournalTitlesByIssn(em, issn);
            if (titles.size() == 1) {
                Set<Integer> yearsFound = new HashSet<Integer>();
                List<JournalTitle> newJournalTitles = new ArrayList<>();
                JournalTitle journalTitle = titles.get(0).clone();
                int yearFromDatabase = journalTitle.getYear();
                String uri = "jop:genre=journal&sid=bib:ughe&pid=bibid%3DUGHE&issn=" + issn;
                MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/ezb2periods.xsl");
                MCRContent jopResponse = MCRSourceContent.getInstance(uri);
                Element data = transformer.transform(jopResponse).asXML().detachRootElement().clone();
                List<Element> sourcesElectronic = new ArrayList<>();
                List<Element> sourcesPrint = new ArrayList<>();
                boolean electronic = true;
                try {
                    sourcesElectronic = data.getChild("electronic").getChild("sources").getChildren("source");
                } catch (Exception e1) {
                    electronic = false;
                }
                boolean print = true;
                try {
                    sourcesPrint = data.getChild("print").getChild("sources").getChildren("source");
                } catch (Exception e2) {
                    print = false;
                }
                if (electronic) {
                    for (Element source : sourcesElectronic) {
                        try {
                            Set<Integer> years = JournalTools.getAvailableYears(source.getChild("period").getText());
                            Iterator<Integer> iterator = years.iterator();
                            while (iterator.hasNext()) {
                                int year = iterator.next();
                                if (year != yearFromDatabase && year >= 2000 && !yearsFound.contains(year)) {
                                    newJournalTitles.add(journalTitle.clone().setYear(year));
                                    yearsFound.add(year);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.info("could not read electronic range");
                        }
                    }
                }
                if (print) {
                    for (Element source : sourcesPrint) {
                        try {
                            Set<Integer> years = JournalTools.getAvailableYears(source.getChild("period").getText());
                            Iterator<Integer> iterator = years.iterator();
                            while (iterator.hasNext()) {
                                int year = iterator.next();
                                if (year != yearFromDatabase && year >= 2000)
                                    newJournalTitles.add(journalTitle.clone().setYear(year));
                            }
                        } catch (Exception e) {
                            LOGGER.info("could not read electronic range");
                        }
                    }
                }
                JournalTitleDAO.deletePackage(em, issn);
                JournalTitleDAO.persistJournals(newJournalTitles);
            }
        }
        em.close();
        job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia");
    }
}
