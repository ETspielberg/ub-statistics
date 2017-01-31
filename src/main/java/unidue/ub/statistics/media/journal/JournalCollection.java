/**
 * 
 */
package unidue.ub.statistics.media.journal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Persistence;

import org.jdom2.Element;

/**
 * Plain old java object as representation of a journal collection, collecting all <code>JournalTitles</code> (by their issn) contained in this collection
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class JournalCollection implements Cloneable, Comparable<JournalCollection> {
	
	@Id
    @GeneratedValue
    private long id;
	
	@Lob
	private String issns;
	
	private String description;
	
	@Lob
	private String anchor;
	
	private Double price;
	
	private int year;
	
	/**
	 * general constructor and initialization
	 */
	public JournalCollection(){
		issns = "";
		description = "";
		anchor = "";
		price = 0.0;
		year = LocalDate.now().getYear();
	}

	/**
	 * returns the ISSNs contained in the collection
	 * @return the issns
	 */
	public String getIssns() {
		return issns;
	}

	/**
	 * returns the description of the the collection
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * returns the anchor of the collection
	 * @return the anchor
	 */
	public String getAnchor() {
		return anchor;
	}

	/**
	 * returns the price of the collection
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	
	/**
	 * returns the year of the collection
	 * @return the zdbID
	 */
	public int getYear() {
		return year;
	}

	/**
	 * sets the issns contained in the collection
	 * @param issns the issns contained in the collection
	 * @return JournalCollection the updated object
	 */
	public JournalCollection setIssns(String issns) {
		this.issns = issns;
		return this;
	}

	/**
	 * sets the description of the collection
	 * @param description the description of the collection
	 * @return JournalCollection the updated object
	 */
	public JournalCollection setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * sets the anchor of the collection
	 * @param anchor the anchor of the collection
	 * @return JournalCollection the updated object
	 */
	public JournalCollection setAnchor(String anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * sets the price of the collection
	 * @param price the price of the collection
	 * @return JournalCollection the updated object
	 */
	public JournalCollection setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	/**
	 * sets the year of the collection
	 * @param year the year of the collection
	 * @return JournalCollection the updated object
	 */
	public JournalCollection setYear(int year) {
		this.year = year;
		return this;
	}
	
	/**
	 * adds an issn to the string of issns (separated by ,) contained in the collection
	 * @param issn the issn 
	 */
	public void addISSN(String issn) {
	    if (issns.isEmpty())
	        issns = issn;
	    else
	        issns = issns + "," + issn;
	}
	
	/**
	 * adds an journal price to the collection price
	 * @param priceJournal the price of the individual journal 
	 */
	public void addPrice(double priceJournal) {
		price = price + priceJournal;
	}
	
	/**
	 * returns the individual <code>JournalTitle</code> objects contained in the collection
	 * @return the list of <code>JournalTitle</code> objects
	 */
	public List<JournalTitle> getJournals() {
	    List<String> issnsList = getIssnsList();
		List<JournalTitle> journals = new ArrayList<>();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
		for (String issn : issnsList) {
			JournalTitle journalTitle = JournalTitleDAO.getJournalTitle(em,issn,year);
			if (journalTitle != null)
			    journals.add(journalTitle);
		}
		em.close();
		return journals;
	}
	
	/**
     * returns the individual <code>JournalTitle</code> objects contained in the collection
     * @return the list of <code>JournalTitle</code> objects
     */
    public List<JournalTitle> getAllJournals() {
        List<String> issnsList = getIssnsList();
        List<JournalTitle> journals = new ArrayList<>();
        for (String issn : issnsList) {
            List<JournalTitle> journalTitles = JournalTitleDAO.getJournalTitlesByIssn(issn);
            if (journals != null)
            journals.addAll(journalTitles);
        }
        return journals;
    }
	
	/**
     * returns the individual <code>JournalTitle</code> objects contained in the collection
     * @return the list of <code>JournalTitle</code> objects
     */
    public List<JournalTitle> getLatestJournals() {
        List<String> issnsList = getIssnsList();
        List<JournalTitle> journals = new ArrayList<>();
        for (String issn : issnsList) {
            List<JournalTitle> journalTitles = JournalTitleDAO.getJournalTitlesByIssn(issn);
            if (journalTitles != null)
            journals.add(journalTitles.get(journalTitles.size()-1));
        }
        return journals;
    }
  
    /**
     * returns the individual <code>JournalTitle</code> objects contained in the collection
     * @param em the entity manager
     * @param year the year
     * @return the list of <code>JournalTitle</code> objects
     */
    public List<JournalTitle> getJournalsForYear(EntityManager em, int year) {
        List<String> issnsList = getIssnsList();
        List<JournalTitle> journals = new ArrayList<>();
        for (String issn : issnsList) {
            JournalTitle journalTitle = JournalTitleDAO.getJournalTitle(em,issn,year);
            if (journalTitle != null) {
                 journals.add(journalTitle);
            }
        }
        return journals;
    }
	
	/**
	 * returns the issns contained in the collection as list.
	 * @return issnsList the list of issns
	 */
	public List<String> getIssnsList() {
	    if (issns.contains(";"))
	        issns = issns.replace(";", ",");
		List<String> issnsList = new ArrayList<>();
	    if (issns.contains(",")){
	        issnsList = Arrays.asList(issns.split(","));
	    } else
	        issnsList.add(issns);
	    return issnsList;
	}

	/**
	 * adds the analysis org.jdom2.element to the desired parent element
	 * 
	 * @param output
	 *            the parent org.jdom2.element the analysis shall be added to
	 */
	public void addToOutput(Element output) {
		Element packageElement = new Element("package");
		packageElement.addContent(new Element("anchor").setText(anchor));
		packageElement.addContent(new Element("description").setText(description));
		packageElement.addContent(new Element("issns").setText(issns));
		packageElement.addContent(new Element("price").setText(String.valueOf(price)));
		packageElement.addContent(new Element("year").setText(String.valueOf(year)));
		output.addContent(packageElement);
	}
	
	/**
	 * clones the <code>JournalCollection</code> and returns an identical <code>JournalCollection</code> object
	 * @return clone a copy of the journal collection
	 */
	public JournalCollection clone() {
		JournalCollection clone = new JournalCollection();
		clone.setAnchor(anchor).setDescription(description).setIssns(issns).setPrice(price).setYear(year);
	    return clone;
	}
	
	/**
     * allows for a comparison of two collections with respect to their years.
     * Allows for the ordering of collections according to the years.
     *
     * @return difference +1 of event is after the other one, -1 if it before.
     */
    public int compareTo(JournalCollection other) {
        if (this.year > other.year)
            return 1;
        else
            return -1;
    }
}
