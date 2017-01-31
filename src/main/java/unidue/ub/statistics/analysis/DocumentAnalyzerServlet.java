package unidue.ub.statistics.analysis;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.admin.Notation;
import unidue.ub.statistics.admin.NotationDAO;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.DocumentGetter;
import unidue.ub.statistics.alephConnector.EventGetter;
import unidue.ub.statistics.alephConnector.ItemGetter;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.StockEventsBuilder;
import unidue.ub.statistics.stockcontrol.StockControlCache;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Performs the document analysis for given stock regions and persists it
 * afterwards.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/documentAnalyzer")
public class DocumentAnalyzerServlet extends MCRServlet implements Job {

	private static final LocalDate TODAY = LocalDate.now();

	private static final Logger LOGGER = Logger.getLogger(DocumentAnalyzerServlet.class);

	private static final long serialVersionUID = 1;

	/**
	 * execute method to be called by the quartz framework.
	 * 
	 * @param context
	 *            job execution context from the quartz framework
	 * 
	 */
	@Override
	public void execute(JobExecutionContext context) {
		try {
			updateAnalysis();
		} catch (Exception e) {
			LOGGER.info("encountered error on running analysis");
		}
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
		StockControlProperties scp = StockControlProperties.buildSCPFromRequest(req);
		StockControlCache.store(scp);
		runAnalysis(scp);
		job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/profile");
	}

	private void runAnalysis(StockControlProperties scp) throws Exception {
		List<Notation> notations = new ArrayList<>();

		EntityManager em = Persistence.createEntityManagerFactory("documentAnalysis").createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		if (!scp.getSCSystemCode().isEmpty())
			notations = NotationDAO.getNotationsList(scp.getSCSystemCode());
		else if (!scp.getSCSubjectID().isEmpty())
			notations = NotationDAO.getNotationsList(scp.getSCSubjectID());

		for (Notation notation : notations) {
			scp.setSCSystemCode(notation.getNotation());
			persistDocumentAnalyses(em, scp);
		}
		tx.commit();
		em.close();

	}

	private void persistDocumentAnalyses(EntityManager em, StockControlProperties scp) throws Exception {
		Set<Manifestation> documents = getDocuments(scp.getSCSystemCode());
		ItemFilter filter = new ItemFilter(scp.getSCCollections(), scp.getSCMaterials());
		if (documents.size() != 0) {
			for (Manifestation document : documents) {
				Hashtable<Integer, DocumentAnalysis> analyses = runDocumentAnalyses(document, filter);
				if (analyses == null)
					continue;
				else {
					Enumeration<DocumentAnalysis> enumerator = analyses.elements();
					while (enumerator.hasMoreElements()) {
						DocumentAnalysis analysis = enumerator.nextElement();
						analysis.setComment(scp.getStockControl());
						em.persist(analysis);
					}
				}
			}
		}
	}

	private Hashtable<Integer, DocumentAnalysis> runDocumentAnalyses(Manifestation document, ItemFilter filter)
			throws Exception {
		AlephConnection connection = new AlephConnection();
		ItemGetter itemGetter = new ItemGetter(connection);
		ItemEventCollector iec = new ItemEventCollector(connection, true, filter);
		iec.addItems(itemGetter, document);
		StockEventsBuilder.buildStockEvents(document);
		if (document.getEvents().isEmpty()) {
			connection.disconnect();
			return null;
		} else {
			EventGetter eventGetter = new EventGetter(connection, filter);
			eventGetter.addEvents(document);
			DocumentAnalyzer da = new DocumentAnalyzer(document);
			Hashtable<Integer, DocumentAnalysis> analyses = da.getAnalyses();
			connection.disconnect();
			return analyses;
		}

	}

	private Set<Manifestation> getDocuments(String notation) throws SQLException {
		AlephConnection connection = new AlephConnection();
		DocumentGetter documentGetter = new DocumentGetter(connection);
		Set<Manifestation> documents = documentGetter.getDocumentsByNotation(notation);
		connection.disconnect();
		return documents;
	}

	/**
	 * updates the analyses for the whole stock	 
	 * 
	 * @exception Exception thrown if one of the things that can go wrong goes wrong ...
	 */
	public void updateAnalysis() throws Exception {
		ItemFilter filter = new ItemFilter("", "");
		List<Notation> notations = NotationDAO.getNotationsList("AAA-ZZZ");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("documentAnalysis");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentAnalysis> q = cb.createQuery(DocumentAnalysis.class);
		Root<DocumentAnalysis> c = q.from(DocumentAnalysis.class);
		for (Notation notation : notations) {
			Set<Manifestation> documents = getDocuments(notation.getNotation());
			for (Manifestation document : documents) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(c.get("description"), document.getDocNumber()));
				predicates.add(cb.equal(c.get("year"), TODAY.getYear()));
				q.select(c).where(predicates.toArray(new Predicate[] {}));
				List<DocumentAnalysis> analyses = em.createQuery(q).getResultList();
				if (analyses.isEmpty()) {
					predicates = new ArrayList<Predicate>();
					predicates.add(cb.equal(c.get("description"), document.getDocNumber()));
					predicates.add(cb.equal(c.get("year"), TODAY.getYear() - 1));
					q.select(c).where(predicates.toArray(new Predicate[] {}));
					analyses = em.createQuery(q).getResultList();
				}
				if (analyses.isEmpty()) {
					Hashtable<Integer, DocumentAnalysis> newAnalyses = runDocumentAnalyses(document, filter);
					if (analyses != null) {
						Enumeration<DocumentAnalysis> enumerator = newAnalyses.elements();
						while (enumerator.hasMoreElements()) {
							DocumentAnalysis analysis = enumerator.nextElement();
							em.persist(analysis);
						}
					}
				} else {
					AlephConnection connection = new AlephConnection();
					ItemGetter itemGetter = new ItemGetter(connection);
					ItemEventCollector iec = new ItemEventCollector(connection, true, filter);
					iec.addItems(itemGetter, document);
					StockEventsBuilder.buildStockEvents(document);
					if (document.getEvents().isEmpty()) {
						connection.disconnect();
					} else {
						/*
						EventGetter eventGetter = new EventGetter(connection, filter);
						eventGetter.addNewEvents(document,timestamp);
						DocumentAnalyzer da = new DocumentAnalyzer(document);
						Hashtable<Integer, DocumentAnalysis> NewAnalyses = da.updateAnalyses(analyses);
						connection.disconnect();
						*/
					}
				}
			}
		}

	}
}
