package unidue.ub.statistics.alert;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;

import unidue.ub.statistics.admin.NotationDAO;

/**
 * Holds the information for the alerting services
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class AlertControl {
	
	private String alertControl;
	
	private String subjectID;
	
	private String notationRange;
	
	private String name;
	
	private boolean performAlert;
	
	private boolean performReader;
	
	private Double thresholdQuotient;
	
	private Double thresholdQuotientAlert;
	
	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}
	
	/**
	 * general constructor and initialization
	 */
	public AlertControl() {
		alertControl = String.valueOf(System.currentTimeMillis());
		subjectID = "";
		notationRange = "";
		name = "";
		performAlert = false;
		performReader = false;
		thresholdQuotient = 0.0;
		thresholdQuotientAlert = 0.0;
	}

	/**
	 * sets the boolean whether or not to perform the alert
	 * @param performAlert true, if the alert should be performed
	 */
	public void setPerformAlert(Boolean performAlert) {
		this.performAlert = performAlert;
	}
	
	/**
     * sets the boolean whether or not to perform the alert
     * @param performAlert true, if the alert should be performed
     */
    public void setPerformReader(Boolean performReader) {
        this.performReader = performReader;
    }

	/**
	 * sets the threshold for the quotient (number of requested items / number of lendable items)
	 * @param thresholdQuotient the thresholdQuotient to set
	 */
	public void setThresholdQuotient(Double thresholdQuotient) {
		this.thresholdQuotient = thresholdQuotient;
	}

	/**
	 * returns the range of notations 
	 * @return the notationRange
	 */
	public String getNotationRange() {
		return notationRange;
	}

	
	/**
	 * sets the range of notations
	 * @param notationRange the notationRange to set
	 */
	public void setNotationRange(String notationRange) {
		this.notationRange = notationRange;
	}
	
	/**
	 * returns the alert identifier
	 * @return the alertControl
	 */
	public String getAlertControl() {
		return alertControl;
	}

	/**
	 * sets the alert identifier
	 * @param alertControl the alertControl to set
	 */
	public void setAlertControl(String alertControl) {
		this.alertControl = alertControl;
	}

	/**
	 * returns the subject ID
	 * @return the subjectID
	 */
	public String getSubjectID() {
		return subjectID;
	}

	/**
	 * sets the subject ID
	 * @param subjectID the subjectID to set
	 */
	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	/**
	 * returns the name of the profile
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the profile
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the boolean, whether to send the alert for new data 
	 * @return the perform
	 */
	public Boolean getPerformAlert() {
		return performAlert;
	}
	

    /**
     * returns the boolean, whether to send the alert for new data 
     * @return the perform
     */
    public Boolean getPerformReader() {
        return performReader;
    }

	/**
	 * returns the the threshold for the quotient (number of requested items / number of lendable items)
	 * @return the thresholdQuotient
	 */
	public Double getThresholdQuotient() {
		return thresholdQuotient;
	}
	
	/**
	 * returns the the threshold for the quotient (number of requested items / number of lendable items) for the alerting service
	 * @return the thresholdDuration
	 */
	public Double getThresholdQuotientAlert() {
		return thresholdQuotientAlert;
	}

	/**
	 * sets the the threshold for the quotient (number of requested items / number of lendable items) for the alerting service
	 * @param thresholdDuration the thresholdDuration to set
	 */
	public void setThresholdDuration(Double thresholdQuotientAlert) {
		this.thresholdQuotientAlert = thresholdQuotientAlert;
	}
	
	/**
	 * reads the parameters from the http request to modify the corresponding fields of the alert control. If the corresponding alertControl string is given
	 * the corresponding file is read from disk.
	 * 
	 * @param req the http-request
	 * @return AlertControl alert control
	 * @exception JDOMException exception while parsing file
	 * @exception IOException exception while reading file from disk
	 */
	public AlertControl buildFromHttpRequest(HttpServletRequest req) throws JDOMException, IOException {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
        	alertControl = getParameter(req,"readerControl");
        	String who = req.getUserPrincipal().getName();
        	if (!alertControl.isEmpty()) {
        		readFromDisk(alertControl, who);
        	} else {
        		if (!getParameter(req,"subjectID").isEmpty())
        			subjectID = getParameter(req,subjectID);
        		if (!getParameter(req,"notationRange").isEmpty())
        			notationRange = getParameter(req,notationRange);
        		name = "request based ad hoc look up";
        		if (!getParameter(req,"thresholdQuotient").isEmpty())
        			thresholdQuotient = Double.parseDouble(getParameter(req,"thresholdQuotient"));
        		if (!getParameter(req,"thresholdQuotientAlert").isEmpty())
        		    thresholdQuotientAlert = Double.parseDouble(getParameter(req,"thresholdQuotientAlert"));
        	}
        }
        return this;
	}
	
	/**
	 * builds the <code>AlertControl</code> by reading the file associated with the given alertControl from disk.
	 * 
	 * @param alertControl the alertControl string
	 * @param who the actual username
	 * @return AlertControl alert control
	 * @exception JDOMException exception while parsing file
	 * @exception IOException exception while reading file from disk
	 */
	public AlertControl readFromDisk(String alertControl, String who) throws JDOMException, IOException {
		File alertControlFile = new File(userDir + "/" + who + "/alert",alertControl + ".xml");
    	Element alertControlXML = new SAXBuilder().build(alertControlFile).detachRootElement().clone();
    	subjectID = alertControlXML.getChildText("subjectID");
    	this.alertControl = alertControl;
    	if (alertControlXML.getChild("notationRange") != null)
            notationRange = alertControlXML.getChildText("notationRange");
        else 
            notationRange = NotationDAO.getNotationsRange(subjectID);
    	name = alertControlXML.getChildText("name");
    	performAlert = alertControlXML.getChildText("performAlert").equals("true");
    	performReader = alertControlXML.getChildText("performReader").equals("true");
    	try {
    		String thresholdQuotientString = alertControlXML.getChildText("thresholdQuotient").trim();
    		if (thresholdQuotientString.contains(","))
    			thresholdQuotientString = thresholdQuotientString.replace(",", ".");
		    thresholdQuotient = Double.parseDouble(thresholdQuotientString);
    	} catch (Exception e) {
    	    thresholdQuotient = 0.0;
    	}
    	try {
    	    String thresholdQuotientAlertString = alertControlXML.getChildText("thresholdQuotientAlert").trim();
            if (thresholdQuotientAlertString.contains(","))
                thresholdQuotientAlertString = thresholdQuotientAlertString.replace(",", ".");
            thresholdQuotient = Double.parseDouble(thresholdQuotientAlertString);
    	} catch (Exception e2) {
    	    thresholdQuotientAlert = 0.0;
    	}
		return this;
	}
	
	private static String getParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }
	
	
	/**
	 * attaches an XML version of the alert controls to the parent xml node
	 * 
	 * @param parent the parent xml node
	 */
	public void addToOutput(Element parent) {
        Element alertControlElement = new Element("alert");
        alertControlElement.addContent(new Element("alertControl").addContent(alertControl));
        alertControlElement.addContent(new Element("name").addContent(name));
        alertControlElement.addContent(new Element("notationRange").addContent(notationRange));
        alertControlElement.addContent(new Element("subjectID").addContent(subjectID));
        alertControlElement.addContent(new Element("performAlert").addContent(String.valueOf(performAlert)));
        alertControlElement.addContent(new Element("performReader").addContent(String.valueOf(performReader)));
        alertControlElement.addContent(new Element("thresholdQuotient").addContent(String.valueOf(thresholdQuotient)));
        alertControlElement.addContent(new Element("thresholdQuotientAlert").addContent(String.valueOf(thresholdQuotientAlert)));
        parent.addContent(alertControlElement);
    }

}
