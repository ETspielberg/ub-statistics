package unidue.ub.statistics.frontend;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.json.JSONArray;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRSourceContent;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.common.content.transformer.MCRXSL2XMLTransformer;
import org.xml.sax.SAXException;

import unidue.ub.statistics.eUsage.CounterDAO;
import unidue.ub.statistics.media.journal.JournalTools;

/**
 * Delivers the JSON arrays containing the SUSHI usage data.
 *
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/journals/journalUsage")
public class JournalUsageServlet extends FachRefServlet {

    private static final long serialVersionUID = 1;

    private String issn;

    private static final Logger LOGGER = Logger.getLogger(JournalUsageServlet.class);

    /**
     * reads the necessary information from the http request, retrieves the data and builds the corresponding JSON arrays.
     *
     * @param job <code>MCRServletJob</code>
     * @exception IOException thrown upon the writing the result to the output
     * @exception TransformerException thrown upon rendering the output xml
     * @exception SAXException thrown upon building the output xml
     */
    protected void doGet(MCRServletJob job) throws ServletException, IOException, TransformerException, SAXException, JDOMException {
        HttpServletRequest req = job.getRequest();

        Element output = prepareOutput(job, "usageAnalysis", "journals", "journalUsage");

        String friendlyName = (String) req.getSession().getAttribute("friendlyName");
        output.setAttribute("loggedInAs", friendlyName);
        issn = getParameter(job, "issn");
        if (!issn.isEmpty()) {
            output.addContent(new Element("issn").setText(issn));
            String type = JournalTools.determineType(issn);
            if (type.equals("issn")) {
                String uri = "jop:genre=journal&sid=bib:ughe&pid=bibid%3DUGHE&issn=" + issn;
                MCRXSL2XMLTransformer transformer = new MCRXSL2XMLTransformer("xsl/ezb2periods.xsl");
                MCRContent jopResponse = MCRSourceContent.getInstance(uri);
                Element data = transformer.transform(jopResponse).asXML().detachRootElement().clone();
                List<Element> electronicSources = data.getChild("electronic").getChild("sources").getChildren("source");
                List<Element> printSources = data.getChild("print").getChild("sources").getChildren("source");
                JSONArray jsonRanges = new JSONArray();
                JSONArray jsonCategories = new JSONArray();
                JSONArray jsonDescription = new JSONArray();

                for (Element source : electronicSources) {
                    JSONArray jsonElectronicRange = new JSONArray();
                    jsonDescription.put(source.getChild("period").getText());
                    Set<Integer> years = JournalTools.getAvailableYears(source.getChild("period").getText());
                    Iterator<Integer> iterator = years.iterator();
                    while (iterator.hasNext())
                        jsonElectronicRange.put(iterator.next());
                    jsonRanges.put(jsonElectronicRange);
                    jsonCategories.put("elektronisch");
                }
                for (Element source : printSources) {
                    JSONArray jsonPrintRange = new JSONArray();
                    jsonDescription.put(source.getChild("period").getText());
                    Set<Integer> years = JournalTools.getAvailableYears(source.getChild("period").getText());
                    Iterator<Integer> iterator = years.iterator();
                    while (iterator.hasNext())
                        jsonPrintRange.put(iterator.next());
                    jsonCategories.put("print");
                    jsonRanges.put(jsonPrintRange);
                }

                Element json = new Element("json");
                if (CounterDAO.getCounters(issn) != null) {
                    output.addContent(data.clone());
                }
                json.addContent(new Element("data").setText(jsonRanges.toString()));
                json.addContent(new Element("description").setText(jsonDescription.toString()));
                json.addContent(new Element("categories").setText(jsonCategories.toString()));
                output.addContent(json);
            }
        }
        sendOutput(job, output);
        LOGGER.info("done.");
    }

}
