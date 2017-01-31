package unidue.ub.statistics;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.jdom2.Element;

import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.alephConnector.DocumentGetter;
import unidue.ub.statistics.alephConnector.EventGetter;
import unidue.ub.statistics.alephConnector.ItemGetter;
import unidue.ub.statistics.alephConnector.MABGetter;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Item;
import unidue.ub.statistics.media.monographs.StockEventsBuilder;

/**
 * Collects documents, items and events connected to build the corresponding
 * documents.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class ItemEventCollector {

	private static final Logger LOGGER = Logger.getLogger(ItemEventCollector.class);

	private AlephConnection connection;

	private Set<String> shelfmarks = new HashSet<String>();

	private boolean shelfmarkAdded = true;

	private boolean exact;

	private Set<Manifestation> documents = new HashSet<Manifestation>();

	private ItemFilter filter;

	private String mode;

	/**
	 * Builds a new instance of a <code>ItemEventCollector</code>-object.
	 * 
	 * @param connection
	 *            the connection to the Aleph database
	 * @param shelfmark
	 *            the shelfmark of the document
	 * @param exact
	 *            boolean, indicating whether a propagation to other editions is
	 *            wanted
	 * @param filter
	 *            limits the collection and material of the items added to the
	 *            document
	 * @param mode
	 *            allowing to retrieve docuemnts by shelfmark or budget
	 */

	public ItemEventCollector(AlephConnection connection, String shelfmark, boolean exact, ItemFilter filter,
			String mode) {
		this.shelfmarks.add(shelfmark.trim());
		this.exact = exact;
		this.filter = filter;
		this.connection = connection;
		this.mode = mode;
	}

	/**
	 * Builds a new instance of a <code>ItemEventCollector</code>-object.
	 * 
	 * @param connection
	 *            the connection to the Aleph database
	 * @param exact
	 *            boolean, indicating whether a propagation to other editions is
	 *            wanted
	 * @param filter
	 *            limits the collection and material of the items added to the
	 *            document
	 */
	public ItemEventCollector(AlephConnection connection, boolean exact, ItemFilter filter) {
		this.exact = exact;
		this.filter = filter;
		this.connection = connection;
	}

	public Set<Manifestation> getDocuments() {
		return documents;
	}

	/**
	 * adds shelfmark to list of shelfmarks, if it is not yet on this list.
	 * 
	 * @param shelfmark
	 *            shelfmark to be added
	 */
	public void addShelfmarkIfNew(String shelfmark) {
		shelfmark = shelfmark.trim();
		shelfmark = shelfmark.replaceAll("\\+\\d+", "");

		if ("???".equals(shelfmark))
			return;

		if (!exact)
			shelfmark = shelfmark.replaceAll("\\(\\d+\\)", "");

		if (shelfmarks.contains(shelfmark))
			return;

		LOGGER.debug("added shelfmark " + shelfmark);
		shelfmarks.add(shelfmark);
		shelfmarkAdded = true;
	}

	/**
	 * collects all items and events for an existing
	 * <code>Document</code>-object.
	 * 
	 * @param document
	 *            existing document, to which the collected items and events are
	 *            added
	 * @throws Exception general exception
	 * @throws UnsupportedEncodingException exception reading the MAB data
	 */
	public void collectByDocument(Manifestation document) throws UnsupportedEncodingException, Exception {
		MABGetter mabGetter = new MABGetter(connection);
		ItemGetter itemGetter = new ItemGetter(connection);
		EventGetter eventGetter = new EventGetter(connection, filter);
		addMAB(mabGetter, document);
		addItems(itemGetter, document);
		eventGetter.addEvents(document);
		StockEventsBuilder.buildStockEvents(document);
	}

	/**
	 * collects all documents, items and events for the requested shelfmark or
	 * shelfmarks.
	 * 
	 * @throws Exception general exception
	 */
	public void collect() throws Exception {
		DocumentGetter documentGetter = new DocumentGetter(connection);
		MABGetter mabGetter = new MABGetter(connection);
		ItemGetter itemGetter = new ItemGetter(connection);
		EventGetter eventGetter = new EventGetter(connection, filter);
		do {
			for (String shelfmark : shelfmarks) {
				LOGGER.debug("Collecting documents for shelfmark " + shelfmark);
				if (mode.equals("etat"))
					documents = documentGetter.getDocumentsByEtat(shelfmark);
				else
					documents = documentGetter.getDocumentsByShelfmark(shelfmark, exact);
				for (Manifestation document : documents) {
					if (document.getMAB() == null) {
						addMAB(mabGetter, document);
						addItems(itemGetter, document);
						eventGetter.addEvents(document);
						StockEventsBuilder.buildStockEvents(document);
					}
				}
			}
			shelfmarkAdded = false;

			for (Manifestation document : documents) {
				for (String callNo : document.getCallNo().split(","))
					addShelfmarkIfNew(callNo);
				for (Item item : document.getItems())
					addShelfmarkIfNew(item.getCallNo());
			}
		} while (shelfmarkAdded);
	}

	/**
	 * adds the bibliographics description to the document.
	 * 
	 * @param mabGetter
	 *            a <code>MABGetter</code>-object to retrieve the bibliographic
	 *            information in MAB-format from the Aleph database.
	 * @param document
	 *            existing document, to which the bibliographic information are
	 *            added
	 * @throws Exception general exception
	 * @throws SQLException exception in connecting to Aleph database
	 * @throws UnsupportedEncodingException exception reading MAB data
	 */
	public void addMAB(MABGetter mabGetter, Manifestation document)
			throws Exception, SQLException, UnsupportedEncodingException {
		Element mab = mabGetter.getMAB(document);
		if (mab != null)
			document.setMAB(mab);
	}

	/**
	 * adds the items to the document.
	 * 
	 * @param getter
	 *            a <code>ItemGetter</code>-object to retrieve the items from
	 *            the Aleph database.
	 * @param document
	 *            existing document, to which the items are added
	 * @throws Exception general exception
	 * 
	 */
	public void addItems(ItemGetter getter, Manifestation document) throws Exception {
		for (Item item : getter.getItems(document))
			if (filter.matches(item))
				document.addItem(item);
	}

	int sortNumber = 0;

}
