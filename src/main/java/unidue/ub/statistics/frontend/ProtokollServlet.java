package unidue.ub.statistics.frontend;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.DocumentCache;
import unidue.ub.statistics.ItemEventCollector;
import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.alephConnector.AlephConnection;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.media.monographs.Item;

/**
 * Retrieves and displays information about one document or some documents.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
@WebServlet("/protokoll")
public class ProtokollServlet extends FachRefServlet {

	private static final Logger LOGGER = Logger.getLogger(ProtokollServlet.class);

	private static final long serialVersionUID = 1;

	/**
	 * reads the necessary data from the http request, retrieves the documents
	 * from the Aleph database. The found documents, their items and events are
	 * assembled into an xml file, which is displayed as web page by XSLT
	 * transformations.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		String shelfmark = getParameter(job, "shelfmark");
		boolean exact = "true".equals(getParameter(job, "exact"));
		String collections = getParameter(job, "collections");
		String materials = getParameter(job, "materials");
		String mode = getParameter(job, "mode");
		Element output = prepareOutput(job,"aleph","protokoll");
		output.setAttribute("exact", String.valueOf(exact));
		if (!collections.isEmpty())
			output.setAttribute("collections", collections);
		if (!materials.isEmpty())
			output.setAttribute("materials", materials);
		if (!shelfmark.isEmpty()) {
			output.setAttribute("shelfmark", shelfmark);
			LOGGER.info(shelfmark + " " + exact + " " + collections + " " + materials);
			ItemFilter filter = new ItemFilter(collections, materials);
			AlephConnection connection = new AlephConnection();
			ItemEventCollector collector = new ItemEventCollector(connection, shelfmark, exact, filter, mode);
			collector.collect();
			connection.disconnect();
			for (Manifestation document : collector.getDocuments()) {
				DocumentCache.store(document);
			}
			addToOutput(output, collector);
		}
		sendOutput(job,output);
		LOGGER.info("Finished output.");
	}

	private void addToOutput(Element output, ItemEventCollector collector) {
		for (Manifestation document : collector.getDocuments())
			if (!document.getItems().isEmpty())
				addToOutput(output, document);
	}

	private void addToOutput(Element parent, Manifestation document) {
		Element xml = new Element("document");
		parent.addContent(xml);

		xml.setAttribute("key", document.getDocNumber());

		String callNo = document.getCallNo();
		if (!callNo.isEmpty())
			xml.setAttribute("callNo", callNo);

		xml.setAttribute("edition", document.getEdition());

		Element mab = document.getMAB();
		if (mab != null)
			xml.addContent(mab);

		List<Item> items = document.getItems();
		if (!items.isEmpty()) {
			Element container = new Element("items");
			xml.addContent(container);
			for (Item item : document.getItems())
				addToOutput(container, item);
		}

		List<Event> events = document.getEvents();
		if (!events.isEmpty()) {
			Element container = new Element("events");
			xml.addContent(container);
			for (Event event : events)
				addToOutput(container, event);

			Element years = new Element("years");
			xml.addContent(years);
			int minYear = Integer.parseInt(events.get(0).getYear());
			int maxYear = Integer.parseInt(events.get(events.size() - 1).getYear());
			for (int year = minYear; year <= maxYear; year++)
				years.addContent(new Element("year").setText(String.valueOf(year)));
		}
	}

	private void addToOutput(Element parent, Item item) {
		Element xml = new Element("item");
		parent.addContent(xml);

		xml.setAttribute("id", String.valueOf(item.getItemSequence()));
		xml.setAttribute("material", item.getMaterial());
		xml.setAttribute("collection", item.getCollection());
		xml.setAttribute("callNo", item.getCallNo());
		if (item.getInventoryDate() != null)
			xml.setAttribute("inventoryDate", item.getInventoryDate());
		if (item.getDeletionDate() != null)
			xml.setAttribute("deletionDate", item.getDeletionDate());

		if (item.getItemStatus() != null)
			xml.setAttribute("status", item.getItemStatus());

		if (item.getProcessStatus() != null)
			xml.setAttribute("process", item.getProcessStatus());
	}

	private final static long dayInMillis = 1000 * 60 * 60 * 24;

	private void addToOutput(Element parent, Event event) {
		Element xml = new Element("event");
		parent.addContent(xml);

		xml.setAttribute("item", String.valueOf(event.getItem().getItemSequence()));
		xml.setAttribute("year", event.getYear());
		xml.setAttribute("type", event.getType());

		long dFrom = event.getTime();
		xml.setAttribute("date", event.getDate());

		Event endEvent = event.getEndEvent();
		if (endEvent != null) {
			long dEnd = endEvent.getTime();
			xml.setAttribute("end", endEvent.getDate());
			int days = (int) (Math.floor((dEnd - dFrom) / dayInMillis)) + 1;
			xml.setAttribute("days", String.valueOf(days));
		}

		String borrowerStatus = event.getBorrowerStatus();
		if (borrowerStatus != null)
			xml.setAttribute("borrower", borrowerStatus);
	}
}
