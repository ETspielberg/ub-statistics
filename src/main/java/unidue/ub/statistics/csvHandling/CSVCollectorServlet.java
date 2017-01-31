package unidue.ub.statistics.csvHandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.transformer.MCRXSLTransformer;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.DocumentCache;
import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.DocumentGetter;
import unidue.ub.statistics.analysis.EventAnalysis;
import unidue.ub.statistics.analysis.EventAnalyzer;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

/**
 * Takes a stored csv file, retrieves the document numbers from it and generates a
 * <code>EventAnalysis</code> for each entry.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/csvAnalyzer")
@MultipartConfig
public class CSVCollectorServlet extends FachRefServlet {

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	private static final Logger LOGGER = Logger.getLogger(CSVCollectorServlet.class);

	private static final long serialVersionUID = 1;

	/**
	 * reads the necessary parameters from the http request, calculates the loan
	 * and request times for a given Document. The results are stored in a
	 * database via persisting a <code>EventAnalysis</code>-object.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		Element output = prepareOutput(job,"csvupload","profile","csvAnalyzer");
		String who = job.getRequest().getUserPrincipal().getName();
		String fileName = getParameter(job, "file");
		File inputFile = new File(userDir + "/" + who + "/upload", fileName);
		StockControlProperties scp = new StockControlProperties("csv", who);
		ItemFilter filter = new ItemFilter(scp.getSCCollections(), scp.getSCMaterials());
		InputStream csvFile = new FileInputStream(inputFile);
		AlephConnection connection = new AlephConnection();
		DocumentGetter getter = new DocumentGetter(connection);
		Set<Manifestation> documents = getter.getDocumentsByCsv(csvFile);
		connection.disconnect();
		for (Manifestation document : documents) {
			connection = new AlephConnection();
			ItemEventCollector collector = new ItemEventCollector(connection, true, filter);
			collector.collectByDocument(document);
			DocumentCache.store(document);
			if (document.getEvents().size() > 0) {
				EventAnalyzer analyzer = new EventAnalyzer(document.getEvents(), document.getDocNumber(), scp);
				EventAnalysis analysis = analyzer.getEventAnalysis();
				analysis.setShelfmark(document.getCallNo());
				try {
					MCRJDOMContent mab = new MCRJDOMContent(document.getMAB());
					MCRXSLTransformer transformer = new MCRXSLTransformer("xsl/mabxml-isbd-shortText.xsl");
					String mabText = (transformer.transform(mab)).asXML().detachRootElement().clone().getValue();
					analysis.setMab(mabText);
				} catch (Exception e) {
					LOGGER.info("couldn't get MAB data.");
				}
				analysis.addAnalysisToOutput(output);
				connection.disconnect();
			}
		}
		sendOutput(job,output);
		LOGGER.info("Finished output.");
	}
}
