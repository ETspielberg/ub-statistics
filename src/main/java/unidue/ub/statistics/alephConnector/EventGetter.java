package unidue.ub.statistics.alephConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import unidue.ub.statistics.ItemFilter;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.media.monographs.Item;

/**
 * Retrieves the events from the Aleph database.
 * 
 * @author Frank L\u00FCtzenkirchen,  Eike Spielberg
 * @version 1
 */
public class EventGetter {

	private PreparedStatement psGetClosedLoans;

	private PreparedStatement psGetOpenLoans;

	private PreparedStatement psGetClosedRequests;

	private PreparedStatement psGetOpenRequests;

	private List<Event> events = new ArrayList<>();

	private int sortNumber = 0;

	private ItemFilter filter;

	/**
	 * uses a given connection to Aleph database to retrieve all events
	 * connected to items, that match the filter conditions
	 * 
	 * @param connection
	 *            an <code>AlephConnection</code>-object
	 * @param filter
	 *            an <code>ItemFilter</code>-object limiting the collections and
	 *            materials to be collected
	 * @exception SQLException exception querying the Aleph database 
	 */

	public EventGetter(AlephConnection connection, ItemFilter filter) throws SQLException {
		String sql = "select z36h_rec_key, z36h_material, z36h_status, z36h_bor_status, z36h_loan_date, z36h_loan_hour, z36h_returned_date, z36h_returned_hour, z36h_sub_library from edu50.z36h where z36h_rec_key like ? and z36h_loan_date > '20000000' order by z36h_loan_date, z36h_loan_hour, z36h_number";
		psGetClosedLoans = connection.prepareStatement(sql);

		sql = "select z36_rec_key, z36_material, z36_status, z36_bor_status, z36_loan_date, z36_loan_hour, z36_sub_library from edu50.z36 where z36_rec_key like ? and z36_loan_date > '20000000' order by z36_loan_date, z36_loan_hour, z36_number";
		psGetOpenLoans = connection.prepareStatement(sql);

		sql = "select z37h_rec_key, z37h_open_date, z37h_open_hour, z37h_hold_date, z37h_pickup_location from edu50.z37h where z37h_rec_key like ? and z37h_open_date > '20000000' order by z37h_open_date, z37h_open_hour, z37h_rec_key";
		psGetClosedRequests = connection.prepareStatement(sql);

		sql = "select z37_rec_key, z37_open_date, z37_open_hour, z37_pickup_location from edu50.z37 where z37_rec_key like ? and z37_open_date > '20000000' order by z37_open_date, z37_open_hour, z37_rec_key";
		psGetOpenRequests = connection.prepareStatement(sql);
		this.filter = filter;
	}

	/**
	 * retrieves all events from the Aleph database.
	 * @return events list of events
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * adds loan and request events to an existing document
	 * 
	 * @param document
	 *            an existing document
	 * @exception Exception general exception
	 */
	public void addEvents(Manifestation document) throws Exception {
		addLoans(document);
		addRequests(document);
	}

	/**
	 * adds loan events to an existing document
	 * 
	 * @param document
	 *            an existing document
	 * @exception Exception general exception
	 */
	public void addLoans(Manifestation document) throws Exception {
		addClosedLoans(document);
		addOpenLoans(document);
	}

	private void addClosedLoans(Manifestation document) throws Exception {
		psGetClosedLoans.setString(1, document.getDocNumber() + "%");
		ResultSet rs = psGetClosedLoans.executeQuery();

		while (rs.next()) {
			String recKey = rs.getString("z36h_rec_key");
			String subLibrary = rs.getString("z36h_sub_library");
			int itemSequence = Integer.parseInt(recKey.substring(9));

			String inventoryDate = "", deletionDate = "", price = "";
			String material = rs.getString("z36h_material");
			Item item = buildItem(document, subLibrary, itemSequence, material, inventoryDate, deletionDate, price);
			if (item == null)
				continue;

			String borrowerStatus = rs.getString("z36h_bor_status");
			String loanDate = rs.getString("z36h_loan_date");
			String loanHour = rs.getString("z36h_loan_hour");
			String returnDate = rs.getString("z36h_returned_date");
			String returnHour = rs.getString("z36h_returned_hour");
			if (returnDate.length() < 8)
				continue;

			Event loanEvent = new Event(item, loanDate, loanHour, "loan", borrowerStatus, sortNumber++, 1);
			Event returnEvent = new Event(item, returnDate, returnHour, "return", borrowerStatus, sortNumber++, -1);
			loanEvent.setEndEvent(returnEvent);
			events.add(loanEvent);
			events.add(returnEvent);
		}
		rs.close();
	}

	private void addOpenLoans(Manifestation document) throws Exception {
		psGetOpenLoans.setString(1, document.getDocNumber() + "%");
		ResultSet rs = psGetOpenLoans.executeQuery();

		while (rs.next()) {
			String recKey = rs.getString("z36_rec_key");
			String subLibrary = rs.getString("z36_sub_library");
			int itemSequence = Integer.parseInt(recKey.substring(9));
			String material = "", inventoryDate = "", deletionDate = "", price = "";

			Item item = buildItem(document, subLibrary, itemSequence, material, inventoryDate, deletionDate, price);
			if (item == null)
				continue;

			String borrowerStatus = rs.getString("z36_bor_status");
			String loanDate = rs.getString("z36_loan_date");
			String loanHour = rs.getString("z36_loan_hour");

			Event event = new Event(item, loanDate, loanHour, "loan", borrowerStatus, sortNumber++, 1);
			events.add(event);
		}
		rs.close();
	}

	/**
	 * adds request events to an existing document
	 * 
	 * @param document
	 *            an existing document
	 * @exception Exception general exception
	 */
	public void addRequests(Manifestation document) throws Exception {
		addClosedRequests(document);
		addOpenRequests(document);
	}

	private void addClosedRequests(Manifestation document) throws Exception {
		psGetClosedRequests.setString(1, document.getDocNumber() + "%");
		ResultSet rs = psGetClosedRequests.executeQuery();

		while (rs.next()) {
			String recKey = rs.getString("z37h_rec_key");
			int itemSequence = Integer.parseInt(recKey.substring(9, 15));

			String material = "", borrowerStatus = "", subLibrary = "", inventoryDate = "", deletionDate = "",
					price = "";

			Item item = buildItem(document, subLibrary, itemSequence, material, inventoryDate, deletionDate, price);
			if (item == null)
				continue;

			String openDate = rs.getString("z37h_open_date");
			String openHour = rs.getString("z37h_open_hour");
			String holdDate = rs.getString("z37h_hold_date");
			if (holdDate.length() != 8)
				if (holdDate.equals("0"))
					holdDate = openDate;
				else
					continue;

			String holdHour = openDate.equals(holdDate) ? openHour : "0000";

			String pickupLocation = rs.getString("z37h_pickup_location");
			boolean isCALD = false;
			if (pickupLocation != null) {
				isCALD = !pickupLocation.equals(item.getSublibrary());
			}
			String eventType = isCALD ? "cald" : "request";
			Event openEvent = new Event(item, openDate, openHour, eventType, borrowerStatus, sortNumber, 1);

			if (!isCALD) {
				Event holdEvent = new Event(item, holdDate, holdHour, "hold", borrowerStatus, sortNumber, -1);
				events.add(holdEvent);
				openEvent.setEndEvent(holdEvent);
			}
			events.add(openEvent);
		}
		rs.close();
	}

	private void addOpenRequests(Manifestation document) throws Exception {
		psGetOpenRequests.setString(1, document.getDocNumber() + "%");
		ResultSet rs = psGetOpenRequests.executeQuery();

		while (rs.next()) {
			String recKey = rs.getString("z37_rec_key");
			int itemSequence = Integer.parseInt(recKey.substring(9, 15));
			int sortNumber = Integer.parseInt(recKey.substring(15));

			String material = "", borrowerStatus = "", subLibrary = "", inventoryDate = "", deletionDate = "",
					price = "";

			Item item = buildItem(document, subLibrary, itemSequence, material, inventoryDate, deletionDate, price);
			if (item == null)
				continue;

			String openDate = rs.getString("z37_open_date");
			String openHour = rs.getString("z37_open_hour");
			String pickupLocation = rs.getString("z37_pickup_location");

			boolean isCALD = !pickupLocation.equals(item.getSublibrary());
			String eventType = isCALD ? "cald" : "request";
			Event event = new Event(item, openDate, openHour, eventType, borrowerStatus, sortNumber, 1);
			events.add(event);
		}
		rs.close();
	}

	private Item buildItem(Manifestation document, String subLibrary, int itemSequence, String material,
			String inventoryDate, String deletionDate, String price) {
		Item item = document.getItem(itemSequence);
		if (item != null)
			return item;

		item = new Item(subLibrary, itemSequence, material, inventoryDate, deletionDate, price);
		if (!filter.matches(item))
			return null;

		document.addItem(item);
		return item;
	}

}
