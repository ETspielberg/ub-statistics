/**
 * 
 */
package unidue.ub.statistics.media.journal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.jdom2.Element;
import org.joda.time.LocalDate;

/**
 * Plain Old Java Object as representation of a journal title consisting of a single ISSN.
 * @author Eike Spielberg
 *
 */
@Entity
public class JournalTitle implements Cloneable, Comparable<JournalTitle> {
	
	@Id
    @GeneratedValue
    private long id;
	
	private String name;
	
	private String issn;
	
	private String type;
	
	private String zdbID;
	
	private String anchor;
	
	private int year;
	
	private double snip;
	
	@Lob
	private String subject;
	
	private double price;
	
	private double priceCalculated;
	
	public JournalTitle() {
		name = "";
		issn = "";
		type="electronic";
		subject = "";
		zdbID = "";
		anchor = "";
		price = 0.0;
		priceCalculated = 0.0;
		year = LocalDate.now().getYear();
		snip = 1.0;
	}

	/**
	 * returns the name of the journal title
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns the ISSN of the journal title
	 * @return the issn
	 */
	public String getIssn() {
		return issn;
	}

	/**
	 * returns the type to the journal (print or online)
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * returns the subject list of the journal
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
     * returns the EZB ID of the journal
     * @return the EZB ID
     */
    public String getZDBID() {
        return zdbID;
    }
    
    /**
     * returns the anchor of the journal collection this journal title is part of
     * @return the anchor
     */
    public String getAnchor() {
        return anchor;
    }
	
	/**
	 * returns the price of the journal
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
     * returns the calculated price of the journal
     * @return the calculated price
     */
    public double getCalculatedPrice() {
        return priceCalculated;
    }
	
	/**
     * returns the SNIP of the journal
     * @return the snip
     */
    public double getSNIP() {
        return snip;
    }

	/**
	 * returns the year of the journal
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * sets the name of the journal title
	 * @param name the name to be set
	 * @return the updated object
	 */
	public JournalTitle setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * sets the ISSN of the journal title
	 * @param issn the issn to be set
	 * @return the updated object
	 */
	public JournalTitle setIssn(String issn) {
		this.issn = issn;
		return this;
	}

	/**
	 * sets the type of the journal title
	 * @param type the type (print or online)
	 * @return the updated object
	 * 	 */
	public JournalTitle setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * sets the subjects of this journal title
	 * @param subject the subject to be set
	 * @return the updated object
	 */
	public JournalTitle setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	/**
     * sets the ZDB ID of this journal title
     * @param zdbID the ZDB ID to be set
     * @return the updated object
     */
    public JournalTitle setZDBID(String zdbID) {
        this.zdbID = zdbID;
        return this;
    }
    
    /**
     * sets the anchor of the journal collection this journal title is part of
     * @param anchor the anchor to be set
     * @return the updated object
     */
    public JournalTitle setAnchor(String anchor) {
        this.anchor = anchor;
        return this;
    }
	
	/**
	 * sets the price of this journal title
	 * @param price the price to be set
	 * @return the updated object
	 */
	public JournalTitle setPrice(double price) {
		this.price = price;
		return this;
	}
	
	/**
     * sets the calculated price of this journal title
     * @param priceCalculated the calculated price to be set
     * @return the updated object
     */
    public JournalTitle setCalculatedPrice(double priceCalculated) {
        this.priceCalculated = priceCalculated;
        return this;
    }
	
	/**
     * sets the SNIP of this journal title
     * @param snip the snip to be set
     * @return the updated object
     */
    public JournalTitle setSnip(double snip) {
        this.snip = snip;
        return this;
    }
    
	/**
	 * sets the year of this journal title
	 * @param year the year to be set
	 * @return the updated object
	 */
	public JournalTitle setYear(int year) {
		this.year = year;
		return this;
	}
	
	/**
	 * returns a list of subjects for this journal title
     * @return subjects a list of subjects covered by this journal title
     */
    public List<String> getSubjectList() {
	    List<String> subjects = new ArrayList<>();
        if (subject.contains(";"))
            subjects = Arrays.asList(subject.split(";"));
        else
            subjects.add(subject);
        return subjects;
	}
	
    /**
     * adds the journal title org.jdom2.element to the desired parent element
     * 
     * @param output
     *            the parent org.jdom2.element the journal title shall be added to
     */
    public void addToOutput(Element output) {
	    Element journalTitle = new Element("journalTitle");
	    journalTitle.addContent(new Element("subject").setText(subject));
	    journalTitle.addContent(new Element("issn").setText(issn));
	    journalTitle.addContent(new Element("name").setText(name));
	    journalTitle.addContent(new Element("type").setText(type));
	    journalTitle.addContent(new Element("year").setText(String.valueOf(year)));
	    journalTitle.addContent(new Element("price").setText(String.valueOf(price)));
	    journalTitle.addContent(new Element("priceCalculated").setText(String.valueOf(priceCalculated)));
	    journalTitle.addContent(new Element("zdbID").setText(String.valueOf(zdbID)));
	    journalTitle.addContent(new Element("anchor").setText(String.valueOf(anchor)));
	    output.addContent(journalTitle);
	}
    
    /**
     * returns a new <code>JournalTitle</code> object as clone of an existing one
     * 
     * @return a clone of the original journal title
     */
    public JournalTitle clone() {
    	JournalTitle clone = new JournalTitle();
    	clone.setIssn(issn).setName(name).setPrice(price).setSubject(subject).setType(type).setCalculatedPrice(priceCalculated).setYear(year).setZDBID(zdbID).setAnchor(anchor);
    	return clone;
    }
	
    /**
     * allows for a comparison of two collections with respect to their years.
     * Allows for the ordering of collections according to the years.
     *
     * @return difference +1 of event is after the other one, -1 if it before.
     */
    public int compareTo(JournalTitle other) {
        if (this.year > other.year)
            return 1;
        else
            return -1;
    }
}
