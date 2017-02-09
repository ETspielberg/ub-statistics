package unidue.ub.statistics.eUsage.sushi;

import static org.quartz.TriggerKey.triggerKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.media.journal.Publisher;
import unidue.ub.statistics.media.journal.PublisherDAO;
/** 
 * Defines a SUSHI profile for a SUSHI provider, collects all available reports since 2000 and registers a Quartz job to continually collect the SUSHI reports.
 * @author Eike Spielberg
 *
 */
@WebServlet("/fachref/eMedia/publisherDefine")
public class SushiProviderDefineServlet extends FachRefServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private static final Logger LOGGER = Logger.getLogger(SushiProviderDefineServlet.class);
	
	private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	/**
	 * receives the file from the http post request and saves the SUSHI provider in the database.
	 * In addition a quartz job is scheduled.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doPost(MCRServletJob job) throws Exception {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
		name = getParameter(job, "name");
		if (PublisherDAO.getPublisher(name) == null) {
		    buildPublisherFromRequest(job);
            try {
                getSushiData();
                registerScheduler();
                job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/eMedia/publisherManagement");
            } catch (Exception e) {
                Element output = new Element("error").addContent((new Element("message")).addContent("error.noSushiConnection"));
                sendOutput(job,output);
            }
 		} else {
				Element output = new Element("error").addContent((new Element("message")).addContent("error.publisherExistAlready"));
				sendOutput(job,output);

		}
		} else {
		Element output = new Element("error").addContent((new Element("message")).addContent("error.noPermission"));
		sendOutput(job,output);
		}
	}

	private void buildPublisherFromRequest(MCRServletJob job) {
		String sushiURL = getParameter(job, "sushiURL");
		String sushiRequestorID = getParameter(job, "sushiRequestorID");
		String sushiRequestorName = getParameter(job, "sushiRequestorName");
		String sushiRequestorEmail = getParameter(job, "sushiRequestorEmail");
		String sushiCustomerReferenceID = getParameter(job, "sushiCustomerReferenceID");
		String sushiCustomerReferenceName = getParameter(job, "sushiCustomerReferenceName");
		String sushiRelease = getParameter(job, "sushiRelease");

		Publisher publisher = new Publisher();
		publisher.setName(name);
		publisher.setSushiURL(sushiURL);
		publisher.setSushiCustomerReferenceID(sushiCustomerReferenceID);
		publisher.setSushiCustomerReferenceName(sushiCustomerReferenceName);
		publisher.setSushiRequestorEmail(sushiRequestorEmail);
		publisher.setSushiRequestorID(sushiRequestorID);
		publisher.setSushiRequestorName(sushiRequestorName);
		publisher.setSushiRelease(Integer.parseInt(sushiRelease));

		PublisherDAO.persistPublisher(publisher);
		LOGGER.info("publisher " + name + " added to database.");
	}
	
	private void getSushiData() throws Exception {
	    LocalDate today = LocalDate.now();
	    LocalDate startDate = LocalDate.parse("01-01-2000", dtf);
        LocalDate endDate;
	    if (today.getDayOfMonth() < 15)
	         endDate = today.minusMonths(1).withDayOfMonth(1).minusDays(1);
	    else
	        endDate = today.withDayOfMonth(1).minusDays(1);
	    String type = "JR1";
	    boolean collected = AskSushiServlet.collectData(startDate,endDate,name,type);
	    if (collected)
            LOGGER.info("updated usage for " + name);
        else 
            LOGGER.info("problems while updating " + name);
	}
	
	private void registerScheduler() throws SchedulerException {
	Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.unscheduleJob(triggerKey(name));
    JobDetail jobDetail = JobBuilder.newJob(AskSushiServlet.class).withIdentity(name).build();
    jobDetail.getJobDataMap().put("publisherName", name);
    Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(
         CronScheduleBuilder.cronSchedule("0 0 3 15 * ?")).startNow().build();
    scheduler.scheduleJob(jobDetail,trigger);
    }
}
