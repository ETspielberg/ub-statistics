package unidue.ub.statistics.analysis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import unidue.ub.statistics.DayInTimeline;
import unidue.ub.statistics.TimelineGeneratorServlet;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;

import java.util.Collections;
import java.util.Hashtable;

/**
 * Calculates <code>DocumentAnalysis</code> from list of
 * <code>Event</code>-objects and persists it into the DocumentAnaylsis table.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class DocumentAnalyzer {

	private final LocalDate TODAY = LocalDate.now();

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private Hashtable<Integer, DocumentAnalysis> allAnalyses = new Hashtable<Integer, DocumentAnalysis>();

	/**
	 * Calculates the loan and request times for a given Document. The results
	 * are stored in a database via persisting a DocumentAnalysisJPA object.
	 * 
	 * 
	 * @param document
	 *            the Document to be analyzed.
	 */

	public DocumentAnalyzer(Manifestation document) {

		String docNumber = document.getDocNumber();
		String shelfmark = document.getCallNo();
		String notation = "";
		for (int i = 0; i < shelfmark.length(); i++) {
			if (!Character.isDigit(shelfmark.charAt(i)))
				notation += shelfmark.charAt(i);
			else
				break;
		}

		List<Event> events = document.getEvents();
		Collections.sort(events);

		// prepare the timeline to evaluate the relative lent
		TimelineGeneratorServlet tlg = new TimelineGeneratorServlet(events);
		List<DayInTimeline> timeline = tlg.getTimeline();

		// initialize the DocumentAnalyses and fill the first values
		LocalDate firstDate = LocalDate.parse(timeline.get(0).getDay(), dtf);
		int firstYear = firstDate.getYear();
		int year = firstYear;

		// create two counters: one for the counts and one for the day sums.
		DayInTimeline ditDaySum = new DayInTimeline();
		DayInTimeline lastState = new DayInTimeline();

		LocalDate newDate = firstDate;
		LocalDate lastDate = LocalDate.parse(year + "-01-01");
		int oldCountLoans = 0;
		int oldCountRequests = 0;
		int counterLoans = 0;
		int counterRequests = 0;
		DocumentAnalysis analysis = new DocumentAnalysis();

		for (int i = 0; i < timeline.size(); i++) {

			// get counters and date
			DayInTimeline dit = timeline.get(i);
			newDate = LocalDate.parse(dit.getDay(), dtf);

			// case 1: the first event, used to initialize the reference states
			if (i == 0) {
				oldCountLoans = dit.getAllLoans();
				oldCountRequests = dit.getAllRequests();
				lastState = dit;
				lastDate = newDate;
				continue;
			}

			// case 2: the change happens in the same year, that is the
			// difference is zero.
			// Use the DayInTimeline ditDaySum to sum up the days in this
			// particular year.
			if (newDate.getYear() - year == 0) {
				int NumberOfDays = newDate.getDayOfYear() - lastDate.getDayOfYear() + 1; // days
																							// between
																							// oldDate
																							// and
																							// lastDate
				ditDaySum = ditDaySum.plus(lastState.times(NumberOfDays));// Multiply
																			// with
																			// counts!

				// if there is a Loan (allLoans increasing) or a request
				// (allRequests increasing), count them
				if (dit.getAllLoans() - oldCountLoans > 0)
					counterLoans += dit.getAllLoans() - oldCountLoans;
				if (dit.getAllRequests() - oldCountRequests > 0)
					counterRequests += dit.getAllRequests() - oldCountRequests;

				// update the old counters to the actual value, also in case of
				// a return or hold.
				oldCountLoans = dit.getAllLoans();
				oldCountRequests = dit.getAllRequests();

				lastState = dit;
				lastDate = newDate;
			}

			// case 3, the change happens in one of the next years. In this
			// case, first fill the time till the end of the year and put the
			// analysis into the hashtable, reset the values in the dayCounter
			// (DayInTimeline) and go to the next year.
			else {
				// step 1: calculate the sum till the end of the current year
				// and put the analysis in the hashtable. As the change does not
				// happen
				// in the current year, the number of loans and requests is not
				// altered, so they are saved in the analysis and reset to 0.
				int numberOfDays = LocalDate.parse(year + "-12-31", dtf).getDayOfYear() - lastDate.getDayOfYear() + 1;
				ditDaySum = ditDaySum.plus(lastState.times(numberOfDays));
				analysis = buildDocumentAnalysis(lastState, ditDaySum);
				analysis.setNumberLoans(counterLoans);
				counterLoans = 0;
				analysis.setNumberRequests(counterRequests);
				counterRequests = 0;

				// additonal information are set
				analysis.setDescription(docNumber);
				analysis.setShelfmark(shelfmark);
				analysis.setTimeStamp(System.currentTimeMillis());
				analysis.setYear(year);
				analysis.setNotation(notation);

				// the number of days in that particular year is calculated.
				if (year == firstYear)
					analysis.setDays(LocalDate.parse(year + "-12-31").getDayOfYear() - firstDate.getDayOfYear() + 1);
				else
					analysis.setDays(LocalDate.parse(year + "-12-31").getDayOfYear()
							- LocalDate.parse(year + "-01-01").getDayOfYear() + 1);

				// the analysis is put into the hashtable, the day sum is reset
				// and the year increased by one
				allAnalyses.put(year, analysis);
				ditDaySum = new DayInTimeline();
				year++;

				// as long as the change is not in the current year, calculate
				// the days in between, put the analysis in the hashtable,
				// reset the dayCounter and increase the year as there are no
				// events in this year, the number of requests and loans is not
				// altered.
				while (newDate.getYear() - year > 0) {
					numberOfDays = LocalDate.parse(year + "-12-31").getDayOfYear()
							- LocalDate.parse(year + "-01-01").getDayOfYear() + 1;
					ditDaySum = lastState.times(numberOfDays);
					analysis.setDays(numberOfDays);
					analysis = buildDocumentAnalysis(lastState, ditDaySum);
					analysis.setDescription(docNumber);
					analysis.setShelfmark(shelfmark);
					analysis.setTimeStamp(System.currentTimeMillis());
					analysis.setYear(year);
					analysis.setNotation(notation);

					allAnalyses.put(year, analysis);
					ditDaySum = new DayInTimeline();
					year++;
				}
				ditDaySum = ditDaySum
						.plus(dit.times(newDate.getDayOfYear() - LocalDate.parse(year + "-01-01").getDayOfYear()));
				oldCountLoans = dit.getAllLoans();
				oldCountRequests = dit.getAllRequests();
			}
			lastDate = newDate;
			lastState = dit;
		}
		analysis.setDays(TODAY.getDayOfYear() - LocalDate.parse(year + "-01-01").getDayOfYear());
		analysis = buildDocumentAnalysis(lastState, ditDaySum);
		analysis.setDescription(docNumber);
		analysis.setShelfmark(shelfmark);
		analysis.setTimeStamp(System.currentTimeMillis());
		analysis.setYear(year);
		if (firstYear == TODAY.getYear())
			analysis.setDays(TODAY.getDayOfYear() - firstDate.getDayOfYear());
		else
			analysis.setDays(TODAY.getDayOfYear() - LocalDate.parse(year + "-01-01").getDayOfYear());
		analysis.setNotation(notation);

		allAnalyses.put(year, analysis);
	}

	private DocumentAnalysis buildDocumentAnalysis(DayInTimeline dit, DayInTimeline ditDaySum) {
		DocumentAnalysis analysis = new DocumentAnalysis();

		analysis.setDaysLoanedStudents(ditDaySum.getStudentLoans());
		analysis.setDaysLoanedIntern(ditDaySum.getInternLoans());
		analysis.setDaysLoanedExtern(ditDaySum.getExternLoans());
		analysis.setDaysLoanedHapp(ditDaySum.getHappLoans());
		analysis.setDaysLoanedElse(ditDaySum.getElseLoans());

		analysis.setDaysRequestedStudents(ditDaySum.getStudentRequests());
		analysis.setDaysRequestedIntern(ditDaySum.getInternRequests());
		analysis.setDaysRequestedExtern(ditDaySum.getExternRequests());
		analysis.setDaysRequestedHapp(ditDaySum.getHappRequests());
		analysis.setDaysRequestedElse(ditDaySum.getElseRequests());

		analysis.setDaysStockLBS(ditDaySum.getStockLBS());
		analysis.setDaysStockLendableAll(ditDaySum.getStockLendable());
		analysis.setDaysStockAll(ditDaySum.getStock());
		analysis.setDaysStockLendableNonLBS(ditDaySum.getStockLendableNonLBS());
		analysis.setDaysStockDeleted(ditDaySum.getStockDeleted());
		analysis.setDaysStockNonLendable(ditDaySum.getStockNonLendable());

		analysis.setItemsLBS(dit.getStockLBS());
		analysis.setItemsLendable(dit.getStockLendable());
		analysis.setItemsLendableNonLBS(dit.getStockLendableNonLBS());
		analysis.setItemsNonLendable(dit.getStockNonLendable());
		analysis.setItemsDeleted(dit.getStockDeleted());
		analysis.setLastStock(dit.getStock());
		return analysis;
	}

	/**
	 * returns the loan and request times for a given Document. The results are
	 * stored in a database via persisting a <code>DocumentAnalysis</code>
	 * object.
	 * 
	 * @return allAnalyses hashtable containing all analyses 
	 */

	public Hashtable<Integer, DocumentAnalysis> getAnalyses() {
		return allAnalyses;
	}

	public DocumentAnalysis updateAnalyses(DocumentAnalysis da, Manifestation document) {
		da.setTimeStamp(System.currentTimeMillis());
		List<Event> events = document.getEvents();
		if (events.isEmpty())
			return da;
		else
			

		return da;
	}
	
	
}
