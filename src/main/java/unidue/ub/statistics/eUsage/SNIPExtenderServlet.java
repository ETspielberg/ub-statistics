package unidue.ub.statistics.eUsage;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRSourceContent;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;

/**
 * Collects the SNIP data from Scopus (https://www.scopus.com) for stored journal titles.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/snipExtender")
@MultipartConfig
public class SNIPExtenderServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(SNIPExtenderServlet.class);

    private static final long serialVersionUID = 1;

    /**
     * reads the year from the http request, and collects the SNIP-data from Scopus and saves them into the database. if no year is provided, a general initialization is run and the SNIPs are collected for all stored journal titles and collections
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        String year = getParameter(job, "year");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        if (year.isEmpty()) {
            List<String> issns = JournalTitleDAO.listAllIssns(em);
            for (String issn : issns) {
                String uri = "journalMetric:" + issn;
                MCRContent scopusResponse = MCRSourceContent.getInstance(uri);
                try {
                    Element scopus = scopusResponse.asXML().detachRootElement().clone();
                    List<Element> snips = scopus.getChild("entry").getChild("SNIPList").getChildren("SNIP");
                    if (snips.size() > 1) {
                    for (Element snipElement : snips) {
                        int yearOfSnip = Integer.parseInt(snipElement.getAttributeValue("year"));
                        JournalTitle title = JournalTitleDAO.getJournalTitle(em,issn,yearOfSnip);
                        em.getTransaction().begin();
                        title.setSnip(Double.parseDouble(snipElement.getValue()));
                        em.getTransaction().commit();
                    }
                    } else if (snips.size() ==1) {
                        List<JournalTitle> titles = JournalTitleDAO.getJournalTitlesByIssn(em,issn);
                        em.getTransaction().begin();
                        for (JournalTitle title : titles)
                            title.setSnip(Double.parseDouble(snips.get(0).getValue()));
                        em.getTransaction().commit();
                    }
                } catch (Exception e) {
                    LOGGER.info("no SNIP data available");
                }
            }
        } else {
            //ToDo: add Update function
        }
        em.close();
        job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia");;
    }
}
