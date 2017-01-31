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

/**
 * Plain old java object as representation of a journal, collecting all different types (electronic/ print) and changes throughout time (publisher change, name changes etc.) 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class Journal {
	
	@Id
    @GeneratedValue
    private long id;
	
	@Lob
	private String actualName;
	
	@Lob
	private String issns;
	
	private String zdbID;
	
	private String ezbID;
	
	@Lob
	private String subject;
	
	@Lob
	private String link;
	
	private String publisher;
	
	
	/**
	 * general constructor and initialization
	 */
	public Journal() {
		actualName = "";
		issns = "";
		ezbID = "";
		zdbID = "";
		link = "";
		subject = "";
		publisher = "";
	}

	/**
	 * returns the ZDB ID
	 * @return the zdbID
	 */
	public String getZdbID() {
		return zdbID;
	}

	/**
	 * returns the EZB ID
	 * @return the ezbID
	 */
	public String getEzbID() {
		return ezbID;
	}

	/**
	 * sets the ZDB ID
	 * @param zdbID the zdbID to be set
	 * @return Journal the updated Object
	 */
	public Journal setZdbID(String zdbID) {
		this.zdbID = zdbID;
		return this;
	}

	/**
	 * sets the EZB ID
	 * @param ezbID the ezbID to be set
	 * @return Journal the updated Object
	 */
	public Journal setEzbID(String ezbID) {
		this.ezbID = ezbID;
		return this;
	}
	
	/**sets the current name of the 
	 * @param actualName the actualName to be set
	 * @return Journal the updated Object
	 */
	public Journal setActualName(String actualName) {
		this.actualName = actualName;
		return this;
	}
	
	/**
	 * sets the current publisher of the journal
	 * @param publisher the publisher to be set
	 * @return Journal the updated Object
	 */
	public Journal setPublisher(String publisher) {
		this.publisher = publisher;
		return this;
	}

	/**
	 * sets the link to the journal
	 * @param link the link to be set
	 * @return Journal the updated Object
	 */
	public Journal setLink(String link) {
		this.link = link;
		return this;
	}

	/**
	 * returns the current name of the journal
	 * @return the name
	 */
	public String getActualName() {
		return actualName;
	}

	/**
	 * returns the issns of the journal
	 * @return the issns
	 */
	public String getIssns() {
		return issns;
	}

	/**
	 * returns the subject categories of the journal
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * returns the publisher of the journal
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * returns the link to the journal
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	
	/**
	 * sets the issns of the journal
	 * @param issns the issns to be set
	 * @return the updated Object
	 */
	public Journal setIssns(String issns) {
		this.issns = issns;
		return this;
	}
	
	/**
	 * sets the subject categories of the journal
	 * @param subject the subject to be set
	 * @return Journal the updated Object
	 */
	public Journal setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	/**
	 * returns the subject categories of the journal as list
	 * @return subject a list of the subject categories
	 */
	public List<String> getSubjects() {
		List<String> subjects = new ArrayList<>();
		if (subject.contains(",")) {
			subjects = Arrays.asList(subject.split(";"));
		} else {
			subjects.add(subject);
		}
		return subjects;
	}
	
	/**
	 * adds a issn to the present string of issns (separated by ,) of the journal
	 * @param issn the ISSN to be added
	 */
	public void addISSN(String issn) {
	    if (issns.isEmpty())
	        issns = issn;
	    else
	        issns = issns + "," + issn;
	}

	 /**
     * adds the journal org.jdom2.element to the desired parent element
     * 
     * @param output
     *            the parent org.jdom2.element the journal shall be added to
     */
     public void addToOutput(Element output) {
        Element journalTitle = new Element("journal");
        journalTitle.addContent(new Element("subject").setText(subject));
        journalTitle.addContent(new Element("zdbID").setText(zdbID));
        journalTitle.addContent(new Element("actualName").setText(actualName));
        journalTitle.addContent(new Element("ezbID").setText(ezbID));
        journalTitle.addContent(new Element("link").setText(String.valueOf(link)));
        journalTitle.addContent(new Element("publisher").setText(String.valueOf(publisher)));
        output.addContent(journalTitle);
    }
}
