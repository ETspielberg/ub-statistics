package unidue.ub.statistics.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONObject;

import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;

/**
 * Prepares JSONArray of series from multiple documents 
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class MultipleSeriesBuilder {
		
	/**
     * prepares am JSONArray with entries of series data for several document
     * 
     * @param documents
     *            list of documents
     * @return json JSON array with series of multiple documents 
     * 
     */
    public static JSONArray buildMultipleSeries(List<Manifestation> documents) {

        List<Event> eventsAll = new ArrayList<>();
        Collections.sort(eventsAll);
        JSONArray json = new JSONArray();

        List<TimeAndCount> allTAC = new ArrayList<>();

        for (Manifestation document : documents)
            eventsAll.addAll(document.getEvents());
        Collections.sort(eventsAll);
        allTAC = SeriesBuilder.buildCompleteSeries(eventsAll).buildTimeAndCountList();
        List<Long> allTimes = new ArrayList<>();
        for (TimeAndCount tac : allTAC)
            allTimes.add(tac.getTime());
        for (Manifestation document : documents) {
            List<TimeAndCount> allTACStockIndividual = SeriesBuilder.buildStockSeries(document).buildTimeAndCountList();
            List<TimeAndCount> allTACLoansIndividual = SeriesBuilder.buildLoansSeries(document).buildTimeAndCountList();
            List<TimeAndCount> allTACRequestsIndividual = SeriesBuilder.buildRequestsSeries(document)
                .buildTimeAndCountList();

            List<TimeAndCount> tacStockAllTimes = TacBuilder.expandTimeAndCountList(allTimes, (ArrayList<TimeAndCount>) allTACStockIndividual);
            List<TimeAndCount> tacLoansAllTimes = TacBuilder.expandTimeAndCountList(allTimes, (ArrayList<TimeAndCount>) allTACLoansIndividual);
            List<TimeAndCount> tacRequestsAllTimes = TacBuilder.expandTimeAndCountList(allTimes, (ArrayList<TimeAndCount>) allTACRequestsIndividual);

            JSONObject doc = new JSONObject();
            doc.put("docNumber", document.getDocNumber());
            doc.put("shelfmark", document.getCallNo());
            doc.put("stock", buildJSONFromList(tacStockAllTimes));
            doc.put("loans", buildJSONFromList(tacLoansAllTimes));
            doc.put("requests", buildJSONFromList(tacRequestsAllTimes));
            json.put(doc);
        }
        return json;
    }

    /**
     * prepares am JSONArray from a time-and-count list
     * 
     * @param list
     *            time-and-count list
     * @return data JSON array of time and count values
     * 
     */
    public static JSONArray buildJSONFromList(List<TimeAndCount> list) {
        JSONArray data = new JSONArray();

        for (ListIterator<TimeAndCount> iterator = list.listIterator(); iterator.hasNext();) {
            TimeAndCount current = iterator.next();
            if (iterator.hasNext()) {
                buildJSON(data, current.getTime(), current.getCount());
            } else {
                buildJSON(data, current.getTime(), current.getCount());
                buildJSON(data, System.currentTimeMillis(), current.getCount());
            }
        }
        return data;
    }

    private static void buildJSON(JSONArray parent, long time, int count) {
        parent.put(new JSONArray().put(time).put(count));
    }

}
