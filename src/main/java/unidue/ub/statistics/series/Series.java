package unidue.ub.statistics.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unidue.ub.statistics.media.monographs.Event;

/**
 * Group of events belonging to a specified categories.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class Series {
	
	private String name;

	/**
     * definition of a series with a name and the event types belonging to this series
     * 
     * @param name
     *            name of series
     * @param eventTypes
     *            names of event types in this series
     * 
     */
	public Series(String name, String... eventTypes) {
		this.name = name;

		for (String type : eventTypes)
			addEventType(type);
	}

	/**
     * returns the name of the series
     * 
     * @return name
     *            name of series
     * 
     */
	public String getName() {
		return name;
	}

	private Set<String> eventTypes = new HashSet<String>();

	/**
     * adds type of event to the series
     * 
     * @param type
     *            type of event
     * 
     */
	public void addEventType(String type) {
		eventTypes.add(type);
	}

	/**
     * checks whether the type of an event is part of this series
     * 
     * @param event
     *            event to be checked
     * @return isAccpted true, if the type of the event is part of the series
     * 
     */
	private boolean isAccepted(Event event) {
		if (name.startsWith("groups"))
			if (event.getType().equals("loan") || event.getType().equals("return"))
				return eventTypes.contains(event.getBorrowerStatus());
			else
				return false;
		else if (name.startsWith("sack"))
			if (name.endsWith("stock")) {
				if (event.getType().equals("loan") || event.getType().equals("return"))
					return eventTypes.contains(event.getItem().getCollection());
				else
					return false;
			} else {
				if (event.getType().equals("inventory") || event.getType().equals("deletion"))
					return eventTypes.contains(event.getItem().getCollection());
				else
					return false;
			}
		else if (name.startsWith("documents"))
			return eventTypes.contains(event.getItem().getCallNo());
		else
			return eventTypes.contains(event.getType());
	}

	private List<Event> events = new ArrayList<Event>();

	/**
     * adds the event to the series if the type of an event is part of this series
     * 
     * @param event
     *            event to be added
     * 
     */
	public void addEventIfAccepted(Event event) {
		if (isAccepted(event))
			events.add(event);
	}

	/**
     * prepares a time-and-count list from the events in this series
     * 
     * @return tacList
     *            time-and-count list
     * 
     */
	public List<TimeAndCount> buildTimeAndCountList() {
		Collections.sort(events);

		List<TimeAndCount> tacList = new ArrayList<TimeAndCount>(events.size());

		TimeAndCount current = new TimeAndCount(0, 0);
		for (Event event : events) {
			current = new TimeAndCount(event.getTime(), current.getCount() + event.getDelta());
			tacList.add(current);
		}

		return tacList;
	}
}
