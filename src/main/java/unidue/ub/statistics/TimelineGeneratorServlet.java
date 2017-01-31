package unidue.ub.statistics;

import java.util.*;

import org.mycore.common.config.MCRConfiguration;

import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;

import java.time.*;

/**
 * Calculates a List of <code>DayInTimeline</code> from list of
 * <code>Event</code>-objects.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class TimelineGeneratorServlet {

	private List<DayInTimeline> timeline = new ArrayList<>();

	private final LocalDate TODAY = LocalDate.now();

	private final static String lbs;
	
	private final static String studentUser;
	
	private final static String internUser;
	
	private final static String externUser;
	
	private final static String happUser;
	
	private final static String lendable;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		lbs = config.getString("ub.statistics.lbs");
		studentUser = config.getString("ub.statistics.status.student");
		internUser = config.getString("ub.statistics.status.intern");
		externUser = config.getString("ub.statistics.status.extern");
		happUser = config.getString("ub.statistics.status.happ");
		lendable = config.getString("ub.statistics.itemStatus.lendable");
	}

	/**
	 * Calculates the individual part of different user groups at all
	 * <code>Event</code>-times.
	 * 
	 * @param events
	 *            a list of Event-objects.
	 */

	public TimelineGeneratorServlet(List<Event> events) {
		Collections.sort(events);
		int internLoansCounter = 0;
		int externLoansCounter = 0;
		int studentLoansCounter = 0;
		int happLoansCounter = 0;
		int elseLoansCounter = 0;

		int stockCounter = 0;
		int stockLendableCounter = 0;
		int stockLBSCounter = 0;
		int stockDeletedCounter = 0;

		int internRequestsCounter = 0;
		int externRequestsCounter = 0;
		int studentRequestsCounter = 0;
		int happRequestsCounter = 0;
		int elseRequestsCounter = 0;

		if (!events.isEmpty()) {
			for (Event event : events) {
				String eventDate = event.getDate();
				if (event.getType().equals("loan") && event.getBorrowerStatus() != null) {
					if (studentUser.contains(event.getBorrowerStatus()))
						studentLoansCounter++;
					else if (externUser.contains(event.getBorrowerStatus()))
						externLoansCounter++;
					else if (internUser.contains(event.getBorrowerStatus()))
						internLoansCounter++;
					else if (happUser.contains(event.getBorrowerStatus()))
						happLoansCounter++;
					else
						elseLoansCounter++;
				} else if (event.getType().equals("return") && event.getBorrowerStatus() != null) {
					if (studentUser.contains(event.getBorrowerStatus()))
						studentLoansCounter--;
					else if (externUser.contains(event.getBorrowerStatus()))
						externLoansCounter--;
					else if (internUser.contains(event.getBorrowerStatus()))
						internLoansCounter--;
					else if (happUser.contains(event.getBorrowerStatus()))
						happLoansCounter--;
					else
						elseLoansCounter--;
				} else if (event.getType().equals("inventory") && event.getItem() != null) {
					stockCounter++;
					if (event.getItem().getItemStatus() != null) {
						if (lendable.contains(event.getItem().getItemStatus()))
							stockLendableCounter++;
					}
					if (lbs.contains(event.getItem().getCollection())) {
						stockLBSCounter++;
					}
				} else if (event.getType().equals("deletion") && event.getItem() != null) {
					stockCounter--;
					if (event.getItem().getItemStatus() != null) {
						if (lendable.contains(event.getItem().getItemStatus()))
							stockLendableCounter--;
					}
					if (lbs.contains(event.getItem().getCollection())) {
						stockLBSCounter--;
					}
					stockDeletedCounter++;
				} else if (event.getType().equals("request") && event.getBorrowerStatus() != null) {
					if (studentUser.contains(event.getBorrowerStatus()))
						studentRequestsCounter++;
					else if (externUser.contains(event.getBorrowerStatus()))
						externRequestsCounter++;
					else if (internUser.contains(event.getBorrowerStatus()))
						internRequestsCounter++;
					else if (happUser.contains(event.getBorrowerStatus()))
						happRequestsCounter++;
					else
						elseRequestsCounter++;
				} else if (event.getType().equals("hold") && event.getBorrowerStatus() != null) {
					if (studentUser.contains(event.getBorrowerStatus()))
						studentRequestsCounter--;
					else if (externUser.contains(event.getBorrowerStatus()))
						externRequestsCounter--;
					else if (internUser.contains(event.getBorrowerStatus()))
						internRequestsCounter--;
					else if (happUser.contains(event.getBorrowerStatus()))
						happRequestsCounter--;
					else
						elseRequestsCounter--;
				}
				if (stockCounter != 0) {
					DayInTimeline dayInTimeline = new DayInTimeline();
					dayInTimeline.setDay(eventDate.substring(0, 10)).setExternLoans(externLoansCounter)
							.setInternLoans(internLoansCounter).setHappLoans(happLoansCounter)
							.setStudentLoans(studentLoansCounter).setElseLoans(elseLoansCounter).setStock(stockCounter)
							.setStockLendable(stockLendableCounter).setStockLBS(stockLBSCounter)
							.setStockLendableNonLBS(stockLendableCounter - stockLBSCounter)
							.setStockNonLendable(stockCounter - stockLendableCounter)
							.setExternRequests(externRequestsCounter).setInternRequests(internRequestsCounter)
							.setStudentRequests(studentRequestsCounter).setHappRequests(happRequestsCounter)
							.setElseRequests(elseRequestsCounter).setStockDeleted(stockDeletedCounter);
					timeline.add(dayInTimeline);
				}
			}
			DayInTimeline lastDayInTimline = timeline.get(timeline.size() - 1);
			DayInTimeline statusQuo = new DayInTimeline();
			statusQuo.setDay(TODAY.toString()).setExternLoans(lastDayInTimline.getExternLoans())
					.setInternLoans(lastDayInTimline.getInternLoans()).setHappLoans(lastDayInTimline.getHappLoans())
					.setStudentLoans(lastDayInTimline.getStudentLoans()).setElseLoans(lastDayInTimline.getElseLoans())
					.setStock(lastDayInTimline.getStock()).setStockLendable(lastDayInTimline.getStockLendable())
					.setStockLBS(lastDayInTimline.getStockLBS())
					.setStockLendableNonLBS(lastDayInTimline.getStockLendableNonLBS())
					.setStockNonLendable(lastDayInTimline.getStockNonLendable())
					.setExternRequests(lastDayInTimline.getExternRequests())
					.setInternRequests(lastDayInTimline.getInternRequests())
					.setStudentRequests(lastDayInTimline.getStudentRequests())
					.setHappRequests(lastDayInTimline.getHappRequests())
					.setElseRequests(lastDayInTimline.getElseRequests()).setStockDeleted(stockDeletedCounter);

			timeline.add(statusQuo);
		} else {
			// if no events are found, just return an empty day with zeros
			DayInTimeline dayInTimeline = (new DayInTimeline()).setDay(TODAY.toString())
					.setExternLoans(externLoansCounter).setInternLoans(internLoansCounter)
					.setHappLoans(happLoansCounter).setStudentLoans(studentLoansCounter).setElseLoans(elseLoansCounter)
					.setStock(stockCounter).setStockLendable(stockLendableCounter).setStockLBS(stockLBSCounter)
					.setStockLendableNonLBS(stockLendableCounter - stockLBSCounter)
					.setStockNonLendable(stockCounter - stockLendableCounter).setExternRequests(externRequestsCounter)
					.setInternRequests(internRequestsCounter).setStudentRequests(studentRequestsCounter)
					.setHappRequests(happRequestsCounter).setElseRequests(elseRequestsCounter)
					.setStockDeleted(stockDeletedCounter);
			timeline.add(dayInTimeline);
		}
	}

	/**
	 * Calculates the overall stock for a list of <code>Document</code> objects.
	 * 
	 * @param documents
	 *            a list of Document-objects.
	 * @return allData
	 *            the array of all stock timelines
	 */

	public static ArrayList<ArrayList<Integer>> buildStockTimelines(List<Manifestation> documents) {
		List<Event> events = new ArrayList<>();
		List<String> allDatesExpanded = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allData = new ArrayList<ArrayList<Integer>>();

		for (Manifestation document : documents)
			events.addAll(document.getEvents());
		Collections.sort(events);
		for (Event event : events)
			allDatesExpanded.add(event.getDate());
		List<String> allDates = new ArrayList<String>(new HashSet<String>(allDatesExpanded));
		for (Manifestation document : documents) {
			ArrayList<Integer> dataIndividual = new ArrayList<>();
			List<Event> eventsIndividual = document.getEvents();
			int stockCounter = 0;
			for (String date : allDates) {
				for (Event event : eventsIndividual) {
					if (date.equals(event.getDate())) {
						switch (event.getType()) {
						case "inventory":
							++stockCounter;
							break;
						case "deletion":
							--stockCounter;
							break;
						default:
						}
					}
				}
				dataIndividual.add(stockCounter);
			}
			allData.add(dataIndividual);
		}
		return allData;
	}

	/**
	 * returns the timeline as list of <code>DayInTimeline</code> objects.
	 * 
	 * @return timeline the timeline
	 */

	public List<DayInTimeline> getTimeline() {
		return timeline;
	}
}
