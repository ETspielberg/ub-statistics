package unidue.ub.statistics.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;

/**
 * Methods to build series from a list of events.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class SeriesBuilder {
	
	/**
	 * prepares a list of series from a list of events and a
	 * <code>Hashtable</code> defining the individual categories
	 * 
	 * @param events
	 *            list of events
	 * @param groups
	 *            <code>Hashtable</code> defining the individual categories
	 * @return list list of series
	 * 
	 */
	public static List<Series> buildSeries(List<Event> events, Hashtable<String, String[]> groups) {
		List<Series> list = new ArrayList<Series>();
		Iterator<Entry<String, String[]>> iterator = groups.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String[]> group = iterator.next();
			list.add(new Series(group.getKey(), group.getValue()));
		}
		for (Series series : list) {
			for (Event event : events)
				series.addEventIfAccepted(event);
		}
		return list;
	}

	/**
	 * prepares a list of series from a document and a <code>Hashtable</code>
	 * defining the individual categories
	 * 
	 * @param document
	 *            document,holding the events
	 * @param groups
	 *            <code>Hashtable</code> defining the individual categories
	 * @return list list of series
	 * 
	 */
	public static List<Series> buildSeries(Manifestation document, Hashtable<String, String[]> groups) {
		List<Event> events = document.getEvents();
		Collections.sort(events);
		return buildSeries(events, groups);
	}

	/**
	 * prepares a series with the stock events from a document
	 * 
	 * @param document
	 *            document holding the events
	 * @return stockSeries series with stock events
	 * 
	 */
	public static Series buildStockSeries(Manifestation document) {
		Series stockSeries = new Series("stock", "inventory", "deletion");
		for (Event event : document.getEvents())
			stockSeries.addEventIfAccepted(event);
		return stockSeries;
	}

	/**
	 * prepares a series with the stock events from a list of events
	 * 
	 * @param events
	 *            list of events
	 * @return stockSeries series with stock events
	 * 
	 */
	public static Series buildStockSeries(List<Event> events) {
		Series stockSeries = new Series("stock", "inventory", "deletion");
		for (Event event : events)
			stockSeries.addEventIfAccepted(event);
		return stockSeries;
	}

	/**
	 * prepares a series with the loan events from a document
	 * 
	 * @param document
	 *            document, holding the events
	 * @return loanSeries series with loan events
	 * 
	 */
	public static Series buildLoansSeries(Manifestation document) {
		Series LoansSeries = new Series("loans", "loan", "return");
		for (Event event : document.getEvents())
			LoansSeries.addEventIfAccepted(event);
		return LoansSeries;
	}

	/**
	 * prepares a series with the loan events from a list of events
	 * 
	 * @param events
	 *            list of events
	 * @return loanSeries series with loan events
	 * 
	 */
	public static Series buildLoansSeries(List<Event> events) {
		Series LoansSeries = new Series("loans", "loan", "return");
		for (Event event : events)
			LoansSeries.addEventIfAccepted(event);
		return LoansSeries;
	}

	/**
	 * prepares a series with the request events from a document
	 * 
	 * @param document
	 *            document, holding the events
	 * @return requestSeries series with request events
	 * 
	 */
	public static Series buildRequestsSeries(Manifestation document) {
		Series RequestsSeries = new Series("requests", "request", "hold");
		for (Event event : document.getEvents())
			RequestsSeries.addEventIfAccepted(event);
		return RequestsSeries;
	}

	/**
	 * prepares a series with the request events from a document
	 * 
	 * @param events
	 *            list of events
	 * @return requestSeries series with request events
	 * 
	 */
	public static Series buildRequestsSeries(List<Event> events) {
		Series RequestsSeries = new Series("requests", "request", "hold");
		for (Event event : events)
			RequestsSeries.addEventIfAccepted(event);
		return RequestsSeries;
	}

	/**
	 * prepares a list of series with the stock events from a document
	 * 
	 * @param events
	 *            list of events
	 * @return stockSeries series with stock events
	 * 
	 */
	public static Series buildCompleteSeries(List<Event> events) {
		Series LoansSeries = new Series("all", "loan", "return", "inventory", "deletion", "request", "hold");
		for (Event event : events)
			LoansSeries.addEventIfAccepted(event);
		return LoansSeries;
	}

	/**
	 * prepares a list of series with the stock, loan and request events from a
	 * document
	 * 
	 * @param document
	 *            document, holding the events
	 * @return series list of series with stock, loan and request events
	 * 
	 */
	public static Series buildCompleteSeries(Manifestation document) {
		Series series = new Series("all", "loan", "return", "inventory", "deletion", "request", "hold");
		for (Event event : document.getEvents())
			series.addEventIfAccepted(event);
		return series;
	}
}
