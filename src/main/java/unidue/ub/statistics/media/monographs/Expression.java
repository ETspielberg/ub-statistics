package unidue.ub.statistics.media.monographs;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * Representation of one expression consisting of different manifestations
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class Expression implements Cloneable {

	private String shelfmarkBase;

	private Element mab;

	private List<Manifestation> documents = new ArrayList<>();

	/**
	 * creates a new <code>Work</code>-object with the given basic shelfmark
	 *
	 * @param shelfmarkBase
	 *            the basic shelfmark of the work
	 */

	public Expression(String shelfmarkBase) {
		this.shelfmarkBase = shelfmarkBase;
	}

	/**
	 * returns the basic shelfmark for this work
	 *
	 * @return shelfmarkBase the basic shelfmark
	 */
	public String getShelfmarkBase() {
		return shelfmarkBase;
	}

	/**
	 * sets the basic shelfmark for this work
	 *
	 * @param shelfmarkBase
	 *            the basic shelfmark
	 */
	public void setShelfmarkBase(String shelfmarkBase) {
		this.shelfmarkBase = shelfmarkBase;
	}

	/**
	 * sets the bibliographic information for this work
	 *
	 * @param mab
	 *            a org.jdom2.element containing the bibliographic information
	 *            in MAB format
	 */
	public void setMAB(Element mab) {
		this.mab = mab;
	}

	/**
	 * returns bibliographic information for this work
	 *
	 * @return mab a org.jdom2.element containing the bibliographic information
	 *         in MAB format
	 */

	public Element getMAB() {
		return mab;
	}

	/**
	 * adds a document to this work
	 *
	 * @param document
	 *            the document to be added
	 */
	public void addDocument(Manifestation document) {
		documents.add(document);
	}

	/**
	 * checks whether a document is already in this work
	 *
	 * @return boolean true if work contains document
	 * @param document
	 *            the document to be tested
	 */
	public boolean contains(Manifestation document) {
		return document.getShelfmarkBase().equals(this.shelfmarkBase);
	}

	/**
	 * returns all documents of this work
	 *
	 * @return documents the list of documents
	 */

	public List<Manifestation> getDocuments() {
		return documents;
	}

	/**
	 * returns the events of all items belonging to this work
	 *
	 * @return events the list of events
	 */
	public List<Event> getEvents() {
		List<Event> events = new ArrayList<>();
		for (Manifestation document : documents) {
			events.addAll(document.getEvents());
		}
		return events;
	}

	/**
	 * returns the items belonging to this work
	 *
	 * @return items the list of items
	 */
	public List<Item> getItems() {
		List<Item> items = new ArrayList<>();
		for (Manifestation document : documents) {
			items.addAll(document.getItems());
		}
		return items;
	}

	/**
	 * returns the document with the specified document number from this work
	 *
	 * @param docNumber
	 *            the document number of a document within the work
	 * @return document the document with the corresponding document number
	 */
	public Manifestation getDocument(String docNumber) {
		for (Manifestation document : documents)
			if (document.getDocNumber().equals(docNumber))
				return document;
		return null;
	}

	/**
	 * returns compares two works by their basic shelfmarks
	 *
	 * @param other
	 *            another work
	 * @return document true, if the basic shelfmarks are identical
	 */
	@Override
	public boolean equals(Object other) {
		return shelfmarkBase.equals(((Expression) other).shelfmarkBase);
	}

	/**
	 * returns a hash code for this work
	 *
	 * @return haschCode a hash for this work
	 */
	@Override
	public int hashCode() {
		return shelfmarkBase.hashCode();
	}
	
	/**
	 * instantiates a clone of the object
	 *
	 * @return a cloned object
	 */
	public Expression clone() {
	    Expression clone = new Expression(shelfmarkBase);
	    for (Manifestation document : documents)
	        clone.addDocument(document);
	    return clone;
	}

}
