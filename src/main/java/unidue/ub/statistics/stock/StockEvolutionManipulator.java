package unidue.ub.statistics.stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;

/**
 * Methods to build and manipulate stock evolution lists.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockEvolutionManipulator {

	private final static String stockDir;

	private final static String sackDir;

	private final static long startTime = 946704600000L;

	private static final Logger LOGGER = Logger.getLogger(StockEvolutionManipulator.class);

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		stockDir = config.getString("ub.statistics.stockDir");
		sackDir = config.getString("ub.statistics.sackDir");
	}

	/**
	 * searches for the corresponding result file in the stock directory and
	 * retrieves a list of <code>StockEvolution</code>-objects.
	 * 
	 * @param stelle
	 *            notation to be analyzed
	 * @return se list of stock evolution data
	 */
	public static List<StockEvolution> readFromDisk(String stelle) {
		List<StockEvolution> se = new ArrayList<>();
		String filename = stelle + ".csv";
		File dataFile = new File(stockDir, filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lines = line.split(",");
				if (!lines[0].startsWith("time")) {
					StockEvolution seInd = new StockEvolution(Long.parseLong(lines[0]), Integer.parseInt(lines[1]),
							lines[2], Double.parseDouble(lines[3]), stelle);
					se.add(seInd);
				}
			}
			br.close();
		} catch (Exception e) {
			LOGGER.info("csv file '" + filename + "' not found");
		}
		return se;
	}

	/**
	 * transforms a list of stock evolution data into a JSON object.
	 * 
	 * @param se
	 *            list of stock evolution data
	 * @return json JSON Object holding the data
	 */
	public static JSONObject convertTOJSON(List<StockEvolution> se) {
		JSONObject json = new JSONObject();
		JSONArray dataTimeAndCountValues = new JSONArray();
		JSONArray dataTimeAndPriceValues = new JSONArray();
		int count = 0;
		for (StockEvolution seInd : se) {
			if (seInd.getPrice() != 0) {
				dataTimeAndPriceValues.put(new JSONArray().put(seInd.getTime()).put(seInd.getPrice()));
			}
			count = count + seInd.getCounter();
			dataTimeAndCountValues.put(new JSONArray().put(seInd.getTime()).put(count));
		}
		json.put("TimeAndPrice", dataTimeAndPriceValues);
		json.put("TimeAndCount", dataTimeAndCountValues);
		return json;
	}

	/**
	 * transforms a list of stock evolution data with a stock analysis control
	 * key into an org.jdom2.element with a csv-file embedded.
	 * 
	 * @param se
	 *            list of stock evolution data
	 * @param sack
	 *            stock analysis control key
	 * @return sackXML org.jdom2.element holding the data as csv
	 * @throws IOException exception while reading file
	 * @throws JDOMException exception while parsing file to JDOM object
	 */
	public static Element createCSVElement(List<StockEvolution> se, String sack) throws JDOMException, IOException {
		Element sackXML;
		if (sack.startsWith("session"))
			sackXML = (Element) MCRSessionMgr.getCurrentSession().get(sack);
		else {
			File sackFile = new File(sackDir, "SACK_" + sack + ".xml");
			sackXML = new SAXBuilder().build(sackFile).detachRootElement().clone().getChild("stockAnalysisFactors");
		}

		Element data = new Element("csvData");
		data.setAttribute("typeOfAnalysis", sackXML.getAttributeValue("name"));

		List<Element> categories = sackXML.getChildren();
		String csvHeader = "'time";
		List<String> filters = new ArrayList<>();
		for (Element category : categories) {
			csvHeader = csvHeader + "," + category.getAttributeValue("name");
			filters.add(category.getValue());
		}
		csvHeader = csvHeader + ", NA \\n";
		filters.add("NA");

		int[] counter = new int[categories.size() + 1];
		String csv = csvHeader;
		for (StockEvolution seInd : se) {
			if (seInd.getTime() >= startTime)
				csv = csv + seInd.getTime();
			else
				csv = csv + startTime;
			for (int i = 0; i < filters.size(); i++) {
				if (sack.startsWith("session") && filters.get(i).contains(seInd.getSystemCode()))
					counter[i] = counter[i] + seInd.getCounter();
				else if (!sack.startsWith("session") && filters.get(i).contains(seInd.getCollection()))
					counter[i] = counter[i] + seInd.getCounter();
				csv = csv + "," + counter[i];
			}
			csv = csv + "\\n";
		}
		csv = csv + "'";
		Element timeAndCount = new Element("timeAndCount");
		timeAndCount.addContent(csv);
		data.addContent(timeAndCount);

		csv = csvHeader;
		for (StockEvolution seInd : se) {
			String csvInd = "";
			if (seInd.getTime() >= startTime)
				csvInd = csvInd + seInd.getTime();
			else
				csvInd = csvInd + startTime;
			boolean include = false;
			for (int i = 0; i < filters.size(); i++) {
				String price = "";
				if (sack.startsWith("session") && filters.get(i).contains(seInd.getSystemCode())
						&& seInd.getCounter() > 0 && seInd.getPrice() != 0) {
					price = String.valueOf(seInd.getPrice());
					include = true;
				} else if (!sack.startsWith("session") && filters.get(i).contains(seInd.getCollection())
						&& seInd.getCounter() > 0 && seInd.getPrice() != 0) {
					price = String.valueOf(seInd.getPrice());
					include = true;
				}
				csvInd = csvInd + "," + price;
			}
			if (include)
				csv = csv + csvInd + "\\n";
		}
		csv = csv + "'";
		Element timeAndPrice = new Element("timeAndPrice");
		timeAndPrice.addContent(csv);
		data.addContent(timeAndPrice);
		return data;
	}

	/**
	 * filters a list of stock evolution data with according to a stock analysis
	 * control key.
	 * 
	 * @param se
	 *            list of stock evolution data
	 * @param sack
	 *            stock analysis control key
	 * @return filteredSe hash table containing the individual groups
	 * @throws IOException exception while reading file
	 * @throws JDOMException exception while parsing file to JDOM object
	 */
	public static Hashtable<String, List<StockEvolution>> filterSEList(List<StockEvolution> se, String sack)
			throws JDOMException, IOException {
		Hashtable<String, List<StockEvolution>> filteredSe = new Hashtable<String, List<StockEvolution>>();
		Element sackXML;
		if (sack.startsWith("session"))
			sackXML = (Element) MCRSessionMgr.getCurrentSession().get(sack);
		else {
			File sackFile = new File(sackDir, "SACK_" + sack + ".xml");
			sackXML = new SAXBuilder().build(sackFile).detachRootElement().clone().getChild("stockAnalysisFactors");
		}
		List<Element> categories = sackXML.getChildren();
		for (Element category : categories) {
			ArrayList<StockEvolution> seCat = new ArrayList<>();
			String filter = category.getValue();
			LOGGER.info("creating Hash for category: " + category.getAttributeValue("name"));
			for (StockEvolution seInd : se) {
				if (sack.startsWith("session")) {
					if (filter.contains(seInd.getSystemCode()))
						seCat.add(seInd);
				} else {
					if (filter.contains(seInd.getCollection()))
						seCat.add(seInd);
				}
			}
			filteredSe.put(category.getAttributeValue("name"), seCat);
		}
		LOGGER.info("filtered StockEvolution: ");
		return filteredSe;
	}

}
