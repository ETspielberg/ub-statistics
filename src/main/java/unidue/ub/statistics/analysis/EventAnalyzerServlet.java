package unidue.ub.statistics.analysis;

import org.apache.log4j.Logger;
import org.mycore.common.MCRMailer;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.transformer.MCRXSLTransformer;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.TriggerKey.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;

import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.admin.Notation;
import unidue.ub.statistics.admin.NotationDAO;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.DocumentGetter;
import unidue.ub.statistics.alephConnector.EventGetter;
import unidue.ub.statistics.alephConnector.ItemGetter;
import unidue.ub.statistics.alephConnector.MABGetter;
import unidue.ub.statistics.blacklist.IgnoredDAO;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.media.monographs.StockEventsBuilder;
import unidue.ub.statistics.media.monographs.Expression;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Performs the event analysis for given stock regions and persists it
 * afterwards.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eventAnalyzer")
public class EventAnalyzerServlet extends MCRServlet implements Job {

	private static final Logger LOGGER = Logger.getLogger(EventAnalyzerServlet.class);

	private static final long serialVersionUID = 1;

	private String who;

	private Set<Manifestation> documents = new HashSet<Manifestation>();
	
	private String stockControl;

	private StockControlProperties scp;

	private EntityManager em;
	
	private EntityManager emTools;
	
	/**
	 * execute method to be called by the quartz framework.
	 * 
	 * @param context
	 *            job execution context from the quartz framework
	 * 
	 */
	@Override
	public void execute(JobExecutionContext context) {
		JobDataMap dataMap = context.getMergedJobDataMap();
		em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
		emTools = Persistence.createEntityManagerFactory("tools").createEntityManager();
		try {
			stockControl = dataMap.getString("stockControl");
			who = dataMap.getString("author");
			scp = new StockControlProperties(stockControl,who);
			deleteOldEntries();
			runAnalysis();
			sendEmail();
		} catch (Exception e) {
			LOGGER.info("encountered error on running analysis");
		}
		em.close();
		emTools.close();
	}

	/**
	 * builds the <code>StockControlProperties</code> from the http request and
	 * performs the corresponding analysis.
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * 
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest req = job.getRequest();
		who = req.getUserPrincipal().getName();
		scp = StockControlProperties.buildSCPFromRequest(req);
		stockControl = scp.getStockControl();
		em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
		emTools = Persistence.createEntityManagerFactory("tools").createEntityManager();
		deleteOldEntries();
		runAnalysis();
		em.close();
		emTools.close();
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.unscheduleJob(triggerKey(stockControl, who));
		JobDetail jobDetail = JobBuilder.newJob(EventAnalyzerServlet.class).withIdentity(stockControl,who).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(stockControl,who).withSchedule(
             calendarIntervalSchedule()
            .withIntervalInMonths(6)).startNow().build();
        
        scheduler.scheduleJob(jobDetail,trigger);
        job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/profile");
	}

	private void deleteOldEntries() {
	    LOGGER.info("deleting old entries");
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<EventAnalysis> delete = cb.createCriteriaDelete(EventAnalysis.class);
		Root<EventAnalysis> c = delete.from(EventAnalysis.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(c.get("stockControl"), scp.getStockControl()));
		predicates.add(cb.equal(c.get("author"), who));
		delete.where(predicates.toArray(new Predicate[] {}));
		Query query = em.createQuery(delete);
		query.executeUpdate();
		tx.commit();
		LOGGER.info("done.");
	}

	private void runAnalysis() throws SQLException {
		List<Notation> notations = new ArrayList<>();
		

		if (!scp.getSCSystemCode().equals("")) {
		    LOGGER.info("retrieving system code " + scp.getSCSystemCode());
		    notations = NotationDAO.getNotationsList(scp.getSCSystemCode());
		} else if (!scp.getSCSubjectID().isEmpty()) {
		    LOGGER.info("retrieving subjectID " + scp.getSCSubjectID());
			notations = NotationDAO.getNotationsForSubjectID(scp.getSCSubjectID());
		}
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (Notation notation : notations) {
			scp.setSCSystemCode(notation.getNotation());
			persistEventAnalyses();
		}
		tx.commit();
	}

	private void persistEventAnalyses() throws SQLException {
		AlephConnection connection = new AlephConnection();
		ItemFilter filter = new ItemFilter(scp.getSCCollections(), scp.getSCMaterials());
		LOGGER.info(scp.getSCSystemCode() + " " + scp.getSCCollections() + " " + scp.getSCMaterials());
		DocumentGetter documentGetter = new DocumentGetter(connection);
		documents = documentGetter.getDocumentsByNotation(scp.getSCSystemCode());
		List<EventAnalysis> analyses = new ArrayList<>();
		if (documents.size() != 0) {
			for (Manifestation document : documents) {
			    // test whether the notation looked up is indeed present in the notations found. If not, i.e. if it is found in the secondary notations, skip the analysis.
			    //if (!document.getCallNo().contains(scp.getSCSystemCode()))
			    //    continue;
			    //test whether the document is listed in the ignored table. If it is, skip the analysis.
				if (IgnoredDAO.contains(document.getDocNumber(), emTools))
					continue;
				// check for empty documents
				connection.disconnect();
				connection = new AlephConnection();
				ItemEventCollector iec = new ItemEventCollector(connection, true, filter);
				EventGetter eventGetter = new EventGetter(connection, filter);
				MABGetter mabGetter = new MABGetter(connection);
				ItemGetter itemGetter = new ItemGetter(connection);
				try {
					iec.addItems(itemGetter, document);
				} catch (Exception e) {
                    LOGGER.info("could not collect items");
                    continue;
				}
                if (!document.getCallNo().contains(scp.getSCSystemCode()))
                    continue;   
                try {
					StockEventsBuilder.buildStockEvents(document);
					List<Event> stockEvents = document.getEvents();
					if (!stockEvents.isEmpty())
						eventGetter.addEvents(document);
				} catch (Exception e2) {
					LOGGER.info("could not collect events");
				} try {
					iec.addMAB(mabGetter, document);
				} catch (Exception e3) {
					LOGGER.info("could not collect MAB data");
				}
			}
			if (scp.isGroupedAnalysis()) {
				Hashtable<String, Expression> works = groupDocuments(documents);
				Enumeration<String> keys = works.keys();
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					if (IgnoredDAO.contains(key, emTools))
						continue;
					Expression work = works.get(key);
					if (work.getEvents().size() > 0) {
						EventAnalyzer ea = new EventAnalyzer(work, scp);
						EventAnalysis analysis = ea.getEventAnalysis();
						analysis.setShelfmark(work.getShelfmarkBase());
						analysis.setAuthor(who);
						try {
							MCRJDOMContent mab = new MCRJDOMContent(work.getMAB());
							MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
							String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
							if (mabText.length() == 0)
								LOGGER.info("no MAB data.");
							analysis.setMab(mabText);
						} catch (Exception e) {
							LOGGER.info("could not get MAB data.");
						}
						analyses.add(analysis);
					}
				}
			} else {
				for (Manifestation document : documents) {
				    if (IgnoredDAO.contains(document.getDocNumber(), emTools))
	                    continue;
					EventAnalyzer ea = new EventAnalyzer(document, scp);
					EventAnalysis analysis = ea.getEventAnalysis();
					analysis.setShelfmark(document.getCallNo());
					analysis.setAuthor(who);
					try {
						MCRJDOMContent mab = new MCRJDOMContent(document.getMAB());
						MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
						String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
						if (mabText.length() == 0)
							LOGGER.info("no MAB data.");
						analysis.setMab(mabText);
					} catch (Exception e) {
						LOGGER.info("could not get MAB data.");
					}
					analysis.setShelfmarkBase(document.getShelfmarkBase());
					analyses.add(analysis);
				}
			}
		}
		for (EventAnalysis analysis : analyses) {
		    em.persist(analysis);
		}
		connection.disconnect();
	}

	private Hashtable<String, Expression> groupDocuments(Set<Manifestation> documentsToGroup) {
		Hashtable<String, Expression> groupedDocuments = new Hashtable<String, Expression>();
		for (Manifestation document : documentsToGroup) {
			if (document.getEvents().size() > 0) {
				String shelfmarkBase = document.getShelfmarkBase();
				if (shelfmarkBase.equals(""))
					continue;
				if (groupedDocuments.containsKey(shelfmarkBase)) {
					Expression work = groupedDocuments.get(shelfmarkBase);
					work.addDocument(document);
					groupedDocuments.replace(shelfmarkBase, work);
				} else {
					Expression work = new Expression(shelfmarkBase);
					work.addDocument(document);
					work.setMAB(document.getMAB());
					groupedDocuments.put(shelfmarkBase, work);
				}
			}
		}
		return groupedDocuments;
	}
	
	private void sendEmail() {
		String text = "Hallo,\n\n Der FachRef-Assistent hat eine neue Liste fertig:\n \n";
		text = text + "services.ub.uni-due.de/ub-statistics/fachref/deletionAssistant?stockControl=" + stockControl + "\n\n";
		text = text + "MFG\n Der FachRef-Assistent";
		MCRMailer.send("FachRef-Assistent",who,"Neue Liste bereit", text);
	}
}
