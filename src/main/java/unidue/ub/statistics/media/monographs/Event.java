package unidue.ub.statistics.media.monographs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representation object of one event from one of the three groups loans (loan
 * and return) requests (request and hold) and stock (inventory and deletion).
 * 
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class Event implements Comparable<Event> {

	private Item item;

	private Event endEvent;

	private long time;

	private String date;

	private String year;

	private String type;

	private String borrowerStatus;

	private int sorter;

	private int delta;

	/**
	 * Creates a new <code>Event</code> related to an item.
	 * 
	 * 
	 * @param item
	 *            the item related to this event
	 * @param date
	 *            the date, wehn this events happens
	 * @param hour
	 *            the hour, wehn this events happens
	 * @param type
	 *            the type of this event
	 * @param borrowerStatus
	 *            the status of the person initiating the event
	 * @param sorter
	 *            an additional sorter to allow sorting of events in the case of
	 *            multiple events at the same time
	 * @param delta
	 *            the change of the overall number of items in a particular
	 *            state upon this event (a loan increases the number of loaned
	 *            items by +1)
	 * @exception ParseException
	 *                exception parsing the date field
	 */
	public Event(Item item, String date, String hour, String type, String borrowerStatus, int sorter, int delta)
			throws ParseException {
		this.type = type;
		this.sorter = sorter;
		this.delta = delta;
		this.borrowerStatus = borrowerStatus;

		setTimeFields(date, hour);

		this.item = item;
		item.addEvent(this);
	}

	private static SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMddHHmm");

	private static SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private void setTimeFields(String date, String hour) throws ParseException {
		this.year = date.substring(0, 4);

		while (hour.length() < 4)
			hour = "0" + hour;

		synchronized (formatIn) {
			Date d = formatIn.parse(date + hour);
			this.date = formatOut.format(d);
			this.time = d.getTime();
		}
	}

	/**
	 * retrieves the item related to this event.
	 *
	 * @return item the item related to this event.
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * sets the Date of the event
	 *
	 * @param newDate
	 *            the new Date
	 */
	public void setDate(String newDate) {
		this.date = newDate;
	}

	/**
	 * relates to the end <code>Event</code>-object, for example the return to a
	 * loan event.
	 *
	 * @param endEvent
	 *            the event ending the started process
	 */
	public void setEndEvent(Event endEvent) {
		this.endEvent = endEvent;
	}

	/**
	 * returns the <code>Event</code>-object, for example the return to a loan
	 * event.
	 *
	 * @return endEvent the event ending the started process
	 */
	public Event getEndEvent() {
		return endEvent;
	}

	/**
	 * returns the year of the event
	 *
	 * @return year the year of the event
	 */
	public String getYear() {
		return year;
	}

	/**
	 * returns the timestamp (in milliseconds) of the event.
	 *
	 * @return time the timestamp of the event
	 */
	public long getTime() {
		return time;
	}

	/**
	 * returns the date (yyyy-MM-dd HH:mm) of the event
	 *
	 * @return date the date of the event
	 */
	public String getDate() {
		return date;
	}

	/**
	 * returns the type of the event (loan, return, request, hold, inventory,
	 * deletion or CALD)
	 *
	 * @return type the type of event
	 */
	public String getType() {
		return type;
	}

	/**
	 * returns the status of the person initiating the event (student,
	 * non-student member of university, external user, research faculty, other)
	 *
	 * @return status the status of the person initiating the event
	 */
	public String getBorrowerStatus() {
		return borrowerStatus;
	}

	/**
	 * returns the change of the overall number of items in a particular state
	 * upon this event (a loan increases the number of loaned items by +1)
	 *
	 * @return delta the overall number of items in a particular state upon this
	 *         event
	 */
	public int getDelta() {
		return delta;
	}

	/**
	 * allows for a comparison of two events with respect to their timestamps.
	 * Allows for the ordering of events according to the timestamps.
	 *
	 * @return difference +1 of event is after the other one, -1 if it before.
	 */
	public int compareTo(Event other) {
		if (this.time > other.time)
			return 1;
		else if (this.time < other.time)
			return -1;
		else
			return this.sorter - other.sorter;
	}
}
