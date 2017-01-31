package unidue.ub.statistics;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;

/**
 * Prepares a csv list of <code>DayInTimline</code>-objects from the documents in the
 * document cache.
 * 
 * @author Eike Spielberg, Frank L\u00FCtzenkirchen
 * @version 1
 */
public class TimelineExporter extends MCRServlet {

	private static final long serialVersionUID = 1;

	private static final Logger LOGGER = Logger.getLogger(TimelineExporter.class);

	/**
	 * reads the necessary document numbers from the http-request and prepares a
	 * csv file to be displayed of the temporal evolution of the individual
	 * counters.
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * 
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		List<String> docNumbers = Arrays.asList(job.getRequest().getParameterValues("docNumber"));
		if (docNumbers == null) {
			LOGGER.info("no docNumbers given");
		} else {

			if (docNumbers.size() == 1) {
				List<Event> events = new ArrayList<>();
				Manifestation documentFromCache = DocumentCache.get(docNumbers.get(0));
				events = documentFromCache.getEvents();
				Collections.sort(events);
				TimelineGeneratorServlet tlg = new TimelineGeneratorServlet(events);
				job.getResponse().setContentType("text/csv");
				OutputStream out = job.getResponse().getOutputStream();
				outputAsCSV(tlg, out);
				out.close();
			} else {
				List<Event> events = new ArrayList<>();
				List<Manifestation> documents = new ArrayList<>();
				for (String docNumber : docNumbers)
					documents.add(DocumentCache.get(docNumber));
				List<String> allDatesExpanded = new ArrayList<>();
				for (Manifestation document : documents)
					events.addAll(document.getEvents());
				Collections.sort(events);
				for (Event event : events)
					allDatesExpanded.add(event.getDate());
				List<String> allDates = new ArrayList<String>(new HashSet<String>(allDatesExpanded));
				ArrayList<ArrayList<Integer>> allStocks = TimelineGeneratorServlet.buildStockTimelines(documents);
				for (Manifestation document : documents)
					events.addAll(document.getEvents());
				job.getResponse().setContentType("text/csv");
				OutputStream out = job.getResponse().getOutputStream();
				outputAsCSV(docNumbers, allDates, allStocks, out);
				out.close();
			}

		}
	}

	private void outputAsCSV(TimelineGeneratorServlet timeline, OutputStream out) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(out);
		writer.write("Day,Intern,Extern,Student,Happ,Else,Stock\n");
		for (DayInTimeline dayInTimeline : timeline.getTimeline()) {
			writer.write(dayInTimeline.getDay() + "," + Integer.toString(dayInTimeline.getInternLoans()) + ","
					+ Integer.toString(dayInTimeline.getExternLoans()) + ","
					+ Integer.toString(dayInTimeline.getStudentLoans()) + ","
					+ Integer.toString(dayInTimeline.getHappLoans()) + ","
					+ Integer.toString(dayInTimeline.getElseLoans()) + "," + Integer.toString(dayInTimeline.getStock())
					+ "\n");
		}
		writer.close();
	}

	private void outputAsCSV(List<String> docNumbers, List<String> allDates, ArrayList<ArrayList<Integer>> allData,
			OutputStream out) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(out);
		String header = "Day";
		for (String docNumber : docNumbers)
			header = header + "," + docNumber;
		writer.write(header + "\n");
		for (int i = 0; i < allDates.size(); i++) {
			String data = "";
			for (int j = 0; j < docNumbers.size(); j++) {
				data = data + "," + Integer.toString(allData.get(j).get(i));
			}
			writer.write(allDates.get(i) + "," + data + "\n");
		}
		writer.close();
	}
}
