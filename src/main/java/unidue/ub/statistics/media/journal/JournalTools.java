package unidue.ub.statistics.media.journal;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Helpful tools for handling of journals
 * @author Eike Spielberg
 *
 */
public class JournalTools {
    
    private final static int yearOfToday = LocalDate.now().getYear();

    private final static Logger LOGGER = Logger.getLogger(JournalTools.class);

    private final static Pattern issnPattern = Pattern.compile("(\\d\\d\\d[0-9,A-Z]\\d\\d\\d[0-9,A-Z])");

    private static final Pattern yearPattern = Pattern.compile("((19|20)\\d\\d)");

    /**
     * determine whether the given journal identifier is a issn (returns string "issn), a comma-separated issn list (returns "list") or the anchor of a journal collection returns ("collection").
     * 
     * 
     * @param identifier	the identifier to be checked
     *            
     * @return String the type of identifier ("issn", "list" or "collection")  
     */
    public static String determineType(String identifier) {
        if (identifier.contains(","))
            return "list";
        if (identifier.contains("-"))
            identifier = identifier.replace("-", "");
        Matcher matcher = issnPattern.matcher(identifier);
        if (matcher.find())
            return "issn";
        else
            return "collection";
    }

    /**
     * takes a string of availability information (e.g. from Journal Online and Print) and retrieves the years of general availabity from it. 
     * !Warning! no attention is paid to partial availability!
     * 
     * @param givenAvailability the string of availability information
     *            
     * @return a set of years of principal availability  
     */
    public static Set<Integer> getAvailableYears(String givenAvailability) {
        Set<Integer> years = new HashSet<Integer>();
        Matcher matcher = yearPattern.matcher(givenAvailability);
        int count = countMatches(matcher);

        //if no year is found at all, return null
        if (count == 0)
            return null;
        
        matcher.reset(givenAvailability);
        matcher.find();
        //if one year is found
        if (count == 1) {
            int startYear = Integer.parseInt(matcher.group());
            if (givenAvailability.contains("ab") || givenAvailability.contains("Ab") || givenAvailability.contains("AB") || givenAvailability.contains("-")) {
                for (int i = startYear; i <= yearOfToday; i++) {
                    years.add(i);
                }
            } else
                years.add(startYear);
        } 
        
        //if two years are found, it is either a region or two singular years
        else if (count == 2) {
            LOGGER.info("found two years");
            int startYear = Integer.parseInt(matcher.group());
            matcher.find();
            int endYear = Integer.parseInt(matcher.group());
            LOGGER.info("found two years: " + startYear + " and " + endYear);
            if (givenAvailability.contains("-") || givenAvailability.contains("bis")) {
                for (int i = startYear; i <= endYear; i++)
                    years.add(i);
            } else {
                years.add(startYear);
                years.add(endYear);
            }
        }
        
        //if more than two years are found
        else {
            if (givenAvailability.contains(";")) {
                String[] regions = givenAvailability.split(";");
                for (String region : regions) {
                    matcher = matcher.reset(region);
                    matcher.find();
                    count = countMatches(matcher);
                    if (count == 0)
                        continue;
                    else if (count == 1) {
                        int startYear = Integer.parseInt(matcher.group());
                        if (region.contains("-")) {
                            for (int i = startYear; i <= LocalDate.now().getYear(); i++)
                                years.add(i);
                        } else {
                            years.add(startYear);
                        }
                    } else if (count == 2) {
                        int startYear = Integer.parseInt(matcher.group());
                        matcher.find();
                        int endYear = Integer.parseInt(matcher.group());
                        if (region.contains("-") || region.contains("bis")) {
                            for (int i = startYear; i <= endYear; i++)
                                years.add(i);
                        } else {
                            years.add(startYear);
                            years.add(endYear);
                        }
                    } else {
                        while (matcher.find())
                            years.add(Integer.parseInt(matcher.group(region)));
                    }
                }
            } else {
                matcher.reset(givenAvailability);
                int startYear = Integer.parseInt(matcher.group());
                int endYear = Integer.parseInt(matcher.group());
                if (givenAvailability.contains("-") || givenAvailability.contains("bis")) {
                    for (int i = startYear; i <= endYear; i++)
                        years.add(i);
                } else {
                    years.add(startYear);
                    years.add(endYear);
                }
            }
        }
        return years;
    }

    private static int countMatches(Matcher toBeFound) {
        int count = 0;
        while (toBeFound.find())
            count++;
        return count;
    }
}
