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
 * Methods to persist and access <code>CollectionUsagePerSubject</code> objects by JPA queries.
 * @author Spielberg
 *
 */
public class CollectionUsagePerSubjectDAO {

    private static final Logger LOGGER = Logger.getLogger(CollectionUsagePerSubjectDAO.class);

    /**
     * Persists a single <code>ShelfAnalysis</code> to the database
     * 
     * 
     * @param cups
     *            the collection usage per subject to be persisted
     */
    public static void persistCollectionUsagePerSubject(CollectionUsagePerSubject cups) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(cups);
        tx.commit();
        em.close();
    }

    /**
     * checks, whether a specific <code>CollectionUsagePerSubject</code> is in the database
     * 
     * 
     * @param anchor	the anchor of the collection
     * @param year	year to be looked at
     * @param em	the entity manager for the database
     *            
     * @return boolean true, if the database contains the desired collection usage per subject  
     */
    public boolean contains(String anchor, int year, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CollectionUsagePerSubject> q = cb.createQuery(CollectionUsagePerSubject.class);
        Root<CollectionUsagePerSubject> c = q.from(CollectionUsagePerSubject.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("collection"), anchor));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<CollectionUsagePerSubject> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return false;
        else
            return true;
    }

    /**
     * retrieves a specific <code>CollectionUsagePerSubject</code> from the database
     * 
     * 
     * @param collection	the collection
     * @param year	the year
     * @param subject the subject
     * @param em	the entity manager for the database
     *            
     * @return CollectionUsagePerSubject the collection usage per subject from the database  
     */
    public static CollectionUsagePerSubject getSingleCollectionUsagePerSubject(String collection, int year, EntityManager em, String subject) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CollectionUsagePerSubject> q = cb.createQuery(CollectionUsagePerSubject.class);
        Root<CollectionUsagePerSubject> c = q.from(CollectionUsagePerSubject.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("collection"), collection));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        predicates.add(cb.equal(c.<String> get("subject"), subject));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<CollectionUsagePerSubject> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return null;
        else if (query.getResultList().size() == 1)
            return query.getSingleResult();
        else
            return query.getResultList().get(0);
    }
    
    /**
     * retrieves a list of <code>CollectionUsagePerSubject</code> for one collection from the database
     * 
     * 
     * @param collection    the collection
     * @param year  the year
     * @param em    the entity manager for the database
     *            
     * @return CollectionUsagePerSubject the collection usage per subject from the database  
     */
    public static List<CollectionUsagePerSubject> getCollectionUsagePerSubjects(String collection, int year, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CollectionUsagePerSubject> q = cb.createQuery(CollectionUsagePerSubject.class);
        Root<CollectionUsagePerSubject> c = q.from(CollectionUsagePerSubject.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("collection"), collection));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<CollectionUsagePerSubject> query = em.createQuery(q);
        List<CollectionUsagePerSubject> cupss = query.getResultList();
        LOGGER.info("found " + cupss.size() + " entries for anchor " + collection + " in year " + year);
        return cupss;
    }

    /**
     * deletes a specific <code>CollectionUsagePerSubject</code> from the database
     * 
     * 
     * @param collection	the collection
     * @param year	the year
     * @param em	the entity manager for the database
     *            
     */
    public static void deleteSingleCollectionUsagePerSubject(String collection, int year, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<CollectionUsagePerSubject> delete = cb.createCriteriaDelete(CollectionUsagePerSubject.class);
        Root<CollectionUsagePerSubject> c = delete.from(CollectionUsagePerSubject.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("collection"), collection));
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        delete.where(predicates.toArray(new Predicate[] {}));
        Query query = em.createQuery(delete);
        query.executeUpdate();
        tx.commit();
    }

    /**
     * Persists a list of <code>CollectionUsagePerSubject</code> objects to the database
     * 
     * 
     * @param cupss
     *            the list of collection usage per subject to be persisted
     */
    public static void persistCollectionUsagePerSubjects(List<CollectionUsagePerSubject> cupss) {
        boolean persist = false;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (CollectionUsagePerSubject cups : cupss) {
            if (cups.getUsagePerSubject() > 0)
                persist = true;
            if (persist) {
                em.persist(cups);
            }
        }
        tx.commit();
        em.close();
    }

    /**
     * retrieves all <code>CollectionUsagePerSubject</code> for one collection from the database
     * 
     * 
     * @param collection	the collection
     *            
     * @return the collection usages per subject from the database  
     */
    public static List<CollectionUsagePerSubject> getCounters(String collection) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CollectionUsagePerSubject> q = cb.createQuery(CollectionUsagePerSubject.class);
        Root<CollectionUsagePerSubject> c = q.from(CollectionUsagePerSubject.class);
        q.select(c).where(cb.equal(c.<String> get("collection"), collection));
        TypedQuery<CollectionUsagePerSubject> query = em.createQuery(q);
        List<CollectionUsagePerSubject> cupss = query.getResultList();
        LOGGER.info("found " + cupss.size() + " counter statistics");
        em.close();
        return cupss;
    }

}
