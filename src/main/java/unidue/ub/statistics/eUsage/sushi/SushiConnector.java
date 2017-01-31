package unidue.ub.statistics.eUsage.sushi;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.jdom2.Element;

import unidue.ub.statistics.media.journal.Publisher;

/**
 * Establishes and closes the connection to the SUSHI provider and prepares the necessary SOAP requests
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class SushiConnector {
    
    private final static String namespaceCounter = "http://www.niso.org/schemas/sushi/counter";
    
    private final static String namespaceSushi = "http://www.niso.org/schemas/sushi";

    private Element report = new Element("ReportDefinition",namespaceSushi);

    private static final Logger LOGGER = Logger.getLogger(SushiConnector.class);
    
    private static final LocalDate TODAY = LocalDate.now();
    
    private LocalDate endDate;
    
    private LocalDate startDate;
    
    int release = 4;
    
    private String type = "JR1";
    
    private Publisher publisher;
    
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * establishes a connection to the SUSHI provider.
     * 
     * @param publisher
     *            the SUSHI provider
     * 
     */
    public SushiConnector(Publisher publisher) {
        this.publisher = publisher;
        report = new Element("ReportDefinition",namespaceSushi);
        report.setAttribute("Release",String.valueOf(release));
        startDate = LocalDate.now().withMonth(1).withDayOfMonth(1);
        endDate = TODAY;
    }

    /**
     * retrieves the statistics as SOAP message.
     * 
     * @return the SOAP response message
     * @exception Exception thrown if anything, that can go wrong, goes wrong
     * 
     */
    public SOAPMessage getSushi() throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = publisher.getSushiURL();
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
        return soapResponse;
    }
    
    private SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("coun", namespaceCounter);
        envelope.addNamespaceDeclaration("sus", namespaceSushi);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement reportRequest = soapBody.addChildElement("ReportRequest", "coun");
        reportRequest.setAttribute("ID", publisher.getSushiRequestorID());
        reportRequest.setAttribute("Created", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        
        SOAPElement requestor = reportRequest.addChildElement("Requestor", "sus");
        SOAPElement requestorID = requestor.addChildElement("ID", "sus");
        requestorID.addTextNode(publisher.getSushiRequestorID());
        
        if (!publisher.getSushiRequestorName().isEmpty()) {
        SOAPElement requestorName = requestor.addChildElement("Name", "sus");
        requestorName.addTextNode(publisher.getSushiRequestorName());
        }
        
        if (!publisher.getSushiRequestorEmail().isEmpty()) {
        SOAPElement requestorEmail = requestor.addChildElement("Email", "sus");
        requestorEmail.addTextNode(publisher.getSushiRequestorEmail());
        }
        
        SOAPElement customerReference = reportRequest.addChildElement("CustomerReference", "sus");
        SOAPElement customerReferenceID = customerReference.addChildElement("ID","sus");
        customerReferenceID.addTextNode(publisher.getSushiCustomerReferenceID());
        
        if (!publisher.getSushiCustomerReferenceName().isEmpty()) {
        SOAPElement customerReferenceName = customerReference.addChildElement("Name","sus");
        customerReferenceName.addTextNode(publisher.getSushiCustomerReferenceName());
        }
        
        SOAPElement reportDefinition = reportRequest.addChildElement("ReportDefinition","sus");
        reportDefinition.setAttribute("Release", String.valueOf(release));
        reportDefinition.setAttribute("Name", type);
        
        SOAPElement filters = reportDefinition.addChildElement("Filters","sus");
        SOAPElement usageDataRange = filters.addChildElement("UsageDateRange","sus");
        
        SOAPElement begin = usageDataRange.addChildElement("Begin","sus");
        begin.addTextNode(startDate.format(dtf));
        
        SOAPElement end = usageDataRange.addChildElement("End","sus");
        end.setTextContent(endDate.format(dtf));

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "SushiService:GetReportIn");

        soapMessage.saveChanges();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        LOGGER.info(out.toString());
        
        return soapMessage;
    }
    
    /**
     * sets the release of the SUSHI report 
     * 
     * @param release
     *            the version of the report
     * 
     */
    public void setRelease(int release) {
        this.release = release;
        
    }
    
    /**
     * sets the type of SUSHI report
     * 
     * @param type
     *            the type of the report
     * 
     */
    public void setType(String type) {
        this.type = type;
        report.setAttribute("Name",this.type);
    }
    
    /**
     *  sets the start date
     * 
     * @param startDate
     *            the start date
     * 
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    /**
     * sets the end date
     * 
     * @param endDate
     *            the end date
     * 
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
