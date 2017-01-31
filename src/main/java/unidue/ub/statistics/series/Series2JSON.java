package unidue.ub.statistics.series;

import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mycore.services.i18n.MCRTranslation;

/**
 * Methods to prepare JSON objects from series to display the series in web pages. 
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class Series2JSON {
	
	/**
     * prepare JSON object from a series
     * 
     * @param series series containing one type of events
     * @return obj
     *            JSON object with the series data
     * 
     */
    public JSONObject buildJSON(Series series) {
        JSONObject obj = new JSONObject();
        obj.put("name", MCRTranslation.translate("aleph.series." + series.getName()));
        if (series.getName().equals("stock")) obj.put("index",0);
        else if (series.getName().equals("loans")) obj.put("index", 1);
        else if (series.getName().equals("requests")) obj.put("index", 2);
        obj.put("data", buildJSON(series.buildTimeAndCountList()));
        return obj;
    }

	/**
	 * prepare JSON array from a time-and-count list
	 * 
	 * @param list
	 *            time-and-count list
	 * @return data JSON Array with the time-and-count list data
	 * 
	 */
    public JSONArray buildJSON(List<TimeAndCount> list) {
        JSONArray data = new JSONArray();

        for (ListIterator<TimeAndCount> iterator = list.listIterator(); iterator.hasNext();) {
            TimeAndCount current = iterator.next();
            buildJSON(data, current.getTime(), current.getCount());

            if (iterator.hasNext()) {
                buildJSON(data, iterator.next().getTime() - 1, current.getCount());
                iterator.previous();
            } else
                buildJSON(data, System.currentTimeMillis(), current.getCount());
        }
        return data;
    }

    private void buildJSON(JSONArray parent, long time, int count) {
        parent.put(new JSONArray().put(time).put(count));
    }
}
