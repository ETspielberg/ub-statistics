package unidue.ub.statistics.blacklist;

import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.jdom2.Element;

/**
 * Plain old java object holding an data of analysis to be ignored upon analysis. The
 * fields can be persisted.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class Ignored {

    @Id
    @GeneratedValue
    private long id;

	private String identifier;
	
	private String shelfmark;
	
	private String type;
	
	@Lob
	private String comment;
	
	@Lob
    private String mab;
	
	private long timestamp;
	
	private long expire;
	
	private String who;
	
	/**
	 * general constructor and initialization
	 */
	public Ignored() {
		comment = "";
		timestamp = System.currentTimeMillis();
		expire = 0;
		identifier = "";
		who = "";
		mab = "";
		shelfmark = "";
		type = "aleph.eventType.deletion";
	}

	/**
	 * retunrs the type
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * sets the type
     * @param type the type to set
     * @return the updated object
     */
    public Ignored setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * returns the shelfmark
     * @return the callNo
     * 
     */
    public String getShelfmark() {
        return shelfmark;
    }

    /**
     * sets the shelfmark
     * @param shelfmark the shelfmark to set
     * @return the updated object
     */
    public Ignored setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
        return this;
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
     * @return the updated object
     */
    public Ignored setMab(String mab) {
        this.mab = mab;
        return this;
    }

    /**
	 * returns the comment connected to the ignored document(s)
	 * 
	 * @return comment the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * returns the author of the ignored document(s)
	 * 
	 * @return who of the ignorance
	 */
	public String getWho() {
		return who;
	}

	/**
	 * sets the author of the ignored document(s)
	 * 
	 * @param who of the ignorance
	 * @return Ignored the updated object
	 */
	public Ignored setWho(String who) {
		this.who = who;
		return this;
	}

	/**
	 * sets the comment connected to the ignored document(s)
	 * 
	 * @param comment the comment
	 * @return Ignored the updated object
	 */
	public Ignored setComment(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * retrieves the timestamp when the ignorance was set
	 * 
	 * @return timestamp the timestamp
	 */
	public double getTimestamp() {
		return timestamp;
	}

	/**
	 * set the timestamp when the ignorance was set
	 * 
	 * @param timestamp the timestamp
	 * @return Ignored the updated object
	 */
	public Ignored setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	/**
	 * retrieves the timestamp when the ignorance expires
	 * 
	 * @return expire the expire timestamp
	 */
	public double getExpire() {
		return expire;
	}

	/**
	 * set the timestamp when the ignorance expires
	 * 
	 * @param expire the expire timestamp
	 * @return Ignored the updated object
	 */
	public Ignored setExpire(long expire) {
		this.expire = expire;
		return this;
	}

	/**
	 * returns the identifier connected to the ignored document(s) (document number or basic shelfmark)
	 * 
	 * @return identifier the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * returns the identifier connected to the ignored document(s) (document number or basic shelfmark)
	 * 
	 * @param identifier the identifier
	 * @return Ignored the updated object
	 */
	public Ignored setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	/**
	 * adds the analysis org.jdom2.element to the desired parent element
	 * 
	 * @param parent
	 *            the parent org.jdom2.element the analysis shall be added to
	 */
	public void addToOutput(Element parent) {
	    Element ignoredXML = new Element("ignored");
	    if (mab != null) {
	        ignoredXML.addContent(new Element("mab").setText(mab));
	    } else
	        ignoredXML.addContent(new Element("mab").setText(""));
	    if (shelfmark != null)
	        ignoredXML.addContent(new Element("shelfmark").setText(shelfmark));
	    else 
	        ignoredXML.addContent(new Element("shelfmark").setText(""));
	    if (type == null || type.equals(""))
            ignoredXML.addContent(new Element("type").setText("aleph.eventType.deletion"));
        else 
            ignoredXML.addContent(new Element("type").setText(type));
	    ignoredXML.addContent(new Element("identifier").setText(identifier));
	    ignoredXML.addContent(new Element("comment").setText(comment));
	    String timestampDate = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
	    ignoredXML.addContent(new Element("timestamp").setText(timestampDate));
	    timestampDate = new SimpleDateFormat("dd-MM-yyyy").format(expire);
	    ignoredXML.addContent(new Element("expire").setText(timestampDate));
	    parent.addContent(ignoredXML);
	}
	
}
