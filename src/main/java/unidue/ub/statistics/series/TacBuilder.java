package unidue.ub.statistics.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Methods to build and manipulate time-and-count lists from a series of events.
 * 
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class TacBuilder {
	
	/**
	 * prepares a list of time-and-count list from a series of events
	 * 
	 * @param series
	 *            a <code>Series</code> of events
	 * @return list time-and-count list
	 * 
	 */
	public static List<TimeAndCount> buildTimeAndCountList(Series series) {
        List<TimeAndCount> tacList = series.buildTimeAndCountList();
        return tacList;
    }

	/**
	 * expands a time-and-count list to the given list of times
	 * 
	 * @param tacOld
	 *            the unchanged time-and-count list
	 * @param times
	 *            a list of times
	 * @return list expanded time-and-count list
	 * 
	 */
	public static ArrayList<TimeAndCount> expandTimeAndCountList(List<Long> times, ArrayList<TimeAndCount> tacOld) {
        ArrayList<TimeAndCount> tacListExpanded = new ArrayList<>();
        int countCurrent = 0;
        for (Long time : times) {
            for (TimeAndCount tac : tacOld) {
                if (time == tac.getTime())
                    countCurrent = tac.getCount();
            }
            tacListExpanded.add(new TimeAndCount(time, countCurrent));
        }
        return tacListExpanded;
    }

	/**
	 * expands each time-and-count lists of several series of events to the given list of times
	 * 
	 * @param seriesList
	 *            a list of series of events
	 * @param times
	 *            a list of times
	 * @return allTacListExpanded an expanded array of time-and-count list
	 * 
	 */
	public static ArrayList<ArrayList<TimeAndCount>> expandSeries2TimeAndCountLists(List<Long> times, List<Series> seriesList) {
        ArrayList<ArrayList<TimeAndCount>> allTacListExpanded = new ArrayList<ArrayList<TimeAndCount>>();
        for (Series series : seriesList) {
            List<TimeAndCount> tacOld = series.buildTimeAndCountList();
            ArrayList<TimeAndCount> tacListExpanded = new ArrayList<>();
            int countCurrent = 0;
            for (Long time : times) {
                for (TimeAndCount tac : tacOld) {
                    if (time == tac.getTime()) countCurrent = tac.getCount();
                }
                tacListExpanded.add(new TimeAndCount(time, countCurrent));
            }
            allTacListExpanded.add(tacListExpanded);
        }
        return allTacListExpanded;
    }
    
	/**
	 * harmonizes several time-and-count lists to a common list of times
	 * 
	 * @param tacAll
	 *            an array of time-and-count lists
	 * @return harmonizedTacLists an harmonized array of time-and-count list
	 * 
	 */
	public static ArrayList<ArrayList<TimeAndCount>> harmonizeTacLists(ArrayList<ArrayList<TimeAndCount>> tacAll) {
        List<Long> allTimes = new ArrayList<>();
        ArrayList<ArrayList<TimeAndCount>> harmonizedTacLists = new ArrayList<ArrayList<TimeAndCount>>();
        
        for (ArrayList<TimeAndCount> tacList : tacAll) {
            for (TimeAndCount tac : tacList) allTimes.add(tac.getTime());
        }
        Collections.sort(allTimes);
        for (ArrayList<TimeAndCount> tacList : tacAll) harmonizedTacLists.add(expandTimeAndCountList(allTimes,tacList));
        return harmonizedTacLists;
    }

}
