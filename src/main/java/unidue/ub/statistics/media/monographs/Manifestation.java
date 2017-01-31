/*
 * 
 */
package unidue.ub.statistics.media.monographs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

import unidue.ub.statistics.analysis.EventAnalysis;

/**
 * Representation of one manifestation characterized by given document number
 * and a specific callNo (shelfmark). One <code>Document</code> can contain
 * several <code>Item</code> objects and gives access to the <code>Event</code>
 * objects connected to these items. It also holds the Bibliographic data in
 * MAB-xml format
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class Manifestation implements Cloneable {

	private String docNumber;

	/** The mab. */
	private Element mab;

	/** The items. */
	private List<Item> items = new ArrayList<Item>();

	/** The event analysis. */
	private EventAnalysis eventAnalysis;

	/**
	 * Creates a new Manifestation with the given docNumber.
	 * 
	 * 
	 * @param docNumber
	 *            the docNumber of the Document.
	 */

	public Manifestation(String docNumber) {
		this.docNumber = docNumber;
	}

	/**
	 * Gets the doc number.
	 *
	 * @return the doc number
	 */
	public String getDocNumber() {
		return docNumber;
	}

	/**
	 * Sets the mab.
	 *
	 * @param mab
	 *            the new mab
	 */
	public void setMAB(Element mab) {
		this.mab = mab;
	}

	/**
	 * Gets the mab.
	 *
	 * @return the mab
	 */
	public Element getMAB() {
		return mab;
	}

	/**
	 * Sets the event analysis.
	 *
	 * @param eventAnalysis
	 *            the new event analysis
	 */
	public void setEventAnalysis(EventAnalysis eventAnalysis) {
		this.eventAnalysis = eventAnalysis;
	}

	/**
	 * Gets the event analysis.
	 *
	 * @return the event analysis
	 */
	public EventAnalysis geteventAnalysis() {
		return eventAnalysis;
	}

	/**
	 * Gets the call no.
	 *
	 * @return the call no
	 */
	public String getCallNo() {
		String callNo = "";
		for (Item item : items) {
			String itemNo = item.getCallNo();
			if ((itemNo == null) || (itemNo.equals(Item.UNKNOWN)))
				continue;
			itemNo = itemNo.replaceAll("\\+\\d+", "");
			if (callNo.contains(itemNo))
				continue;
			if (!callNo.isEmpty())
				callNo += ", ";
			callNo += itemNo;
		}
		return callNo;
	}

	/** The reg ex. */
	String regEx = ".*\\((\\d+)\\).*";

	/** The edition finder. */
	private static Pattern editionFinder = Pattern.compile(".*\\((\\d+)\\).*");

	/**
	 * Gets the shelfmark base.
	 *
	 * @return the shelfmark base
	 */
	public String getShelfmarkBase() {
		String callNo = getCallNo();
		String shelfmarkBase = editionFinder.matcher(callNo).matches() ? callNo.replaceAll("\\((\\d+)\\)", "") : callNo;
		return shelfmarkBase;
	}

	/**
	 * Gets the edition.
	 *
	 * @return the edition
	 */
	public String getEdition() {
		String callNo = getCallNo();
		Matcher m = editionFinder.matcher(callNo);
		return m.matches() ? m.group(1) : "1";
	}

	/**
	 * Adds the item.
	 *
	 * @param item
	 *            the item
	 */
	public void addItem(Item item) {
		items.add(item);
	}

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * Gets the item.
	 *
	 * @param itemSequence
	 *            the item sequence
	 * @return the item
	 */
	public Item getItem(int itemSequence) {
		for (Item item : items)
			if (item.getItemSequence() == itemSequence)
				return item;
		return null;
	}

	/**
	 * Gets the events.
	 *
	 * @return the events
	 */
	public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>();
		for (Item item : getItems())
			events.addAll(item.getEvents());
		Collections.sort(events);
		return events;
	}

	/**
	 * checks the identity of two manifestations 
	 * 
	 * @return true, if the two manifestations have the same docNumber
	 */
	@Override
	public boolean equals(Object other) {
		return docNumber.equals(((Manifestation) other).docNumber);
	}

	/**
	 * returns the hash code
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return docNumber.hashCode();
	}
	
	/**
	 * instantiates a clone of the object
	 *
	 * @return a cloned object
	 */
	public Manifestation clone() {
	    Manifestation clone = new Manifestation(docNumber);
	    for (Item item : items)
	        clone.addItem(item);
	    return clone; //for example
	}

}
