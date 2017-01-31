/**
 * 
 */
package unidue.ub.statistics.analysis;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import unidue.ub.statistics.alert.AlertControl;

/**
 * Methods to save and access <code>EventAnalysis</code> by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class NRequestsDAO {
    
    private final static long millisPerMonth = 2592000000L;

	/**
     * Persists a single <code>NREquests</code> to the database
     * 
     * 
     * @param nRequests
     *            the number of requests to be persisted
     */
    public static void persistNRequest(NRequests nRequest) {
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(nRequest);
        tx.commit();
        em.close();
    }
    
    /**
     * persists a list of <code>NRequests</code> objects to the database
     * 
     * 
     * @param analyses	the list of <code>NRequests</code> objects to be persisted
     */
    public static void persistNRequests(List<NRequests> nRequests) {
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (NRequests nRequest : nRequests) {
           em.persist(nRequest);
        }
        tx.commit();
        em.close();
    }

    
    /**
     * retrieves a a list of <code>NRequests</code> for an <code>AlertControl</code> from the database
     * 
     * 
     * @param scp    the <code>AlertControl</code> used to select the entries
     * @param em    the entity manager for the database
     *            
     * @return the <code>NRequests</code> associated with the alert control  
     */
    public static List<NRequests> getEventAnalyses(AlertControl ac, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NRequests> q = cb.createQuery(NRequests.class);
        Root<NRequests> c = q.from(NRequests.class);
        q.select(c).where(cb.equal(c.<String>get("alertControl"), ac.getAlertControl()));
        TypedQuery<NRequests> query = em.createQuery(q);
        return query.getResultList();
    }
    
    /**
     * retrieves a a list of <code>NRequests</code> for an <code>AlertControl</code> and a given time range from the database
     * 
     * 
     * @param scp    the <code>AlertControl</code> used to select the entries
     * @param months the number of recent months the <code>NRequests</code> were persisted (calculated as 30 days)
     * @param em    the entity manager for the database
     *            
     * @return the <code>NRequests</code> associated with the alert control  
     */
    public static List<NRequests> getEventAnalyses(AlertControl ac, int numberOfMonths, EntityManager em) {
        long oldTimestamp = System.currentTimeMillis() - numberOfMonths * millisPerMonth;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NRequests> q = cb.createQuery(NRequests.class);
        Root<NRequests> c = q.from(NRequests.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String>get("alertControl"), ac.getAlertControl()));
        predicates.add(cb.ge(c.<Long>get("timestamp"), oldTimestamp));
        q.select(c).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<NRequests> query = em.createQuery(q);
        return query.getResultList();
    }
    
    /**
     * retrieves a a list of <code>NRequests</code> for an <code>AlertControl</code> and a given time range from the database
     * 
     * 
     * @param scp    the <code>AlertControl</code> used to select the entries
     * @param months the number of recent months the <code>NRequests</code> were persisted (calculated as 30 days)
     * @param em    the entity manager for the database
     *            
     * @return the <code>NRequests</code> associated with the alert control  
     */
    public static List<NRequests> getEventAnalyses(AlertControl ac, int numberOfMonths) {
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        long oldTimestamp = System.currentTimeMillis() - numberOfMonths * millisPerMonth;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NRequests> q = cb.createQuery(NRequests.class);
        Root<NRequests> c = q.from(NRequests.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String>get("alertControl"), ac.getAlertControl()));
        predicates.add(cb.ge(c.<Long>get("timestamp"), oldTimestamp));
        q.select(c).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<NRequests> query = em.createQuery(q);
        List<NRequests> results = query.getResultList();
        em.close();
        return results;
    }
}
