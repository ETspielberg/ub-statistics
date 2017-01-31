package unidue.ub.statistics.series;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.DocumentCache;
import unidue.ub.statistics.media.monographs.Manifestation;

/**
 * Delivers the JSON arrays containig the evolution of stock, loan and request for a document in the document cache.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
@WebServlet("/series")
public class SeriesServlet extends MCRServlet {
	
	private static final long serialVersionUID = 1;

	private static final Logger LOGGER = Logger.getLogger(SeriesServlet.class);

	private String type;

	/**
     * reads the necessary document numbers from the http request, retrieves the documents from the cache and builds the corresponding JSON arrays. 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
	protected void doGet(MCRServletJob job) throws ServletException, IOException {
		HttpServletRequest req = job.getRequest(); 
		this.type = req.getParameter("type");
		if (type == null)
			type = "notGiven";
		List<String> docNumbers = Arrays.asList(req.getParameterValues("docNumber"));
		List<Manifestation> documents = new ArrayList<>();
		for (String docNumber : docNumbers)
			documents.add(DocumentCache.get(docNumber));
		if (documents.size() == 0)
			LOGGER.info("No docNumbers given");
		else {
			JSONArray json = buildJSON(documents);
			sendJSON(job.getResponse(), json);
		}
	}

	private void sendJSON(HttpServletResponse res, JSONArray json) throws IOException {
		res.setContentType("application/json");
		PrintWriter out = res.getWriter();
		if (json.length() == 1)
			out.println(json.get(0).toString());
		else
			out.println(json.toString());
		out.close();
	}

	private JSONArray buildJSON(List<Manifestation> documents) throws IOException {
		JSONArray json = new JSONArray();

		Hashtable<String, String[]> categories = new Hashtable<String, String[]>();
		if (type.equals("groups")) {
			categories.put("groupStudents", (new String[] { "01", "18", "24" }));
			categories.put("groupExtern", (new String[] { "02", "20", "21", "22", "23" }));
			categories.put("groupIntern", (new String[] { "06", "03", "05" }));
			categories.put("groupHappLoans", (new String[] { "07", "04", "08", "09" }));
		} else {
			categories.put("requests", (new String[] { "request", "hold" }));
			categories.put("loans", (new String[] { "loan", "return" }));
			categories.put("stock", (new String[] { "inventory", "deletion" }));
		}
		if (documents.size() == 1) {
			JSONObject jsonInd = new JSONObject();
			jsonInd.put("edition", documents.get(0).getEdition());
			jsonInd.put("shelfmark", documents.get(0).getCallNo());
			// jsonInd.put("numItems", documents.get(0).getItems().size());
			jsonInd.put("docNumber", documents.get(0).getDocNumber());
			JSONArray jsonSeries = new JSONArray();

			for (Series series : SeriesBuilder.buildSeries(documents.get(0), categories)) {
				jsonSeries.put(new Series2JSON().buildJSON(series));
				jsonInd.put("series", jsonSeries);
				if (series.getName().equals("stock")) {
					List<TimeAndCount> stock = series.buildTimeAndCountList();
					jsonInd.put("numItems", stock.get(stock.size() - 1).getCount());
				}
			}
			json.put(jsonInd);
		} else {
			json = MultipleSeriesBuilder.buildMultipleSeries(documents);
		}

		return json;
	}
}
