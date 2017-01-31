/**
 * 
 */
package unidue.ub.statistics.eUsage;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.jdom2.Element;
import org.joda.time.LocalDate;

/**
 * Stores the usage of electronic collections connected to individual subjects and the corresponding price
 * @author Spielberg
 *
 */
@Entity
public class CollectionUsagePerSubject {
    
    @Id
    @GeneratedValue
    private long id;
    
    private String collection;
    
    private String subject;
    
    private int year;
    
    private int usagePerSubject;
    
    private double pricePerSubject;
    
    public CollectionUsagePerSubject() {
        collection = "";
        subject = "";
        year = LocalDate.now().getYear();
        usagePerSubject = 0;
        pricePerSubject = 0.0;
    }

    /**returns the collection
     * @return the collection
     */
    public String getCollection() {
        return collection;
    }

    /**
     * returns the subject
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * returns the year
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * returns the usage per subject
     * @return the usagePerSubject
     */
    public int getUsagePerSubject() {
        return usagePerSubject;
    }
    
    /**
     * returns the price per subject
    * @return the pricePerSubject
    */
   public double getPriceePerSubject() {
       return pricePerSubject;
   }

    /**
     * sets the collection
     * @param collection the collection to set
     * @return CollectionUsagePerSubject the updated object
     */
    public CollectionUsagePerSubject setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    /**
     * sets the subject
     * @param subject the subject to set
     * @return CollectionUsagePerSubject the updated object
     */
    public CollectionUsagePerSubject setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * sets the collection usage per subject
     * @param year the year to set
     * @return CollectionUsagePerSubject the updated object
     */
    public CollectionUsagePerSubject setYear(int year) {
        this.year = year;
        return this;
    }

    /**
     * sets the usage per subject
     * @param usagePerSubject the usagePerSubject to set
     * @return CollectionUsagePerSubject the updated object
     */
    public CollectionUsagePerSubject setUsagePerSubject(int usagePerSubject) {
        this.usagePerSubject = usagePerSubject;
        return this;
    }
    
    /**
     * sets the price per subject
     * @param pricePerSubject the pricePerSubject to set
     * @return CollectionUsagePerSubject the updated object
     */
    public CollectionUsagePerSubject setPricePerSubject(double pricePerSubject) {
        this.pricePerSubject = pricePerSubject;
        return this;
    }
    
    /**
	 * adds the analysis org.jdom2.element to the desired parent element
	 * 
	 * @param parent
	 *            the parent org.jdom2.element the analysis shall be added to
	 */
	public void addToOutput(Element parent) {
        Element cups = new Element("collectionUsagePerSubject");
        cups.addContent(new Element("subject").setText(subject));
        cups.addContent(new Element("collection").setText(collection));
        cups.addContent(new Element("year").setText(String.valueOf(year)));
        cups.addContent(new Element("usagePerSubject").setText(String.valueOf(usagePerSubject)));

        DecimalFormat format = new DecimalFormat("#,##0.00;-#");
        cups.addContent(new Element("pricePerSubject").setText(format.format(pricePerSubject)));
        parent.addContent(cups);
    }
    
    

}
