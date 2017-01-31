package unidue.ub.statistics.eUsage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * Helpful tools for handling of COUNTER reports
 * @author Eike Spielberg
 *
 */
public class CounterConverter {

    private static final Namespace namespaceSushiCounter = Namespace.getNamespace("http://www.niso.org/schemas/sushi/counter");

    private static final Namespace namespaceCounter = Namespace.getNamespace("http://www.niso.org/schemas/counter");

    private static final Namespace namespaceSOAP = Namespace.getNamespace("http://schemas.xmlsoap.org/soap/envelope/");

    private static final Logger LOGGER = Logger.getLogger(CounterConverter.class);

    /**
     * returns a list of <code>Counter</code> objects generated from the response of a SUSHI request.
     * @param sushi the SUSHI response
     * @return counters the list of COUNTER reports
     * @exception SOAPException thrown upon errors occurring parsing the SUSHI response
     * @exception IOException thrown upon errors occurring writing of the SUSHI response to the SAX-Buuilder
     * @exception JDOMException thrown upon errors parsing the xml structure of the SUSHI response
     */
    public static List<Counter> convertSOAPMessageToCounters(SOAPMessage sushi) throws SOAPException, IOException, JDOMException {
        List<Counter> counters = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sushi.writeTo(out);
        String sushiString = new String(out.toByteArray());
        SAXBuilder builder = new SAXBuilder();
        Document sushiDoc = builder.build(new StringReader(sushiString));
        Element sushiElement = sushiDoc.detachRootElement().clone();
        LOGGER.info(sushiElement.getChildren());
        Element report = sushiElement.getChild("Body", namespaceSOAP).getChild("ReportResponse", namespaceSushiCounter).getChild("Report", namespaceSushiCounter).getChild("Report", namespaceCounter);
        Element customer = report.getChild("Customer", namespaceCounter);
        List<Element> reportItems = customer.getChildren("ReportItems", namespaceCounter);
        for (Element item : reportItems) {
            LOGGER.info(item.getChild("ItemName", namespaceCounter).getValue());
            String fullname = item.getChild("ItemName", namespaceCounter).getValue();
            String publisher = item.getChild("ItemPublisher", namespaceCounter).getValue();
            String type = item.getChild("ItemDataType", namespaceCounter).getValue();
            List<Element> identifiers = item.getChildren("ItemIdentifier", namespaceCounter);
            String onlineISSN = "";
            String printISSN = "";
            String proprietary = "";
            for (Element identifier : identifiers) {
                if (identifier.getChild("Type", namespaceCounter).getValue().equals("Online_ISSN"))
                    onlineISSN = identifier.getChild("Value", namespaceCounter).getValue();
                else if (identifier.getChild("Type", namespaceCounter).getValue().equals("Print_ISSN"))
                    printISSN = identifier.getChild("Value", namespaceCounter).getValue();
                else if (identifier.getChild("Type", namespaceCounter).getValue().equals("Proprietary"))
                    proprietary = identifier.getChild("Value", namespaceCounter).getValue();
            }
            List<Element> itemPerformances = item.getChildren("ItemPerformance", namespaceCounter);
            for (Element itemPerformance : itemPerformances) {
                Element period = itemPerformance.getChild("Period", namespaceCounter);
                String startDate = period.getChild("Begin", namespaceCounter).getValue();
                List<Element> instances = itemPerformance.getChildren("Instance", namespaceCounter);
                Counter counter = new Counter();
                counter.setFullName(fullname).setPublisher(publisher).setType(type).setOnlineISSN(onlineISSN).setPrintISSN(printISSN).setAbbreviation(proprietary);

                counter.setYear(Integer.parseInt(startDate.substring(0, 4)));
                counter.setMonth(Integer.parseInt(startDate.substring(5, 7)));
                for (Element instance : instances) {
                    if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_html"))
                        counter.setHtmlRequests(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_pdf"))
                        counter.setPdfRequests(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_html_mobile"))
                        counter.setHtmlRequestsMobile(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_pdf_mobile"))
                        counter.setPdfRequestsMobile(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_ps"))
                        counter.setPsRequests(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_ps_mobile"))
                        counter.setPsRequestsMobile(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));
                    else if (instance.getChild("MetricType", namespaceCounter).getValue().equals("ft_total"))
                        counter.setTotalRequests(Integer.parseInt(instance.getChild("Count", namespaceCounter).getValue().trim()));

                    counters.add(counter);

                }
            }
        }
        return counters;
    }

}
