package unidue.ub.statistics.stock;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.*;

import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.DocumentGetter;
import unidue.ub.statistics.alephConnector.ItemGetter;
import unidue.ub.statistics.alephConnector.MABGetter;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.media.monographs.Item;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Prepares analysis with respect to time, counter, price and collection.
 * 
 * @author Eike Spielberg
 *
 **/
@WebServlet("/fachref/stock/stockAnalyzer")
public class StockAnalyzerServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(StockAnalyzerServlet.class);

	private final static String stockDir;

	private Set<Manifestation> documents = new HashSet<Manifestation>();

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		stockDir = config.getString("ub.statistics.stockDir");
	}

	/**
	 * reads the necessary parameters from http-request and performs the
	 * analysis
	 * 
	 * @author Eike Spielberg
	 *
	 * @param job
	 *            <code>MCRServletJob</code>
	 **/
	protected void doGetPost(MCRServletJob job) throws Exception {

		HttpServletRequest req = job.getRequest();
		StockControlProperties scp = StockControlProperties.buildSCPFromRequest(req);

		if (!scp.getSCSystemCode().isEmpty()) {
			buildResultsFile(scp);
		} else if (!scp.getSCSubjectID().isEmpty()) {
			String filename = "systematik" + scp.getSCSubjectID() + ".xml";
			Element current = GHBPersistence.loadFile(filename);
			List<String> stellen = new ArrayList<>();
			Iterator<Element> processDescendants = current.getDescendants(new ElementFilter());
			while (processDescendants.hasNext()) {
				Element e = processDescendants.next();
				String currentName = e.getName();
				if (currentName.equals("stelle"))
					stellen.add(e.getAttributeValue("code"));
			}
			for (String stelle : stellen) {

				scp.setSCSystemCode(stelle);
				buildResultsFile(scp);
			}
		}
		job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL()+"fachref/stock");
	}

	private void buildResultsFile(StockControlProperties scp) throws Exception {
		ItemFilter filter = new ItemFilter(scp.getSCCollections(), scp.getSCMaterials());

		LOGGER.info(scp.getSCCollections() + scp.getSCMaterials());
		List<StockEvolution> stockEvolution = new ArrayList<>();
		List<Item> allItems = new ArrayList<>();
		AlephConnection connection = new AlephConnection();
		ItemGetter itemGetter = new ItemGetter(connection);
		MABGetter mabGetter = new MABGetter(connection);
		DocumentGetter documentGetter = new DocumentGetter(connection);
		documents = documentGetter.getDocumentsByNotation(scp.getSCSystemCode());

		for (Manifestation document : documents) {
			ItemEventCollector iec = new ItemEventCollector(connection, true, filter);
			iec.addMAB(mabGetter, document);
			iec.addItems(itemGetter, document);
			allItems.addAll(document.getItems());
		}
		for (Item item : allItems) {
			List<Event> events = item.getEvents();
			double price = getPriceDouble(item);
			for (Event event : events) {
				if (event.getType().equals("inventory")) {
					stockEvolution.add(new StockEvolution(event.getTime(), +1,
							item.getCollection().replace("???", "NA"), price, scp.getSCSystemCode()));
				} else if (event.getType().equals("deletion")) {
					stockEvolution.add(new StockEvolution(event.getTime(), -1,
							item.getCollection().replace("???", "NA"), price, scp.getSCSystemCode()));
				}
			}
		}
		Collections.sort(stockEvolution);
		writeCSV(stockEvolution, scp.getSCSystemCode());
	}

	private void writeCSV(List<StockEvolution> stockEvolution, String systemCode) throws IOException {
		File filename = new File(stockDir, systemCode + ".csv");
		try {
			FileWriter writer = new FileWriter(filename);
			writer.write("time,counter,collection,price \n");
			for (StockEvolution se : stockEvolution) {
				writer.write(
						se.getTime() + "," + se.getCounter() + "," + se.getCollection() + "," + se.getPrice() + " \n");
			}
			writer.close();
		} catch (Exception e) {
		}
	}

	private double getPriceDouble(Item item) {
		if (item.getPrice() != null) {
			Double priceDouble;
			String price = item.getPrice().trim().replace(",", ".");
			if (price.length() > 2) {
				if (price.substring(0, 2).equals("DM")) {
					String priceDM = price.substring(2, price.indexOf("."));
					priceDouble = Double.parseDouble(priceDM) / 1.95583;
				} else {
					priceDouble = Double.parseDouble(price.substring(0, price.indexOf(".")));
				}
			} else if (price.indexOf(".") != 1) {
				priceDouble = Double.parseDouble(price.substring(0, price.indexOf(".")));
			} else
				priceDouble = Double.parseDouble(price);
			return priceDouble;
		} else
			return 0;
	}

}
