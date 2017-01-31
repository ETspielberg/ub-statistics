package unidue.ub.statistics.stock;

import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;

/**
 * Transforming the date-and-price lists to JSON array.
 * 
 * @author Eike Spielberg
 **/
public class DAPList2JSONArray {

	/**
	 * takes a date-and-price list and transforms it into a JSON array
	 * 
	 * @param list
	 *            the date-and-price list
	 * @return data the JSON array containing the data
	 **/
	public static JSONArray buildJSONFromList(List<DateAndPrice> list) {
		JSONArray data = new JSONArray();

		for (ListIterator<DateAndPrice> iterator = list.listIterator(); iterator.hasNext();) {
			DateAndPrice current = iterator.next();
			buildJSON(data, current.getDate(), current.getPrice());
		}
		return data;
	}

	private static void buildJSON(JSONArray parent, String date, double price) {
		parent.put(new JSONArray().put(date).put(price));
	}

}
