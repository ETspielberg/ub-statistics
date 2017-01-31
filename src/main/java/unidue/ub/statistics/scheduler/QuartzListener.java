package unidue.ub.statistics.scheduler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

import unidue.ub.statistics.admin.NotationDAO;

/**
 * Quartz listener to initialize the necessary actions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebListener
public class QuartzListener extends QuartzInitializerListener {
    
    private static final Logger LOGGER = Logger.getLogger(QuartzInitializerListener.class);
    
    private Scheduler scheduler;

	/**
	 * starts the scheduler
	 * 
	 * @param sce
	 *            the servlet context event
	 * 
	 */
	@Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        ServletContext ctx = sce.getServletContext();
        StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);
        try {
            scheduler = factory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(NotationDAO.class).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("notations").withSchedule(
                    CronScheduleBuilder.cronSchedule("0 0 1 * * ? *")).startNow().build();
            scheduler.scheduleJob(jobDetail, trigger);
            /*JobDetail jobDetail2 = JobBuilder.newJob(CollectionDAO.class).build();
            Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("collections").withSchedule(
                    CronScheduleBuilder.cronSchedule("0 0 1 * * ? *")).startNow().build();
            scheduler.scheduleJob(jobDetail2,trigger2);
            scheduler.start();*/
        } catch (Exception e) {
            LOGGER.info("There was an error scheduling the job.", e);
        }
    }
	
	/**
	 * stops the scheduler
	 * 
	 * @param servletContext
	 *            the servlet context 
	 * 
	 */
	@Override
    public void contextDestroyed(ServletContextEvent servletContext) {
            System.out.println("Context Destroyed");
            try 
            {
                    scheduler.shutdown();
            } 
            catch (SchedulerException e) 
            {
                    LOGGER.info("There was an error shuting the scheduler down.");
            }
    }


}
