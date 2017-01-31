/**
 * 
 */
package unidue.ub.statistics.analysis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.jdom2.Element;

/**
 * Plain old java object holding the entrie of the requests hitlist. 
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class NRequests implements Cloneable {
	
	@Id
	@GeneratedValue
	private long id;

	@Lob
	private String mab;
	
	private String docNumber;
	
	private String callNo;
	
	private double ratio;
	
	private int NRequests;
	
	private int NItems;
	
	private int NLoans;
	
	private int NLendable;
	
	private int duration;
	
	private String alertControl;
	
	private boolean forAlert;
	
	private boolean forReader;
	
	private long timestamp;
	
	/**
	 * general constructor and initialization
	 */
	public NRequests() {
		docNumber = "";
		ratio = 1.0;
		callNo = "";
		NRequests = 0;
		NItems = 1;
		NLoans = 0;
		NLendable = 1;
		mab = "";
		duration = 1;
		alertControl = "";
		forAlert = false;
		forReader = false;
		timestamp = System.currentTimeMillis();
	}

	/**
	 * returns the shelfmark
	 * @return the callNo
	 */
	public String getCallNo() {
		return callNo;
	}

	/**
	 * returns the duration of requests
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * sets the duration
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * sets the shelfmark
	 * @param callNo the callNo to set
	 */
	public void setCallNo(String callNo) {
		this.callNo = callNo;
	}

	/**
	 * returns the bibliographic data
	 * @return the mab
	 */
	public String getMab() {
		return mab;
	}

	/**
	 * sets the bibliographic data
	 * @param mab the mab to set
	 */
	public void setMab(String mab) {
		this.mab = mab;
	}

	/**
	 * returns the document number for the manifestation 
	 * @return the docNumber
	 */
	public String getDocNumber() {
		return docNumber;
	}

	/**
	 * sets the document number for this manifestation
	 * @param docNumber the docNumber to set
	 */
	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	/**
	 * returns the ratio (number of requested items / number of lendable items)
	 * @return the ratio
	 */
	public double getRatio() {
		return ratio;
	}

	/**
	 * sets the ratio (number of requested items / number of lendable items)
	 * @param ratio the ratio to set
	 */
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	/**
	 * returns the number of requests
	 * @return the nRequests
	 */
	public int getNRequests() {
		return NRequests;
	}

	/**
	 * sets the number of requests
	 * @param nRequests the nRequests to set
	 */
	public void setNRequests(int nRequests) {
		NRequests = nRequests;
	}

	/**
	 * returns the number of items
	 * @return the nItems
	 */
	public int getNItems() {
		return NItems;
	}

	/**
	 * sets the number of items
	 * @param nItems the nItems to set
	 */
	public void setNItems(int nItems) {
		NItems = nItems;
	}

	/**
	 * returns the number of loans
	 * @return the nLoans
	 */
	public int getNLoans() {
		return NLoans;
	}

	/**
	 * sets the number of loans
	 * @param nLoans the nLoans to set
	 */
	public void setNLoans(int nLoans) {
		NLoans = nLoans;
	}

	/**
	 * returns the number of lendable items
	 * @return the nLendable
	 */
	public int getNLendable() {
		return NLendable;
	}

	/**
	 * sets the number of lendable items
	 * @param nLendable the nLendable to set
	 */
	public void setNLendable(int nLendable) {
		NLendable = nLendable;
	}
	
	/**
     * returns the timestamp
     * @return the timestamp
     */
    public long getNTimestamp() {
        return timestamp;
    }

    /**
     * sets the number of lendable items
     * @param nLendable the nLendable to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
	
	/**
     * returns the alert control 
     * @return the alert control
     */
    public String getAlertControl() {
        return alertControl;
    }

    /**
     * sets the number of alert control
     * @param alertControl the alert control to set
     */
    public void setAlertControl(String alertControl) {
        this.alertControl = alertControl;
    }
    
    /**
     * returns the boolean whether the entry is within the scope of an alert control
     * @return true, if the thresholds are surpassed
     */
    public boolean getForAlert() {
        return forAlert;
    }

    /**
     * sets the boolean whether the entry is within the scope of an alert control
     * @param forAlert true, if the thresholds are surpassed
     */
    public void setForAlert(boolean forAlert) {
        this.forAlert = forAlert;
    }
	
    /**
     * returns the boolean whether the entry is within the scope of a reader control
     * @return true, if the thresholds are surpassed
     */
    public boolean getForReader() {
        return forReader;
    }

    /**
     * sets the boolean whether the entry is within the scope of a reader control
     * @param forReader true, if the thresholds are surpassed
     */
    public void setForReader(boolean forReader) {
        this.forReader = forReader;
    }
    
	/**
	 * adds the entry to the parent element
	 * @param parent the parent org.jdom2.element to attach the data to.
	 */
	public void addOutput(Element parent) {
		Element nRequestElement = new Element("nRequest");
		nRequestElement.setAttribute("docNumber",docNumber);
		nRequestElement.setAttribute("callNo",callNo);
		nRequestElement.addContent(new Element("ratio").addContent(String.valueOf(ratio)));
		nRequestElement.addContent(new Element("NRequests").addContent(String.valueOf(NRequests)));
		nRequestElement.addContent(new Element("NItems").addContent(String.valueOf(NItems)));
		nRequestElement.addContent(new Element("NLoans").addContent(String.valueOf(NLoans)));
		nRequestElement.addContent(new Element("NLendable").addContent(String.valueOf(NLendable)));
		nRequestElement.addContent(new Element("duration").addContent(String.valueOf(duration)));
		nRequestElement.addContent(new Element("mab").addContent(String.valueOf(mab)));
		nRequestElement.addContent(new Element("forAlert").addContent(String.valueOf(forAlert)));
		nRequestElement.addContent(new Element("forReader").addContent(String.valueOf(forReader)));
		nRequestElement.addContent(new Element("alertControl").addContent(alertControl));
		nRequestElement.addContent(new Element("timestamp").addContent(String.valueOf(timestamp)));
        parent.addContent(nRequestElement);
	}

	public NRequests clone() {
	    NRequests clone = new NRequests();
	    clone.setAlertControl(alertControl);
	    clone.setCallNo(callNo);
	    clone.setDocNumber(docNumber);
	    clone.setDuration(duration);
	    clone.setMab(mab);
	    clone.setNItems(NItems);
	    clone.setNLendable(NLendable);
	    clone.setNLoans(NLoans);
	    clone.setNRequests(NRequests);
	    return clone;   
	}
} 