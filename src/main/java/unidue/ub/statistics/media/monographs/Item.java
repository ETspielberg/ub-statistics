package unidue.ub.statistics.media.monographs;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation object of one item
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class Item {

	public final static String UNKNOWN = "???";

	private String collection;

	private String callNo;

	private int itemSequence;

	private String subLibrary;

	private String material;

	private String itemStatus;

	private String processStatus;

	private String inventoryDate;

	private String deletionDate;

	private String price;

	private String recKey;

	private String etat;

	private List<Event> events = new ArrayList<Event>();

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param collection
	 *            the collection this item belongs to
	 * @param callNo
	 *            the shelfmark of this item
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param itemSequence
	 *            the identifier of this particular item
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 * @param itemStatus
	 *            the status of this item
	 * @param processStatus
	 *            the process status of this item
	 * @param inventoryDate
	 *            the date this item was inventoried
	 * @param deletionDate
	 *            the date when this item was de-inventoried
	 * @param price
	 *            the price of this item
	 */
	public Item(String collection, String callNo, String subLibrary, int itemSequence, String material,
			String itemStatus, String processStatus, String inventoryDate, String deletionDate, String price) {
		this(subLibrary, itemSequence, material, inventoryDate, deletionDate, price);

		if ((itemStatus != null) && !itemStatus.trim().isEmpty())
			this.itemStatus = itemStatus;
		if ((processStatus != null) && !processStatus.trim().isEmpty())
			this.processStatus = processStatus;

		if ((collection != null) && !collection.isEmpty())
			this.collection = collection.trim();

		if ((callNo != null) && !callNo.isEmpty())
			this.callNo = callNo;
	}

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param itemSequence
	 *            the identifier of this particular item
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 * @param inventoryDate
	 *            the date this item was inventoried
	 * @param deletionDate
	 *            the date when this item was de-inventoried
	 * @param price
	 *            the price of this item
	 */
	public Item(String subLibrary, int itemSequence, String material, String inventoryDate, String deletionDate,
			String price) {
		this.subLibrary = subLibrary;
		this.itemSequence = itemSequence;
		this.material = material.trim();
		this.inventoryDate = inventoryDate;
		this.deletionDate = deletionDate;
		this.collection = UNKNOWN;
		this.callNo = UNKNOWN;
		this.itemStatus = "xx";
		this.price = price;
	}

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param itemSequence
	 *            the identifier of this particular item
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 */
	Item(String subLibrary, int itemSequence, String material) {
		this.subLibrary = subLibrary;
		this.itemSequence = itemSequence;
		this.material = material.trim();
		this.collection = UNKNOWN;
		this.callNo = UNKNOWN;
		this.itemStatus = "xx";
	}

	/**
	 * returns the code of the budget this item was paid for.
	 *
	 * @return etat the budget code
	 */
	public String getEtat() {
		return etat;
	}

	/**
	 * sets the code of the budget this item was paid for.
	 *
	 * @param etat
	 *            the budget code
	 */
	public void setEtat(String etat) {
		this.etat = etat;
	}

	/**
	 * returns the sub-library this item is located in
	 *
	 * @return subLibrary the sub-library this item is located in
	 */
	public String getSublibrary() {
		return subLibrary;
	}

	/**
	 * returns the key in the Aleph database of this item
	 *
	 * @return recKey the key in the database
	 */
	public String getRecKey() {
		return recKey;
	}

	/**
	 * sets the key in the Aleph database of this item
	 *
	 * @param recKey
	 *            the key in the database
	 */
	public void setRecKey(String recKey) {
		this.recKey = recKey;
	}

	/**
	 * returns the item sequence identifying this particular item
	 *
	 * @return itemSequence the item sequence
	 */
	public int getItemSequence() {
		return itemSequence;
	}

	/**
	 * returns the status of this particular item
	 *
	 * @return itemStatus the status of the item
	 */
	public String getItemStatus() {
		return itemStatus;
	}

	/**
	 * returns the process status of this particular item
	 *
	 * @return processStatus the process status
	 */
	public String getProcessStatus() {
		return processStatus;
	}

	/**
	 * returns the collection this item belongs to
	 *
	 * @return collection the collection this item belongs to
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * returns the type of material of this item
	 *
	 * @return material the type of material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * returns the shelfmark of this particular item
	 *
	 * @return callNo the shelfmark
	 */
	public String getCallNo() {
		return callNo;
	}

	/**
	 * returns the date when this item was inventoried
	 *
	 * @return inventoryDate the date of inventory
	 */
	public String getInventoryDate() {
		return inventoryDate;
	}

	/**
	 * returns the date this item was de-inventoried
	 *
	 * @return deletionDate the date of de-inventory
	 */
	public String getDeletionDate() {
		return deletionDate;
	}

	/**
	 * adds an <code>Event</code>-object to the list of events associated with
	 * this item.
	 *
	 * @param event
	 *            an <code>Event</code>-object
	 */
	public void addEvent(Event event) {
		events.add(event);
	}

	/**
	 * returns all events associated with this item
	 *
	 * @return events list of events
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * returns the price of this item
	 *
	 * @return price the price
	 */
	public String getPrice() {
		return price;
	}
}
