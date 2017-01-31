package unidue.ub.statistics.eUsage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

/**
 * Methods to persist and access <code>Counter</code> objects by JPA queries.
 * @author Spielberg
 *
 */
public class CounterDAO {

    private static final Logger LOGGER = Logger.getLogger(CounterDAO.class);

    /**
     * Persists a counter objects to the database
     * 
     * 
     * @param counter
     *            the counter to be persisted
     */
    public static void persistCounter(Counter counter) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(counter);
        tx.commit();
        em.close();
    }

    /**
     * checks, whether a specific <code>Counter</code> object is in the database
     * 
     * 
     * @param issn    the ISSN of the journal to be checked
     * @param year    the year of the COUNTER statistics
     * @param month    the month of the COUNTER statistics
     * @param em    the entity manager for the database
     *            
     * @return boolean true, if the database contains the desired COUNTER statistics 
     */
    public boolean contains(String issn, int year, int month, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Counter> q = cb.createQuery(Counter.class);
        Root<Counter> c = q.from(Counter.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("onlineISSN"), issn));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        predicates.add(cb.equal(c.<String> get("month"), month));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Counter> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return false;
        else
            return true;
    }

    /**
     * retrieves a single <code>Counter</code> object from the database
     * 
     * @param issn    the ISSN of the journal to be checked
     * @param year    the year of the COUNTER statistics
     * @param month    the month of the COUNTER statistics
     * @param em    the entity manager for the database
     * 
     * @return Counter the desired COUNTER statistics. Returns null if the statistics is not found or more than one exist.
     * 
     */
    public static Counter getSingleCounter(String issn, int year, int month, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Counter> q = cb.createQuery(Counter.class);
        Root<Counter> c = q.from(Counter.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("onlineISSN"), issn));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        predicates.add(cb.equal(c.<String> get("month"), month));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Counter> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return null;
        else if (query.getResultList().size() == 1)
            return query.getSingleResult();
        else
            return query.getResultList().get(0);
    }

    /**
     * retrieves a list of  <code>Counter</code> objects for one journal and one year from the database
     * 
     * @param issn    the ISSN of the journal to be checked
     * @param year    the year of the COUNTER statistics
     * @param em    the entity manager for the database
     * 
     * @return the desired COUNTER statistics. Returns null if the statistics is not found.
     * 
     */
     public static List<Counter> getCountersForYear(String issn, int year, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Counter> q = cb.createQuery(Counter.class);
        Root<Counter> c = q.from(Counter.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("onlineISSN"), issn));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Counter> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return null;
        else
            return query.getResultList();
    }

     /**
      * returns the sum of all types of request within a given year
      * 
      * @param issn    the ISSN of the journal to be checked
      * @param year    the year of the COUNTER statistics
      * @param em    the entity manager for the database
      * 
      * @return long the total number of successful requests
      * 
      */
     public static long getYearlyTotalRequests(String issn, int year, EntityManager em) {
        Query query = em.createQuery("SELECT SUM(totalRequests) FROM Counter where (onlineISSN = :issn or printISSN = :issn) and year = :year");
        query.setParameter("issn", issn);
        query.setParameter("year", year);
        try {
            long totalRequests = (Long) query.getSingleResult();
            return totalRequests;
        } catch (Exception e) {
            LOGGER.info("found no total requests");
            return 0L;
        }
    }

     /**
      * deletes a single <code>Counter</code> object from the database
      * 
      * @param issn    the ISSN of the journal to be checked
      * @param year    the year of the COUNTER statistics
      * @param month    the month of the COUNTER statistics
      * @param em    the entity manager for the database
      *  
      */
     public static void deleteSingleCounter(String issn, int year, int month, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Counter> delete = cb.createCriteriaDelete(Counter.class);
        Root<Counter> c = delete.from(Counter.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("onlineISSN"), issn));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        predicates.add(cb.equal(c.<String> get("month"), month));
        delete.where(predicates.toArray(new Predicate[] {}));
        Query query = em.createQuery(delete);
        query.executeUpdate();
        tx.commit();
    }

     /**
      * persists a list of <code>Counter</code> objects to the database
      * 
      * @param counters the list of COUNTER statistics to be persisted
      * 
      * 
      */
     public static void persistCounter(List<Counter> counters) {
        boolean persist = false;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (Counter counter : counters) {
            if (counter.getTotalRequests() > 0)
                persist = true;
            if (persist) {
                em.persist(counter);
            }
        }
        tx.commit();
        em.close();
    }

     /**
      * retrieves a list of <code>Counter</code> objects for one journal from the database
      * 
      * @param onlineISSN    the ISSN of the journal to be checked
      * 
      * @return the desired COUNTER statistics. Returns null if the statistics is not found.
      * 
      */
      public static List<Counter> getCounters(String onlineISSN) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Counter> q = cb.createQuery(Counter.class);
        Root<Counter> c = q.from(Counter.class);
        q.select(c).where(cb.equal(c.<String> get("onlineISSN"), onlineISSN));
        TypedQuery<Counter> query = em.createQuery(q);
        List<Counter> counters = query.getResultList();
        LOGGER.info("found " + counters.size() + " counter statistics");
        em.close();
        return counters;
    }

}
