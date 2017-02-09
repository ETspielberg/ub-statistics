package unidue.ub.statistics.stock;

import org.mycore.frontend.servlets.*;

import unidue.ub.statistics.analysis.DocumentAnalysis;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Servlet class to retrieve and prepare DocumentAnalysis for display. Fetches
 * the desired analyses from the database, sums up according to years and
 * different notations and builds the JSON node in the output xml document.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockUsageAnalysisServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(StockUsageAnalysisServlet.class);

	private String collections = "";

	private String materials = "";

	private String id;

	private Hashtable<String, DocumentAnalysis> stats;

	private HashSet<String> works;

	private EntityManager em;

	private Hashtable<String, Integer> years = new Hashtable<String, Integer>();

	/**
	 * reads the necessary parameters (start and end year, stock control
	 * properties) from the http request and retrieves the
	 * <code>DocumentAnalysis</code> objects from the database. The results are
	 * assembled into an xml file, which is displayed as web page by XSLT
	 * transformations.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
	    Element output = prepareOutput(job,"stockUsageAnalysis","stock");
	    
		em = Persistence.createEntityManagerFactory("documentAnalysis").createEntityManager();
		works = new HashSet<String>();
		stats = new Hashtable<String, DocumentAnalysis>();
		
		String startYear = getParameter(job, "startYear");
		if (startYear.isEmpty())
			startYear = "2000";
		String endYear = getParameter(job, "endYear");
		if (endYear.isEmpty())
			endYear = "2016";

		years.put("start", Integer.parseInt(startYear));
		years.put("end", Integer.parseInt(endYear));
		Element topLevel = new Element("topLevel");
		Element json = new Element("json");

		StockControlProperties scp = StockControlProperties.buildSCPFromRequest(job.getRequest());

		String path = job.getRequest().getPathInfo();
		String link = "";

		Element current = new Element("analyzedStellen");
		List<Element> children = new ArrayList<>();
		Hashtable<String, String> notations = new Hashtable<String, String>();

		try {
			StringTokenizer tok = new StringTokenizer(path, "/");

			id = tok.nextToken();
			String filename = "systematik-" + id + ".xml";
			current = GHBPersistence.loadFile(filename);
			while (tok.hasMoreTokens()) {
				id = tok.nextToken();
				current = getChild(current, id);
			}
			if (current.getName().equals("stelle")) {
				topLevel.addContent(new Element("range").addContent(id));
				topLevel.addContent(new Element("bez").addContent(current.getChildText("bez")));
				notations.put("von", id);
				List<DocumentAnalysis> statsFromDB = getDocumentAnalysesFromDatabase(scp.getStockControl(), notations,
						years);
				if (statsFromDB != null) {
					List<DocumentAnalysis> statistics = reduce(statsFromDB);
					for (DocumentAnalysis da : statistics) {
						String reducedShelfmark = "";
						if (da.getShelfmark().contains("("))
							reducedShelfmark = deleteEditionFromShelfmark(da.getShelfmark());
						else
							reducedShelfmark = da.getShelfmark();
						if (!works.contains(reducedShelfmark))
							works.add(reducedShelfmark);
						stats.put(da.getShelfmark(), da);
					}
					link = job.getRequest().getServletPath().toString().replace("fachref/stockUsage",
							"ausleihprotokoll");
				}
			} else if (current.getName().equals("systematik") || current.getName().equals("gruppe")) {
				topLevel.addContent(new Element("range").addContent(id));
				topLevel.addContent(new Element("bez").addContent(current.getChildText("bez")));
				children = current.getChildren();
				for (Element child : children) {
					if (child.getName().equals("stelle")) {
						notations.put("von", child.getAttributeValue("code"));
						List<DocumentAnalysis> statistics = getDocumentAnalysesFromDatabase(scp.getStockControl(),
								notations, years);
						if (statistics == null)
							continue;
						else {
							for (DocumentAnalysis da : statistics) {
								String reducedShelfmark = "";
								if (da.getShelfmark().contains("("))
									reducedShelfmark = deleteEditionFromShelfmark(da.getShelfmark());
								else
									reducedShelfmark = da.getShelfmark();
								if (!works.contains(reducedShelfmark))
									works.add(reducedShelfmark);
							}
							DocumentAnalysis combinedStats = new DocumentAnalysis();
							if (statistics.size() == 1)
								combinedStats = statistics.get(0);
							else
								combinedStats = combine(statistics);
							combinedStats.setShelfmark(child.getAttributeValue("code"));
							combinedStats.setComment(child.getChildText("bez"));
							stats.put(child.getChildText("bez"), combinedStats);
						}
					} else if (child.getName().equals("gruppe")) {
						notations.put("von", child.getAttributeValue("von"));
						notations.put("bis", child.getAttributeValue("bis"));
						List<DocumentAnalysis> statistics = getDocumentAnalysesFromDatabase(scp.getStockControl(),
								notations, years);
						if (statistics == null)
							continue;
						for (DocumentAnalysis da : statistics) {
							String reducedShelfmark = "";
							if (da.getShelfmark().contains("("))
								reducedShelfmark = deleteEditionFromShelfmark(da.getShelfmark());
							else
								reducedShelfmark = da.getShelfmark();
							if (!works.contains(reducedShelfmark))
								works.add(reducedShelfmark);
						}

						DocumentAnalysis combinedStats = new DocumentAnalysis();
						if (statistics.size() == 1)
							combinedStats = statistics.get(0);
						else
							combinedStats = combine(statistics);
						combinedStats
								.setShelfmark(child.getAttributeValue("von") + "-" + child.getAttributeValue("bis"));
						combinedStats.setComment(child.getChildText("bez"));
						stats.put(child.getChildText("bez"), combinedStats);
					}
				}
				link = job.getRequest().getRequestURL().toString();
			}
		} catch (Exception e) {
			List<String> filenames = GHBPersistence.listFiles();
			topLevel.addContent(new Element("range").addContent("AAA-ZZZ"));
			topLevel.addContent(new Element("bez").addContent("Komplettbestand"));
			for (String file : filenames) {
				current = GHBPersistence.loadFile(file);
				notations.put("von", current.getAttributeValue("von"));
				notations.put("bis", current.getAttributeValue("bis"));
				String subjectID = file.substring(file.indexOf("-") + 1, file.indexOf("."));

				List<DocumentAnalysis> statistics = getDocumentAnalysesFromDatabase(scp.getStockControl(), notations,
						years);
				if (statistics == null)
					continue;
				for (DocumentAnalysis da : statistics) {
					String reducedShelfmark = "";
					if (da.getShelfmark().contains("("))
						reducedShelfmark = deleteEditionFromShelfmark(da.getShelfmark());
					else
						reducedShelfmark = da.getShelfmark();
					if (!works.contains(reducedShelfmark))
						works.add(reducedShelfmark);
				}
				DocumentAnalysis combinedStats = combine(statistics);
				combinedStats.setShelfmark(subjectID);
				combinedStats.setComment(current.getChildText("bez"));
				stats.put(current.getChildText("bez"), combinedStats);
			}
			link = job.getRequest().getRequestURL().toString();
		}
		try {
		addJSONObject2Array(link, json);
		addOverallStats(topLevel);
		output.addContent(json);
		output.addContent(topLevel);
		} catch (Exception e2) {
			output.addContent(new Element("message").setText("noDataFound"));
		}
		em.close();
		sendOutput(job,output);
		LOGGER.info("Finished output.");
	}

	private static Element getChild(Element parent, String ID) {
		if (ID.indexOf('-') > 0) {
			List<Element> children = parent.getChildren("gruppe");
			for (int i = 0; i < children.size(); i++) {
				Element gruppe = (Element) (children.get(i));
				String range = gruppe.getAttributeValue("von") + "-" + gruppe.getAttributeValue("bis");
				if (ID.equals(range))
					return gruppe;
			}
		} else {
			List<Element> children = parent.getChildren("stelle");
			for (int i = 0; i < children.size(); i++) {
				Element stelle = (Element) (children.get(i));
				String code = stelle.getAttributeValue("code");
				if (ID.equals(code))
					return stelle;
			}
		}
		return null;
	}

	private List<DocumentAnalysis> getDocumentAnalysesFromDatabase(String stockControl,
			Hashtable<String, String> notations, Hashtable<String, Integer> yearRange) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentAnalysis> q = cb.createQuery(DocumentAnalysis.class);
		Root<DocumentAnalysis> c = q.from(DocumentAnalysis.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		// predicates.add(cb.equal(c.<String>get("comment"), stockControl));
		if (notations.size() == 1) {
			predicates.add(cb.equal(c.<String>get("notation"), notations.get("von")));
		} else {
			predicates.add(cb.between(c.<String>get("notation"), notations.get("von"), notations.get("bis")));
		}
		predicates.add(cb.between(c.<Integer>get("year"), yearRange.get("start"), yearRange.get("end")));
		q.select(c).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<DocumentAnalysis> query = em.createQuery(q);
		if (query.getResultList().size() == 0) {
			LOGGER.info("found no entries in database");
			return null;
		} else {
			List<DocumentAnalysis> analyses = query.getResultList();
			return analyses;
		}
	}

	// this is the part, which might be highly interesting for parallelization!
	private DocumentAnalysis combine(List<DocumentAnalysis> stats) {
		List<DocumentAnalysis> reduced = reduce(stats);
		DocumentAnalysis combined = new DocumentAnalysis();
		for (DocumentAnalysis da : reduced)
			combined.addEditionalDocumentAnalysis(da);
		return combined;
	}

	// this is the part, which might be highly interesting for parallelization
	private List<DocumentAnalysis> reduce(List<DocumentAnalysis> stats) {
		List<DocumentAnalysis> reduced = new ArrayList<DocumentAnalysis>();
		Collections.sort(stats);
		String shelfmark = stats.get(0).getShelfmark();
		DocumentAnalysis singleShelfmarkAnalysis = stats.get(0);
		for (int i = 1; i < stats.size(); i++) {
			if (stats.get(i).getShelfmark().equals(shelfmark))
				singleShelfmarkAnalysis.addYearlyDocumentAnalysis(stats.get(i));
			else {
				reduced.add(singleShelfmarkAnalysis);
				singleShelfmarkAnalysis = stats.get(i);
				shelfmark = stats.get(i).getShelfmark();
			}
		}
		return reduced;
	}

	private void addJSONObject2Array(String completePath, Element json) {
		JSONArray numberItemsTotal = new JSONArray();
		JSONArray numberItemsNonLendable = new JSONArray();
		JSONArray numberItemsDeleted = new JSONArray();
		JSONArray numberItemsLBS = new JSONArray();
		JSONArray numberItemsLendableNonLBS = new JSONArray();
		JSONArray daysStock = new JSONArray();
		JSONArray daysStockLBS = new JSONArray();
		JSONArray daysStockLendable = new JSONArray();
		JSONArray daysStockNonLendable = new JSONArray();
		JSONArray daysStockLendableNonLBS = new JSONArray();
		JSONArray daysLoanedStudents = new JSONArray();
		JSONArray daysLoanedElse = new JSONArray();
		JSONArray daysLoanedExtern = new JSONArray();
		JSONArray daysLoanedIntern = new JSONArray();
		JSONArray daysLoanedHapp = new JSONArray();
		JSONArray meanLoan = new JSONArray();
		JSONArray jsonStellen = new JSONArray();

		String link = "";
		Enumeration<String> keys = stats.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			jsonStellen.put(key);
			DocumentAnalysis da = stats.get(key);
			if (completePath.contains("ausleihprotokoll")) {
				if (key.contains(","))
					link = completePath + "?shelfmark=" + da.getShelfmark().substring(0, da.getShelfmark().indexOf(","))
							+ "&collections=" + collections + "&materials=" + materials;
				else
					link = completePath + "?shelfmark=" + da.getShelfmark() + "&collections=" + collections
							+ "&materials=" + materials;
			} else {
				link = completePath + "/" + da.getShelfmark() + "?collections=" + collections + "&materials="
						+ materials + "&startYear=" + years.get("start") + "&endYear" + years.get("end");
			}
			numberItemsTotal
					.put(new JSONObject().put("y", da.getLastStock()).put("link", link).put("description", key));
			numberItemsNonLendable
					.put(new JSONObject().put("y", da.getItemsNonLendable()).put("link", link).put("description", key));
			numberItemsLBS.put(new JSONObject().put("y", da.getItemsLBS()).put("link", link).put("description", key));
			numberItemsLendableNonLBS.put(
					new JSONObject().put("y", da.getItemsLendableNonLBS()).put("link", link).put("description", key));
			numberItemsDeleted
					.put(new JSONObject().put("y", da.getItemsDeleted()).put("link", link).put("description", key));
			daysStock.put(new JSONObject().put("y", da.getDaysStockAll()).put("link", link).put("description", key));
			daysStockLBS.put(new JSONObject().put("y", da.getDaysStockLBS()).put("link", link).put("description", key));
			daysStockLendable.put(
					new JSONObject().put("y", da.getDaysStockLendableAll()).put("link", link).put("description", key));
			daysStockLendableNonLBS.put(new JSONObject().put("y", da.getDaysStockLendableNonLBS()).put("link", link)
					.put("description", key));
			daysStockNonLendable.put(
					new JSONObject().put("y", da.getDaysStockNonLendable()).put("link", link).put("description", key));
			daysLoanedStudents.put(
					new JSONObject().put("y", da.getDaysLoanedStudents()).put("link", link).put("description", key));
			daysLoanedExtern
					.put(new JSONObject().put("y", da.getDaysLoanedExtern()).put("link", link).put("description", key));
			daysLoanedIntern
					.put(new JSONObject().put("y", da.getDaysLoanedIntern()).put("link", link).put("description", key));
			daysLoanedHapp
					.put(new JSONObject().put("y", da.getDaysLoanedHapp()).put("link", link).put("description", key));
			daysLoanedElse
					.put(new JSONObject().put("y", da.getDaysLoanedElse()).put("link", link).put("description", key));

			double meanRelativeLoanValue = 0.0;
			int daysStockLendableAllNumber = da.getDaysStockLBS() + da.getDaysStockLendableNonLBS();
			if (daysStockLendableAllNumber != 0)
				meanRelativeLoanValue = 100
						* ((double) (da.getDaysLoanedStudents() + da.getDaysLoanedExtern() + da.getDaysLoanedIntern()
								+ da.getDaysLoanedHapp() + da.getDaysLoanedElse()))
						/ ((double) daysStockLendableAllNumber);
			meanLoan.put(new JSONObject().put("y", meanRelativeLoanValue).put("link", link).put("description", key));
		}
		json.addContent(new Element("stellen").addContent(jsonStellen.toString()));
		json.addContent(new Element("numberItemsTotal").addContent(numberItemsTotal.toString()));
		json.addContent(new Element("numberItemsLBS").addContent(numberItemsLBS.toString()));
		json.addContent(new Element("numberItemsLendableNonLBS").addContent(numberItemsLendableNonLBS.toString()));
		json.addContent(new Element("numberItemsLendableNonLBS").addContent(numberItemsLendableNonLBS.toString()));
		json.addContent(new Element("numberItemsDeleted").addContent(numberItemsDeleted.toString()));
		json.addContent(new Element("daysStock").addContent(daysStock.toString()));
		json.addContent(new Element("daysStockLBS").addContent(daysStockLBS.toString()));
		json.addContent(new Element("daysStockLendable").addContent(daysStockLendable.toString()));
		json.addContent(new Element("daysStockLendableNonLBS").addContent(daysStockLendableNonLBS.toString()));
		json.addContent(new Element("daysStockNonLendable").addContent(daysStockNonLendable.toString()));
		json.addContent(new Element("daysLoanedStudents").addContent(daysLoanedStudents.toString()));
		json.addContent(new Element("daysLoanedExtern").addContent(daysLoanedExtern.toString()));
		json.addContent(new Element("daysLoanedIntern").addContent(daysLoanedIntern.toString()));
		json.addContent(new Element("daysLoanedHapp").addContent(daysLoanedHapp.toString()));
		json.addContent(new Element("daysLoanedElse").addContent(daysLoanedElse.toString()));
		json.addContent(new Element("meanRelativeLoan").addContent(meanLoan.toString()));
	}

	private void addOverallStats(Element parent) {
		List<DocumentAnalysis> daAll = new ArrayList<>();
		Enumeration<DocumentAnalysis> allDa = stats.elements();
		while (allDa.hasMoreElements()) {
			daAll.add(allDa.nextElement());
		}
		DocumentAnalysis da = combine(daAll);
		parent.addContent(new Element("dayStockLendableTotal")
				.addContent(String.valueOf(da.getDaysStockLBS() + da.getDaysStockLendableNonLBS())));
		parent.addContent(new Element("daysLoanedTotal")
				.addContent(String.valueOf(da.getDaysLoanedStudents() + da.getDaysLoanedExtern()
						+ da.getDaysLoanedIntern() + da.getDaysLoanedHapp() + da.getDaysLoanedElse())));
		parent.addContent(new Element("actualTotalNumberOfItems").addContent(String.valueOf(da.getLastStock())));
		parent.addContent(new Element("actualTotalNumberOfWorks").addContent(String.valueOf(works.size())));
	}

	private String deleteEditionFromShelfmark(String shelfmark) {
		String partTwo = "";
		int indexClosingBracket = shelfmark.indexOf(")");
		if (indexClosingBracket != shelfmark.length() - 1) {
			partTwo = shelfmark.substring(indexClosingBracket + 1);
		}
		return shelfmark.substring(0, shelfmark.indexOf("(")) + partTwo;
	}
}
