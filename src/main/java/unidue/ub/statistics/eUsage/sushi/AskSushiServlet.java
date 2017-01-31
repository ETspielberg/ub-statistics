package unidue.ub.statistics.eUsage.sushi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import unidue.ub.statistics.eUsage.Counter;
import unidue.ub.statistics.eUsage.CounterConverter;
import unidue.ub.statistics.eUsage.CounterDAO;
import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.Publisher;
import unidue.ub.statistics.media.journal.PublisherDAO;

/**
 * Collects COUNTER usage statistics via the SUSHI standard.
 * 
 * @author Eike Spielberg
 * 
 */
@WebServlet("/fachref/eMedia/askSushi")
public class AskSushiServlet extends FachRefServlet implements Job {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AskSushiServlet.class);

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public AskSushiServlet() {
    }

    /**
     * execute method to be called by the quartz framework.
     * 
     * @param context
     *            job execution context from the quartz framework
     * 
     */
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        try {
            String publisherName = dataMap.getString("publisher");
            LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
            String type = "JR1";
            boolean persisted = collectData(startDate, endDate,publisherName,type);
            if (persisted)
                LOGGER.info("updated usage for " + publisherName);
            else 
                LOGGER.info("problems while updating " + publisherName);
        } catch (Exception e) {
            LOGGER.info("encountered error during fetching counter statistics");
        }
    }

    /**
     * manual collection of SUSHI report. The parameters startDate, endDate, type and publisher are to be provided as request parameters.
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * 
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        String start = getParameter(job, "startDate");
        LocalDate startDate = LocalDate.parse(start, dtf);
        String end = getParameter(job, "endDate");
        LocalDate endDate = LocalDate.parse(end, dtf);
        String type = getParameter(job, "type");
        String publisherName = getParameter(job, "publisher");
        boolean persisted = collectData(startDate, endDate,publisherName,type);
        if (persisted)
            job.getResponse().sendRedirect("/fachref/eMedia");
        else {
            Element output = new Element("error");
            output.addContent(new Element("message").setText("noSushiDataInDatabase"));
            sendOutput(job,output);
        }
    }
    
    /**
     * Method to collect SUSHI reports. 
     * 
     * @param startDate	the start date
     * @param endDate	the end date
     * @param publisherName	the name of the SUSHI provider
     * @param type	the type of report requested
     * 
     * @return true, if publisher for SUSHI request was found in the database, false if not. 
     * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
     * 
     */
    public static boolean collectData(LocalDate startDate, LocalDate endDate, String publisherName, String type) throws Exception {
        Publisher publisher = PublisherDAO.getPublisher(publisherName);
        if (publisher != null) {
            LOGGER.info("building SUSHI connection for publisher" + publisher.getName());
            SushiConnector connection = new SushiConnector(publisher);
            connection.setType(type);
            connection.setStartDate(startDate);           
            connection.setEndDate(endDate);
            SOAPMessage sushi = connection.getSushi();
            OutputStream out = new FileOutputStream(new File(publisher.getName() + ".xml"));
            sushi.writeTo(out);
            List<Counter> counters = CounterConverter.convertSOAPMessageToCounters(sushi);
            Collections.sort(counters);
            CounterDAO.persistCounter(counters);
            return true;
        } else
            return false;
    }
}
