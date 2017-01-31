/**
 * 
 */
package unidue.ub.statistics.eUsage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.joda.time.LocalDate;

/**
 * Plain Old Java Object as representation of the COUNTER statistics obtained by SUSHI requests.
 * @author Spielberg
 *
 */
@Entity
public class Counter implements Comparable<Counter> {
    
    @Id
    @GeneratedValue
    private long id;
    
    private String printISSN;
    
    private String onlineISSN;
    
    private String abbreviation;
    
    private String fullName;
    
    private String publisher;
    
    private String type;
    
    private int year;
    
    private int month;
    
    private int htmlRequests;
    
    private int htmlRequestsMobile;
    
    private int pdfRequests;
    
    private int pdfRequestsMobile;
    
    private int psRequests;
    
    private int psRequestsMobile;
    
    private int totalRequests;
    
    /**
     * general constructor and initialization
     */
    public Counter() {
         printISSN = "";
         onlineISSN = "";
         abbreviation = "";
         fullName = "";
         publisher = "";
         type = "";
         year = LocalDate.now().getYear();
         month = 0;
         htmlRequests = 0;
         htmlRequestsMobile = 0;
         pdfRequests = 0;
         pdfRequestsMobile = 0;
         psRequests = 0;
         psRequestsMobile = 0;
         totalRequests = 0;
    }

    /**
     * returns the ISSNs of print journals
     * @return the printISSN
     */
    public String getPrintISSN() {
        return printISSN;
    }

    /**
     * returns the ISSNs of online journals
     * @return the onlineISSN
     */
    public String getOnlineISSN() {
        return onlineISSN;
    }

    /**
     * returns the abbreviation of the journal
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * returns the full name of the journal
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * returns the SUSHI provider of the journal
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * returns the type of the COUNTER report (e.g. JR1)
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * returns the year of the report
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * retursn the month of the report
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * returns the number of successful HTML request
     * @return the htmlRequests
     */
    public int getHtmlRequests() {
        return htmlRequests;
    }

    /**
     * returns the number of successful mobile HTML requests
     * @return the htmlRequestsMobile
     */
    public int getHtmlRequestsMobile() {
        return htmlRequestsMobile;
    }

    /**
     * returns the number of successful PDF requests
     * @return the pdfRequests
     */
    public int getPdfRequests() {
        return pdfRequests;
    }

    /**
     * returns the number of successful mobile PDF requests
     * @return the pdfRequestsMobile
     */
    public int getPdfRequestsMobile() {
        return pdfRequestsMobile;
    }

    /**
     * returns the number of successful PostScript requests
     * @return the psRequests
     */
    public int getPsRequests() {
        return psRequests;
    }

    /**
     * returns the number of successful mobile PostScript requests
     * @return the psRequestsMobile
     */
    public int getPsRequestsMobile() {
        return psRequestsMobile;
    }

    /**
     * returns the total number of successful requests independent of the type
     * @return the totalRequests
     */
    public int getTotalRequests() {
        return totalRequests;
    }

    /**
     * sets the ISSNs of print journals 
     * @param printISSN the printISSN to set
     * @return Counter the updated object
     */
    public Counter setPrintISSN(String printISSN) {
        this.printISSN = printISSN;
        return this;
    }

    /**
     * sets the ISSNs of online journals
     * @param onlineISSN the onlineISSN to set
     * @return Counter the updated object
     */
    public Counter setOnlineISSN(String onlineISSN) {
        this.onlineISSN = onlineISSN;
        return this;
    }

    /**
     * sets the abbreviation of the journal
     * @param abbreviation the abbreviation to set
     * @return Counter the updated object
     */
    public Counter setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
        return this;
    }

    /**
     * sets the full name of the journal
     * @param fullName the fullName to set
     * @return Counter the updated object
     */
    public Counter setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    /**
     * sets the SUSHI provider of the journal
     * @param publisher the publisher to set
     * @return Counter the updated object
     */
    public Counter setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * sets the type of the COUNTER report (e.g. JR1)
     * @param type the type to set
     * @return Counter the updated object
     */
    public Counter setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * sets the year of the report
     * @param year the year to set
     * @return Counter the updated object
     */
    public Counter setYear(int year) {
        this.year = year;
        return this;
    }

    /**
     * sets the month of the report
     * @param month the month to set
     * @return Counter the updated object
     */
    public Counter setMonth(int month) {
        this.month = month;
        return this;
    }

    /**
     * sets the number of successful HTML request
     * @param htmlRequests the htmlRequests to set
     * @return Counter the updated object
     */
    public Counter setHtmlRequests(int htmlRequests) {
        this.htmlRequests = htmlRequests;
        return this;
    }

    /**
     * sets the number of successful mobile HTML request
     * @param htmlRequestsMobile the htmlRequestsMobile to set
     * @return Counter the updated object
     */
    public Counter setHtmlRequestsMobile(int htmlRequestsMobile) {
        this.htmlRequestsMobile = htmlRequestsMobile;
        return this;
    }

    /**
     * sets the number of successful PDF request
     * @param pdfRequests the pdfRequests to set
     * @return Counter the updated object
     */
    public Counter setPdfRequests(int pdfRequests) {
        this.pdfRequests = pdfRequests;
        return this;
    }

    /**
     * sets the number of successful mobile PDF request
     * @param pdfRequestsMobile the pdfRequestsMobile to set
     * @return Counter the updated object
     */
    public Counter setPdfRequestsMobile(int pdfRequestsMobile) {
        this.pdfRequestsMobile = pdfRequestsMobile;
        return this;
    }

    /**
     * sets the number of successful PostScript request
     * @param psRequests the psRequests to set
     * @return Counter the updated object
     */
    public Counter setPsRequests(int psRequests) {
        this.psRequests = psRequests;
        return this;
    }

    /**
     * sets the number of successful mobile PostScript request
     * @param psRequestsMobile the psRequestsMobile to set
     * @return Counter the updated object
     */
    public Counter setPsRequestsMobile(int psRequestsMobile) {
        this.psRequestsMobile = psRequestsMobile;
        return this;
    }

    /**
     * sets the total number of successful requests independent of the type
     * @param totalRequests the totalRequests to set
     * @return Counter the updated object
     */
    public Counter setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
        return this;
    }
    
    /**
     * compares one COUNTER report to the other. Allows for time-dependent ordering of COUNTER reports. 
     * @param other the other COUNTER report, the actual one is compared to
     * return int 1, if the actual report dates later than the other one and -1 if it dates earlier.
     */
    public int compareTo(Counter other) {
		if (this.year > other.getYear())
			return 1;
		else if (this.year < other.getYear())
		 return -1;
		else { 
		if (this.month < other.getMonth())
			return -1;
		else if (this.month > other.getMonth())
			return 1;
		else
			return this.month - other.getMonth();
		}
	}
    
    /**
     * adds another COUNTER report to the actual one. The request fields of the other report are added to the ones of the actual report, 
     * the String fields are retained from the actual COUNTER report. 
     * @param other the other COUNTER report, the actual one is compared to
     * @return Counter the updated object
     */
    public Counter add(Counter other) {
        htmlRequests += other.getHtmlRequests();
        htmlRequestsMobile += other.getHtmlRequestsMobile();
        pdfRequests += other.getPdfRequests();
        pdfRequestsMobile += other.getPdfRequestsMobile();
        psRequests += other.getPsRequests();
        psRequestsMobile += other.getPsRequestsMobile();
        totalRequests += other.getTotalRequests();
        return this;
    }
}
