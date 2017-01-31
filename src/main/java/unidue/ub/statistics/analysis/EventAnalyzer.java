package unidue.ub.statistics.analysis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;

import unidue.ub.statistics.DayInTimeline;
import unidue.ub.statistics.TimelineGeneratorServlet;
import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Event;
import unidue.ub.statistics.media.monographs.Expression;
import unidue.ub.statistics.stockcontrol.StockControlProperties;

import java.util.Collections;
import java.util.Hashtable;

/**
 * Calculates <code>EventAnalysis</code> from list of <code>Event</code>-objects
 * or a document docNumber.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class EventAnalyzer {

	private static final Logger LOGGER = Logger.getLogger(EventAnalyzer.class);

	private final LocalDate TODAY = LocalDate.now();

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private EventAnalysis analysis;

	private final static String studentUser;
    
    private final static String internUser;
    
    private final static String externUser;
    
    private final static String happUser;
    
    private final static String lendable;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        studentUser = config.getString("ub.statistics.status.student");
        internUser = config.getString("ub.statistics.status.intern");
        externUser = config.getString("ub.statistics.status.extern");
        happUser = config.getString("ub.statistics.status.happ");
        lendable = config.getString("ub.statistics.itemStatus.lendable");
    }

	/**
	 * Calculates the loan and request parameters for a given List of Events
	 * with the parameters in the StockControlProperties. The results are stored
	 * in an DocumentAnalysis object.
	 * 
	 * 
	 * @param events
	 *            a list of Event-objects.
	 * @param description
	 *            a string describing the list of Event-objects. For a single
	 *            edition (referred to as document in this program), this is
	 *            usually the docNumber.
	 * @param scp
	 *            a StockControlProperties-objects containing the main
	 *            parameters for the calculation.
	 */

	public EventAnalyzer(List<Event> events, String description, StockControlProperties scp) {
	    Hashtable<Integer, Integer> allMaxLoansAbs = new Hashtable<Integer, Integer>();
	    analysis = new EventAnalysis();
		analysis.setDescription(description);
		analysis.setTimestamp(System.currentTimeMillis());

		if (scp.getStockControl() != null)
			analysis.setStockcontrol(scp.getStockControl());
		if (scp.getSCSystemCode() != null)
			analysis.setSystemCode(scp.getSCSystemCode());
		if (scp.getSCMaterials() != null)
			analysis.setMaterials(scp.getSCMaterials());
		if (scp.getSCCollections() != null)
			analysis.setCollection(scp.getSCCollections());

		LocalDate scpStartDate = TODAY.minus((long) scp.getSCYearsToAverage(), ChronoUnit.YEARS);
		LocalDate scpStartYearRequests = TODAY.minus((long) scp.getSCYearsOfRequests(), ChronoUnit.YEARS);
		LocalDate scpMiniumumDate = TODAY.minus((long) scp.getSCMinimumYears(), ChronoUnit.YEARS);
		Collections.sort(events);

		// prepare the timeline to evaluate the relative lent
		TimelineGeneratorServlet tlg = new TimelineGeneratorServlet(events);
		List<DayInTimeline> timeline = tlg.getTimeline();

		if (timeline.size() > 0) {
			// initialize the EventAnalyses and fill the first values
			Integer yearsBefore = TODAY.getYear() - LocalDate.parse(timeline.get(0).getDay(), dtf).getYear();
			for (int year = 0; year <= yearsBefore; year++) {
				allMaxLoansAbs.put((Integer) year, 0);
			}

			int loans = 0;
			int oldLoans = 0;
			analysis.setLastStock(timeline.get(timeline.size() - 1).getStockLendable());
			for (DayInTimeline dayInTimeline : timeline) {
				LocalDate eventDate = LocalDate.parse(dayInTimeline.getDay(), dtf);
				int timeIntervall = TODAY.getYear() - eventDate.getYear();

				// if there is a wrong date given, skip this entry
				if (eventDate.isAfter(TODAY)) {
					continue;
				}

				loans = dayInTimeline.getElseLoans() + dayInTimeline.getStudentLoans() + dayInTimeline.getExternLoans()
						+ dayInTimeline.getInternLoans();
				for (int i = timeIntervall; i <= yearsBefore; i++) {
					int maxLoans = Math.max(allMaxLoansAbs.get(i), loans);
					allMaxLoansAbs.replace(i, maxLoans);
				}
				if (eventDate.isAfter(scpStartDate)) {
					loans = Math.max(loans, oldLoans);
					analysis.setMaxLoansAbs(Math.max(loans, analysis.getMaxLoansAbs()));
					int reducedStock = dayInTimeline.getStockLendable() - dayInTimeline.getHappLoans();
					if (reducedStock > 0) {
						double relativeLoan = (double) loans / (double) reducedStock;
						analysis.setMaxRelativeLoan(Math.max(relativeLoan, analysis.getMaxRelativeLoan()));
					}
				} else
					oldLoans = loans;
				int requests = dayInTimeline.getElseRequests() + dayInTimeline.getStudentRequests()
						+ dayInTimeline.getInternRequests();
				int maxItemsNeeded = dayInTimeline.getStockLendable() + requests;
				if (eventDate.isAfter(scpStartYearRequests)) {
					analysis.setMaxNumberRequest(Math.max(requests, analysis.getNumberRequests()));
					analysis.setMaxItemsNeeded(Math.max(maxItemsNeeded, analysis.getMaxItemsNeeded()));
				}
			}
			SimpleRegression trend = new SimpleRegression();
			for (int year = 1; year <= yearsBefore; year++) {
				trend.addData(year, (double) allMaxLoansAbs.get(year));
			}
			double slope = 0;
			if (trend.getN() > 1)
				slope = trend.getSlope();
			analysis.setSlope(slope);
		}
		int daysLoaned = 0;
		int daysStockLendable = 0;
		int daysRequested = 0;

		// sum up the days loaned, days in stock, all lendable days in stock,
		// days in the lbs, both for the whole time range as well as for the
		// time frame given.
		for (Event event : events) {
			// set the start date
			LocalDate startDate = LocalDate.parse(event.getDate().substring(0, 10), dtf);

			// try to get the end date. If no end date is given in the event,
			// set the end date to the actual date.
			Event endEvent = event.getEndEvent();
			LocalDate endDate;
			if (endEvent != null)
				endDate = LocalDate.parse(endEvent.getDate().substring(0, 10), dtf);
			else
				endDate = TODAY;
			if (endDate.isBefore(scpStartDate))
				continue;
			else if (startDate.isBefore(scpStartDate))
				startDate = scpStartDate;
			int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

			// analyze loan events
			if (event.getType().equals("loan")) {
				if (event.getBorrowerStatus() == null) {
					LOGGER.info("no Borrower given");
					continue;
				}
				String countableUserGroups = studentUser + externUser + internUser;
				
				if (countableUserGroups.contains(event.getBorrowerStatus()))
					daysLoaned += days;
				else if (happUser.contains(event.getBorrowerStatus()))
					daysStockLendable -= days;
				else
					daysLoaned += days;

				// analyze stock events
			} else if (event.getType().equals("inventory")) {
				if (event.getItem() != null) {
					if (event.getItem().getItemStatus() != null) {
						if (lendable.contains(event.getItem().getItemStatus()))
							daysStockLendable += days;
					}
				}

				// analyze request events
			} else if (event.getType().equals("request")) {
				if (startDate.isAfter(scpStartYearRequests)) {
					analysis.increaseNumberRequests();
					daysRequested += days;
				}
			}
		}
		analysis.setDaysRequested(daysRequested);
		Double meanRelativeLoan = 0.0;
		if (daysStockLendable != 0) {
			meanRelativeLoan = (double) daysLoaned / (double) daysStockLendable;
			analysis.setMeanRelativeLoan(meanRelativeLoan);
		}

		if (analysis.getMaxRelativeLoan() != 0) {
			double ratio = analysis.getMeanRelativeLoan()/analysis.getMaxRelativeLoan();
			if (scp.getSCStaticBuffer() < 1 && scp.getSCVariableBuffer() < 1)
				analysis.setProposedDeletion((int) ((analysis.getLastStock() - analysis.getMaxLoansAbs()) * (1
						- scp.getSCStaticBuffer()
						- scp.getSCVariableBuffer() *ratio)));
			else if (scp.getSCStaticBuffer() >= 1 && scp.getSCVariableBuffer() < 1)
				analysis.setProposedDeletion(
						(int) ((analysis.getLastStock() - analysis.getMaxLoansAbs() - scp.getSCStaticBuffer())
								* (1 - scp.getSCVariableBuffer() * ratio)));
			else if (scp.getSCStaticBuffer() >= 1 && scp.getSCVariableBuffer() >= 1)
				analysis.setProposedDeletion(
						(int) ((analysis.getLastStock() - analysis.getMaxLoansAbs() - scp.getSCStaticBuffer())
							 - scp.getSCVariableBuffer() * ratio));
			else if (scp.getSCStaticBuffer() < 1 && scp.getSCVariableBuffer() < 1)
				analysis.setProposedDeletion(
						(int) ((analysis.getLastStock() - analysis.getMaxLoansAbs())*(1-scp.getSCStaticBuffer())
							 - scp.getSCVariableBuffer() * ratio));

			if (analysis.getProposedDeletion() < 0)
				analysis.setProposedDeletion(0);
			if (analysis.getProposedDeletion() == 0 && ratio > 0.5)
				analysis.setProposedPurchase((int) (-1 * analysis.getLastStock() * 0.001 * ratio));
		} else {
			if (scp.getSCStaticBuffer() < 1)
				analysis.setProposedDeletion(
						(int) ((analysis.getLastStock() - analysis.getMaxLoansAbs()) * (1 - scp.getSCStaticBuffer())));
			else
				analysis.setProposedDeletion(
						(int) (analysis.getLastStock() - analysis.getMaxLoansAbs() - scp.getSCStaticBuffer()));
		}
		if (events.size() > 0) {
		if (LocalDate.parse(events.get(0).getDate().substring(0, 10), dtf).isAfter(scpMiniumumDate))
			analysis.setProposedDeletion(0);
		}

		if (analysis.getLastStock() - analysis.getProposedDeletion() < 2 && analysis.getLastStock() >= 3)
			analysis.setComment("ggf. umstellen");

		if ((double) analysis.getDaysRequested() / (double) analysis.getNumberRequests() >= scp
				.getSCMinimumDaysOfRequest()) {
			analysis.setProposedPurchase(analysis.getMaxItemsNeeded() - analysis.getLastStock());
		}

	}

	/**
	 * As an alternative, the EventAnalysis-object can also be created using
	 * a Document-object. The description is set to the docNumber.
	 * 
	 * @param document
	 *            A Document, that is one edition with a given docNumber.
	 * @param scp
	 *            a StockControlProperties-objects containing the main
	 *            parameters for the calculation.
	 */
	public EventAnalyzer(Manifestation document, StockControlProperties scp) {
		this(document.getEvents(), document.getDocNumber(), scp);
	}

	/**
	 * As an alternative, the EventAnalysis-object can also be created using
	 * a Work-object. The description is set to the base shelfmark.
	 * 
	 * @param work
	 *            A Work, that is many editions with a given base shelfmark.
	 * @param scp
	 *            a StockControlProperties-objects containing the main
	 *            parameters for the calculation.
	 */
	public EventAnalyzer(Expression work, StockControlProperties scp) {
		this(work.getEvents(), work.getShelfmarkBase(), scp);
	}

	/**
	 * Returns the calculated <code>EventAnalysis</code> object.
	 * 
	 * @return EventAnalysis the calculated event analysis
	 */

	public EventAnalysis getEventAnalysis() {
		return analysis;
	}
}
