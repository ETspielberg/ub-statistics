package unidue.ub.statistics.stockcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;

import unidue.ub.statistics.admin.NotationDAO;

/**
 * Holds all the information for the analysis of stock regions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockControlProperties {
	
	private String subjectID;
	
	private String systemCode;
	
	private String collections;
	
	private String materials;
	
	private Integer yearsToAverage;
	
	private Integer minimumYears;
	
	private Double staticBuffer;
	
	private Double variableBuffer;
	
	private Integer threshold;
	
	private String deletionMailBcc;
	
	private String stockControl;
	
	private Integer yearsOfRequests;
	
	private Integer minimumDaysOfRequest;
	
	private Double blacklistExpire;
	
	private boolean groupedAnalysis;

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	/**
     * building stock control properties with default values
     * 
     * 
     * @param stockControl
     *            stock control key
     * @param author logged in user
     * @exception InvalidPropertiesFormatException exception while parsing properties file
	 * @exception IOException exception while reading file from disk
	 */
	public StockControlProperties(String stockControl, String author) throws InvalidPropertiesFormatException, IOException {
		this.stockControl = stockControl;
		String stockControlFilename = stockControl + ".xml";
		File stockControlFile;
		if (author.equals("guest"))
		    stockControlFile = new File(userDir, "default.xml");
		else
		    stockControlFile = new File(userDir + "/" + author + "/stockControl", stockControlFilename);
		FileInputStream stockControlInput = new FileInputStream(stockControlFile);
		Properties stockControlProperties = new Properties();
		stockControlProperties.loadFromXML(stockControlInput);
		stockControlInput.close();
		collections = stockControlProperties.getProperty("collections").trim();
		materials = stockControlProperties.getProperty("materials").trim();
		subjectID = stockControlProperties.getProperty("subjectID").trim();
		systemCode = stockControlProperties.getProperty("systemCode").trim();
		minimumYears = Integer.parseInt(stockControlProperties.getProperty("minimumYears"));
		staticBuffer = Double.parseDouble(stockControlProperties.getProperty("staticBuffer"));
		setGroupedAnalysis("true".equals(stockControlProperties.getProperty("groupedAnalysis", "false")));
		setBlacklistExpire(Double.parseDouble(stockControlProperties.getProperty("blacklistExpire", "2")));
		//if (staticBuffer == 0) staticBuffer = 0.001;
		variableBuffer = Double.parseDouble(stockControlProperties.getProperty("variableBuffer"));
		deletionMailBcc = stockControlProperties.getProperty("deletionMailBcc").trim();
		threshold = Integer.parseInt(stockControlProperties.getProperty("threshold"));
		yearsToAverage = Integer.parseInt(stockControlProperties.getProperty("yearsToAverage"));
		yearsOfRequests = Integer.parseInt(stockControlProperties.getProperty("yearsOfRequests"));
		minimumDaysOfRequest = Integer.parseInt(stockControlProperties.getProperty("minimumDaysOfRequest"));
	}

	/**
	 * returns the standard number of years on the blacklist before items are considered again
	 * 
	 * @return blacklistExpire the number of years on the blacklist
	 */
	public Double getBlacklistExpire() {
		return blacklistExpire;
	}

	/**
	 * sets the standard number of years on the blacklist before items are considered again
	 * 
	 * @param blacklistExpire the number of years on the blacklist
	 */
	public void setBlacklistExpire(Double blacklistExpire) {
		this.blacklistExpire = blacklistExpire;
	}

	/**
	 * sets the years used for the analysis
	 * 
	 * @param yearsToAverage the years for the analysis
	 */
	public void setSCYearsToAverage(int yearsToAverage) {
		this.yearsToAverage = yearsToAverage;
	}
	
	/**
	 * sets the minimum number of days for a request to be analyzed
	 * 
	 * @param minimumDaysOfRequest the minimum number of days for requests
	 */
	public void setSCMinimumDaysOfRequest(int minimumDaysOfRequest) {
        this.minimumDaysOfRequest = minimumDaysOfRequest;
    }
	
	/**
	 * sets the years in which requests are analyzed
	 * 
	 * @param yearsOfRequests the years for requests
	 */
	public void setSCYearsOfRequests(int yearsOfRequests) {
        this.yearsOfRequests = yearsOfRequests;
    }

	/**
	 * sets the collections to be analyzed
	 * 
	 * @param collections the collections (separated by blanks)
	 */
	public void setSCCollections(String collections) {
		this.collections = collections;
	}

	/**
	 * sets the materials to be analyzed
	 * 
	 * @param materials materials (separated by blanks)
	 */
	public void setSCMaterials(String materials) {
		this.materials = materials;
	}

	/**
	 * sets the notation to be analyzed
	 * 
	 * @param systemCode the notation
	 */
	public void setSCSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	/**
	 * sets the subject (ID according to GHBSYS) to be analyzed
	 * 
	 * @param subjectID subject ID
	 */
	public void setSCSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
	
	/**
	 * sets the stock control key
	 * 
	 * @param stockControl stock control key
	 */
	public void setStockControl(String stockControl) {
		this.stockControl = stockControl;
	}

	/**
	 * returns the stock control key
	 * 
	 * @return stockControl stock control key
	 */
	public String getStockControl() {
		return stockControl;
	}

	/**
	 * returns the collections to be analyzed
	 * 
	 * @return collections the collections (separated by blanks)
	 */
	public String getSCCollections() {
		return collections;
	}

	/**
	 * returns the materials to be analyzed
	 * 
	 * @return materials the materials (separated by blanks)
	 */
	public String getSCMaterials() {
		return materials;
	}

	/**
	 * returns the subject (ID according to GHBSYS) to be analyzed
	 * 
	 * @return subjectID subject ID
	 */
	public String getSCSubjectID() {
		return subjectID;
	}

	/**
	 * returns the notation to be analyzed
	 * 
	 * @return systemCode the notation
	 */
	public String getSCSystemCode() {
		return systemCode;
	}

	/**
	 * returns the BCC recipient of the deletion mail
	 * 
	 * @return deletionMailBcc the BCC recipient
	 */
	public String getDeletionMailBcc() {
		return deletionMailBcc;
	}

	/**
	 * returns the static buffer used to calculate the proposed deletion
	 * 
	 * @return staticBuffer the staticBuffer
	 */
	public Double getSCStaticBuffer() {
		return staticBuffer;
	}

	/**
	 * returns the variable buffer used to calculate the proposed deletion
	 * 
	 * @return variableBuffer the variable buffer
	 */
	public Double getSCVariableBuffer() {
		return variableBuffer;
	}

	/**
	 * returns the years used for the analysis
	 * 
	 * @return yearsToAverage the years used for the analysis
	 */
	public Integer getSCYearsToAverage() {
		return yearsToAverage;
	}

	/**
	 * returns the threshold for the proposed deletions
	 * 
	 * @return threshold the threshold
	 */
	public Integer getSCThreshold() {
		return threshold;
	}

	/**
	 * returns the minimum number of years an item has to be in stock to be considered for deletion
	 * 
	 * @return minimumYears the minimum number of years
	 */
	public Integer getSCMinimumYears() {
		return minimumYears;
	}
	
	/**
	 * returns the years in which requests are analyzed
	 * 
	 * @return yearsOfRequests years for requests
	 */
	public Integer getSCYearsOfRequests() {
	    return yearsOfRequests;
	}
	
	/**
	 * returns the minimum number of days for a request to be analyzed
	 * 
	 * @return minimumDaysOfRequest the  minimum number of days for a request
	 */
	public Integer getSCMinimumDaysOfRequest() {
	    return minimumDaysOfRequest;
	}

	/**
	 * attaches an XML version of the stock control properties to the parent xml node
	 * 
	 * @param parent the parent xml node
	 */
	public void addStockControlToOutput(Element parent) {
	    Element stockControlProps = new Element("stockControlProperties");
	    
	    Element stockControlXML = new Element("stockControl");
	    stockControlXML.addContent(stockControl);
	    stockControlProps.addContent(stockControlXML);
        
        Element scCollectionsXML = new Element("collections");
		if (!collections.isEmpty())
			scCollectionsXML.addContent(collections);
		else
			scCollectionsXML.addContent("all");
		stockControlProps.addContent(scCollectionsXML);

		Element scMaterialsXML = new Element("materials");
		if (!materials.equals(""))
			scMaterialsXML.addContent(materials);
		else
			scMaterialsXML.addContent("all");
		stockControlProps.addContent(scMaterialsXML);

		Element scSubjectIDXML = new Element("subjectID");
		scSubjectIDXML.addContent(subjectID);
		stockControlProps.addContent(scSubjectIDXML);

		Element scSsystemCodeXML = new Element("systemCode");
		scSsystemCodeXML.addContent(systemCode);
		stockControlProps.addContent(scSsystemCodeXML);

		Element scMinimumYearsXML = new Element("minimumYears");
		scMinimumYearsXML.addContent(minimumYears.toString());
		stockControlProps.addContent(scMinimumYearsXML);

		Element scYearsToAverageXML = new Element("yearsToAverage");
		scYearsToAverageXML.addContent(yearsToAverage.toString());
		stockControlProps.addContent(scYearsToAverageXML);
		
		Element scGroupedAnalysisXML = new Element("groupedAnalysis");
		scGroupedAnalysisXML.addContent(String.valueOf(groupedAnalysis));
        stockControlProps.addContent(scGroupedAnalysisXML);
		
		Element scStaticBufferXML = new Element("staticBuffer");
		scStaticBufferXML.addContent(staticBuffer.toString());
		stockControlProps.addContent(scStaticBufferXML);

		Element scVariableBufferXML = new Element("variableBuffer");
		scVariableBufferXML.addContent(variableBuffer.toString());
		stockControlProps.addContent(scVariableBufferXML);

		Element scThresholdXML = new Element("threshold");
		scThresholdXML.addContent(threshold.toString());
		stockControlProps.addContent(scThresholdXML);
		
		Element scYearsOfRequestsXML = new Element("yearsOfRequests");
        scYearsOfRequestsXML.addContent(yearsOfRequests.toString());
        stockControlProps.addContent(scYearsOfRequestsXML);
        
        Element scMinimumDaysOfRequestXML = new Element("minimumDaysOfRequest");
        scMinimumDaysOfRequestXML.addContent(minimumDaysOfRequest.toString());
        stockControlProps.addContent(scMinimumDaysOfRequestXML);
        
		parent.addContent(stockControlProps);
	}
	
	private static String getParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }
    
	/**
	 * reads the parameters from the http request to modify the corresponding fields of the default stock control properteis
	 * 
	 * @param req the http-request
	 * @return scp stock control properties
	 * @exception InvalidPropertiesFormatException exception while parsing properties file
	 * @exception IOException exception while reading file from disk
	 */
	public static StockControlProperties buildSCPFromRequest(HttpServletRequest req) throws InvalidPropertiesFormatException, IOException {
        String stockControl;
        String systemCode = getParameter(req, "systemCode");
        String collections = getParameter(req, "collections");
        String materials = getParameter(req, "materials");
        String subjectID = getParameter(req, "subjectID");
        boolean groupedAnalysis = "true".equals(getParameter(req, "groupedAnalysis"));
        stockControl = getParameter(req, "stockControl");
        StockControlProperties scp;

        if (!stockControl.isEmpty())
            scp = new StockControlProperties(stockControl, req.getUserPrincipal().getName());
        else {
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            if (currentUser.isAuthenticated())
            scp = new StockControlProperties("default",req.getUserPrincipal().getName());
            else scp = new StockControlProperties("default","guest");
            if (!systemCode.isEmpty()) {
                scp.setSCSystemCode(systemCode);
                //TO DO: get corresponding SubjectID for a given SystemCode
                scp.setSCSubjectID("00");
                stockControl = systemCode;
            } else if (!subjectID.isEmpty()) {
                scp.setSCSubjectID(subjectID);
                scp.setSCSystemCode(NotationDAO.getNotationsRange(subjectID));
                stockControl = subjectID;
            } else {
                stockControl = "";
            }
            if (!collections.isEmpty()) {
                scp.setSCCollections(collections);
                stockControl = stockControl + "_" + collections.replace(" ", "_").replace("?","");
            }
            if (!materials.isEmpty()) {
                scp.setSCMaterials(materials);
                stockControl = stockControl + "_" + materials;
            }
            scp.setGroupedAnalysis(groupedAnalysis);
            scp.setStockControl(stockControl);
        }
        return scp;
    }

	/**
	 * tests, whether to perform a grouped analysis
	 * 
	 * @return isGroupedAnalysis true, if grouped analysis is to be performed
	 */
	public boolean isGroupedAnalysis() {
        return groupedAnalysis;
    }

	/**
	 * sets boolean, whether to perform a grouped analysis
	 * 
	 * @param groupedAnalysis true, if grouped analysis is to be performed
	 */
	public void setGroupedAnalysis(boolean groupedAnalysis) {
        this.groupedAnalysis = groupedAnalysis;
    }

}
