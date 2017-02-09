/**
 * 
 */
package unidue.ub.statistics.alert;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.mycore.common.MCRMailer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import unidue.ub.statistics.admin.Notation;
import unidue.ub.statistics.admin.NotationDAO;
import unidue.ub.statistics.analysis.NRequests;
import unidue.ub.statistics.analysis.NRequestsDAO;

/**
 * Sends the alerting emails
 * @author Spielberg
 *
 */
public class AlertSender implements Job {

    private static final Logger LOGGER = Logger.getLogger(AlertSender.class);

    private String who;

    private String alertControl;

    private AlertControl ac;

    private String text;

    private boolean send;

    /**
     * execute method to be called by the quartz framework.
     * 
     * @param context
     *            job execution context from the quartz framework
     * @exception JobExecutionException thrown, if the job cannot be started from the quartz framework
     * 
     */

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("preparing alerting service");
        JobDataMap dataMap = context.getMergedJobDataMap();
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NRequests> q = cb.createQuery(NRequests.class);
        Root<NRequests> c = q.from(NRequests.class);
        
        try {
            alertControl = dataMap.getString("alertControl");
            who = dataMap.getString("who");
            ac = new AlertControl().readFromDisk(alertControl, who);
            List<Notation> notations = NotationDAO.getNotationsList(ac.getNotationRange());
            List<NRequests> nRequests = new ArrayList<>();
            for (Notation notation : notations) {
                Predicate predicate = cb.like(c.<String> get("callNo"), notation.getNotation() + "%");
                q.select(c).where(predicate);
                nRequests.addAll(em.createQuery(q).getResultList());
            }
            if (ac.getPerformReader()){
                List<NRequests> reader = new ArrayList<>();
                for (NRequests nRequest : nRequests) {
                    if (nRequest.getRatio() >= ac.getThresholdQuotient()) {
                        NRequests readerEntry = nRequest.clone();
                        readerEntry.setForReader(true);
                        readerEntry.setAlertControl(alertControl);
                        reader.add(readerEntry);
                    }
                }
                NRequestsDAO.persistNRequests(reader);
                if (LocalDate.now().getDayOfMonth() == 1)
                    sendReader();
            }
            if (ac.getPerformAlert()) {
                send = false;
                for (NRequests nRequest : nRequests) {
                    text = "Hallo,\n\n laut FachRef-Assistent wurde der Schwellenwert für Vormerkungen überschritten:\n \n";
                    if (nRequest.getRatio() > ac.getThresholdQuotientAlert()) {
                        addToEmail(nRequest);
                        send = true;
                    }
                    text = text + "services.ub.uni-due.de/ub-statistics/fachref/nRequests?readerControl=" + ac + "\n\n";
                    text = text + "MFG\n Der FachRef-Assistent";
                }
                if (send) {
                    MCRMailer.send("eike.spielberg@uni-due.de", who, "Vormerk-Schwellenwert überschritten", text);
                    LOGGER.info("email sent.");
                }
            }
            
        } catch (Exception e) {
            LOGGER.info("Problem with sending the alert mail.");
        }
        em.close();
    }
    
    
    private void sendReader() {
        List<NRequests> nRequests = NRequestsDAO.getEventAnalyses(ac, 1);
        if (nRequests.size()>0) {
            String readerText = "Hallo,\n\n laut FachRef-Assistent wurde der Schwellenwert für Vormerkungen in den letzten 30 Tagen ";
            readerText += nRequests.size() + " mal überschritten:\n \n";
            readerText += "Signatur : Anzahl Vormerkungen : Anzahl ausleihbare Exemplare : Quotient \n";
            for (NRequests nRequest : nRequests) {
                readerText +=  nRequest.getCallNo() + " : " + nRequest.getNRequests() + " Vormerkungen auf " + nRequest.getNLendable() + " Exemplare. Quotient: " + nRequest.getRatio() + "\n";
            }
            readerText += "services.ub.uni-due.de/ub-statistics/fachref/nRequests?readerControl=" + ac + "\n\n";
            readerText +=  "MFG\n Der FachRef-Assistent";
            MCRMailer.send("eike.spielberg@uni-due.de", who, "Vormerkungen der Hitliste der letzten 30 Tage", readerText);
        }
    }

    private void addToEmail(NRequests nRequest) {
        if (!send)
            text = text + "Signatur : Anzahl Vormerkungen : Anzahl ausleihbare Exemplare : Quotient \n";
        text = text + nRequest.getCallNo() + " : " + nRequest.getNRequests() + " Vormerkungen auf " + nRequest.getNLendable() + " Exemplare. Quotient: " + nRequest.getRatio() + "\n";
    }
}
