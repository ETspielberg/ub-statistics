package unidue.ub.statistics.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import unidue.ub.statistics.frontend.FachRefServlet;
import unidue.ub.statistics.stock.GHBPersistence;

/**
 * Builds an index of notations to allow retrieval of regions by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/admin/buildNotationIndex")
public class NotationDAO extends FachRefServlet implements Job {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger LOGGER = Logger.getLogger(NotationDAO.class);

    /**
     * updates the index of <code>Notation</code> and <code>NotationsPerSubject</code>
     * 
     * @param job
    *            <code>MCRServletJob</code>
     */
    public void doGetPost(MCRServletJob job) throws IOException, JDOMException {
    	try {
            buildIndexfromGHBSYS();
            LOGGER.info("built CollectionIndex");
        } catch (IOException | JDOMException e) {
            LOGGER.info("error building Index");
        }
    	job.getResponse().sendRedirect("overview?message=builtNotationIndex");
    }

    /**
     * execute method to be called by the quartz framework.
     * 
     * @param context
     *            job execution context from the quartz framework
     * 
     */
    @Override
    public void execute(JobExecutionContext context) {
        try {
            buildIndexfromGHBSYS();
            LOGGER.info("built NotationIndex");
        } catch (IOException | JDOMException e) {
            LOGGER.info("error building Index");
        }
    }

    /**
     * retrieves a list of <code>Notation</code>-objects from the index of notations. The notations may have different notations as well as regions, separated by ',' and '-' , respectively.
     * 
     * @param notations
     *            a string containing the first and last notation, separated by a '-'
     * @return notationsFound a list of <code>Notation</code> objects.
     * 
     */
    public static List<Notation> getNotationsList(String notations) {
        List<String> notationsRanges = new ArrayList<>();
        if (!notations.contains(","))
            notationsRanges.add(notations);
        else {
            StringTokenizer tok = new StringTokenizer(notations, ",");
            while (tok.hasMoreTokens()) {
                String region = tok.nextToken();
                notationsRanges.add(region);
            }
        }
        List<Notation> notationsFound = new ArrayList<>();
        for (String range : notationsRanges) {
            Hashtable<String, String> notationsRange = new Hashtable<String, String>();
            if (range.contains("-")) {
                notationsRange.put("von", range.substring(0, range.indexOf("-")).trim());
                notationsRange.put("bis", range.substring(range.indexOf("-") + 1).trim());
            } else
                notationsRange.put("von", range);
            List<Notation> found = getNotationsList(notationsRange);
            if (found != null)
            notationsFound.addAll(found);
        }
        return notationsFound;
    }

    /**
     * retrieves a list of <code>Notation</code>-objects from the index of notations.
     * 
     * @param notationsRange
     *            a <code>Hashtable</code> containing the first ('von') and the last notation ('bis')
     * @return notationsFound a list of <code>Notation</code> objects
     * 
     */
    public static List<Notation> getNotationsList(Hashtable<String, String> notationsRange) {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        List<Notation> notationsFound = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Notation> q = cb.createQuery(Notation.class);
        Root<Notation> c = q.from(Notation.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (notationsRange.size() == 1) {
            predicates.add(cb.equal(c.<String> get("notation"), notationsRange.get("von")));
        } else {
            predicates.add(cb.between(c.<String> get("notation"), notationsRange.get("von"), notationsRange.get("bis")));
        }
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Notation> query = em.createQuery(q);
        if (query.getResultList().size() == 0) {
            em.close();
            LOGGER.info("found no entries in database");
            notationsFound = null;
            return notationsFound;
        } else {
            notationsFound = query.getResultList();
            em.close();
            return notationsFound;
        }
    }

    /**
     * retrieves a the range of notations for a given subject ID
     * 
     * @param subjectID
     *            the subject ID
     * @return String a string containing the first and last notation, separated by a '-'
     * 
     */
    public static String getNotationsRange(String subjectID) {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        Query query = em.createQuery("SELECT notations FROM NotationsPerSubject WHERE subjectID = ?");
        query.setParameter(1, subjectID.trim());
        return (String) query.getSingleResult();
    }

    /**
     * retrieves a list of <code>Notation</code>-objects from the index of notations by a given subject ID.
     * 
     * @param subjectID
     *            the subject ID
     * @return notationsFound a list of <code>Notation</code> objects.
     * 
     */
    public static List<Notation> getNotationsForSubjectID(String subjectID) {
        String notationsRange = getNotationsRange(subjectID);
        List<Notation> notations = getNotationsList(notationsRange);
        return notations;
    }

    /**
     * reads in the notations from a list of systematik.xml files and builds the corresponding index.
     * 
     * @exception IOException exception while reading systematik.xml file from disk
     * @exception JDOMException exception upon parsing the systematik.xml file
     */
    public void buildIndexfromGHBSYS() throws IOException, JDOMException {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            em.createQuery("DELETE FROM Notation").executeUpdate();
            em.createQuery("DELETE FROM NotationsPerSubject").executeUpdate();
        } catch (Exception exc1) {
            LOGGER.info("error in deleting notations (per subject)");
        }
        tx.commit();
        List<String> filenames = GHBPersistence.listFiles();
        List<Notation> notations = new ArrayList<>();
        List<NotationsPerSubject> notationsPerSubjects = new ArrayList<>();
        Element current;

        for (String file : filenames) {
            current = GHBPersistence.loadFile(file);
            NotationsPerSubject notationsPerSubject = new NotationsPerSubject();
            notationsPerSubject.setDescription(current.getChildText("bez"));
            notationsPerSubject.setNotations(current.getAttributeValue("von") + "-" + current.getAttributeValue("bis"));
            notationsPerSubject.setSubjectID(current.getAttributeValue("zahl"));
            notationsPerSubjects.add(notationsPerSubject);
            Iterator<Element> descendants = current.getDescendants(new ElementFilter());
            while (descendants.hasNext()) {
                Element descendant = descendants.next();
                if (descendant.getName().equals("stelle")) {
                    notations.add(new Notation().setNotation(descendant.getAttributeValue("code")).setDescription(descendant.getChildText("bez")));
                }
            }

        }
        //build new index
        tx.begin();
        for (Notation notation : notations)
            em.persist(notation);
        for (NotationsPerSubject notationsPerSubject : notationsPerSubjects)
            em.persist(notationsPerSubject);
        tx.commit();
        em.close();
    }
}
