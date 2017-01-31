package unidue.ub.statistics.stock;

import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.*;

import unidue.ub.statistics.frontend.FachRefServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Retrieves and displays the analysis of a set of documents as defined by a
 * <code>StockControlProperties</code> with respect to usage properties.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockAnalysisAssistantServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(StockAnalysisAssistantServlet.class);

	/**
	 * reads the necessary parameters from the http request and retrieves the
	 * stock analysis control keys objects from the corresponding file. The
	 * results are assembled into an xml file containing a number of JSON
	 * strings, which is displayed as web page by XSLT transformations.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		Element output = prepareOutput(job,"stockAnalysis");
		String sack = getParameter(job, "sack");
		String path = job.getRequest().getPathInfo();
		StringTokenizer tok = new StringTokenizer(path, "/");
		String id = tok.nextToken();
		String filename = "systematik-" + id + ".xml";
		Element current = GHBPersistence.loadFile(filename);
		while (tok.hasMoreTokens())
			current = getChild(current, tok.nextToken());

		// if no sack is given, create one from the grouping of systemCodes,
		// store it into the session
		// and reuse it to create the csv table.
		if (sack.isEmpty())
			sack = "session_" + current.getName();
		Element stockAnalysisFactors = new Element("stockAnalysisFactors");
		stockAnalysisFactors.setAttribute("name", "Systemstellen");
		// prepare the json for a single systemCode
		if (current.getName().equals("stelle")) {
			// prepare the sack with only on element
			Element stockAnalysisFactor = new Element("stockAnalysisFactors");
			stockAnalysisFactor.setAttribute("name", current.getAttributeValue("code"));
			stockAnalysisFactor.addContent(current.getAttributeValue("code"));
			stockAnalysisFactors.addContent(stockAnalysisFactor);

			// store it to the session
			MCRSessionMgr.getCurrentSession().put(sack, stockAnalysisFactors);

			// get the StockEvolution list
			List<StockEvolution> se = StockEvolutionManipulator.readFromDisk(current.getAttributeValue("code"));

			// create the csv table and attach it to the output.
			output.addContent(StockEvolutionManipulator.createCSVElement(se, sack));
		}

		// prepare the csv for a group of systemCodes and systemCode ranges.
		else {

			List<StockEvolution> se = new ArrayList<StockEvolution>();

			// get all members of the group of interest
			List<Element> children = current.getChildren();
			// analyze each member
			for (Element child : children) {

				if (child.getName().equals("stelle")) {
					// prepare specific sack Element and add it to the overall
					// sack Element
					Element stockAnalysisFactor = new Element("stockAnalysisFactor");
					stockAnalysisFactor.setAttribute("name", child.getAttributeValue("code"));
					stockAnalysisFactor.addContent(child.getAttributeValue("code"));
					stockAnalysisFactors.addContent(stockAnalysisFactor);

					// add the stockEvolution list to the overall list.
					se.addAll(StockEvolutionManipulator.readFromDisk(child.getAttributeValue("code")));

				} else if (child.getName().equals("gruppe")) {
					// get a list of all systemCodes that are within this group.
					List<String> stellen = new ArrayList<>();
					for (Element e : child.getDescendants(new ElementFilter())) {
						String currentName = e.getName();
						if (currentName.equals("stelle"))
							stellen.add(e.getAttributeValue("code"));
					}

					// add all StockEvolution lists to the overall list and
					// build the String listing the individual SystemCodes for
					// the sack Element
					String description = "";
					for (String stelle : stellen) {
						se.addAll(StockEvolutionManipulator.readFromDisk(stelle));
						description = description + stelle + " ";
					}

					// prepare specific sack Element and add it to the overall
					// sack Element
					Element stockAnalysisFactor = new Element("stockAnalysisFactor");
					stockAnalysisFactor.setAttribute("name",
							child.getAttributeValue("von") + " bis " + child.getAttributeValue("bis"));
					stockAnalysisFactor.addContent(description);
					stockAnalysisFactors.addContent(stockAnalysisFactor);
				}
			}
			Collections.sort(se);
			// store the prepared sack into the session
			MCRSessionMgr.getCurrentSession().put(sack, stockAnalysisFactors);
			// prepare the csv and add them to the output Element
			output.addContent(StockEvolutionManipulator.createCSVElement(se, sack));
		}

		// display the output Element
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
}
