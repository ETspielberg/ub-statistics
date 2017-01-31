package unidue.ub.statistics.alert;

import static org.quartz.TriggerKey.triggerKey;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
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
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Saves the alert control file to disk.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class AlertDefineServlet extends FachRefServlet {

    private static final Logger LOGGER = Logger.getLogger(AlertDefineServlet.class);

    private final static String userDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    private static final long serialVersionUID = 1L;

    /**
     * Takes a XEditor submission and writes the corresponding alert control file as xml to disk
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     * @throws SchedulerException thrown if problem during job registration occurs
     */
    protected void doGetPost(MCRServletJob job) throws ServletException, IOException, TransformerException, SAXException, SchedulerException {
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.hasRole("fachreferent")) {
            org.jdom2.Document xmlJDOM = (org.jdom2.Document) job.getRequest().getAttribute("MCRXEditorSubmission");
            MCRJDOMContent xml = new MCRJDOMContent(xmlJDOM);
            String id = getParameter(job, "id");
            boolean performAlert = getParameter(job, "performAlert").equals(true);
            boolean performReader = getParameter(job, "performReader").equals(true);
            String filename = "";
            if (id.isEmpty())
                id = String.valueOf(System.currentTimeMillis());
            filename = id + ".xml";
            String who = job.getRequest().getUserPrincipal().getName();
            File alertFolder = new File(userDir + "/" + who + "/alert");
            if (!alertFolder.exists())
                alertFolder.mkdir();
            File outputFile = new File(userDir + "/" + who + "/alert", filename);
            if (!outputFile.exists())
                outputFile.createNewFile();
            xml.sendTo(outputFile);
            LOGGER.info("written file " + outputFile);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (performAlert || performReader) {
                String alertID = "alert" + id;
                scheduler.unscheduleJob(triggerKey(alertID, who));
                JobDetail jobDetail = JobBuilder.newJob(AlertSender.class).withIdentity(alertID, who).build();
                jobDetail.getJobDataMap().put("alertControl", alertID);
                jobDetail.getJobDataMap().put("who", who);
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(alertID, who).withSchedule(CronScheduleBuilder.cronSchedule("0 0 5 * * ?")).startNow().build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
            job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/hitlists");
        } else {
            Element output = new Element("error");
            output.addContent((new Element("message")).addContent("error.noPermission"));
            sendOutput(job,output);
        }
    }
}
