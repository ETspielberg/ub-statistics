package unidue.ub.statistics.media.journal;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
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
 * Methods to retrieve, delete and persist <code>JournalCollection</code> objects.
 * 
 * @author Eike Spielberg
 *
 */
public class JournalCollectionDAO {
    
    private static final Logger LOGGER = Logger.getLogger(JournalCollectionDAO.class);
    
    /**
	 * returns a list of journal collections with a given anchor
	 * 
	 * @param anchor the anchor of the journal collections
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections(String anchor) {
        List<JournalCollection> collections = new ArrayList<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.equal(c.get("anchor"), anchor));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        collections = query.getResultList();
        em.close();
        return collections;
    }
    
	/**
	 * returns a list of journal collections with a given anchor
	 * 
	 * @param anchor the anchor of the journal collections
	 * @param em the entity manager for the database
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections(EntityManager em, String anchor) {
        List<JournalCollection> collections = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.equal(c.get("anchor"), anchor));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        collections = query.getResultList();
        return collections;
    }
    
	/**
	 * returns a specific journal collection for one year with a given anchor
	 * 
	 * @param anchor the anchor of the journal collections
	 * @param year the year
	 * @param em the entity manager of the database
	 * @return the journal collection
	 * 
	 */
	public static JournalCollection getCollection(EntityManager em, String anchor, int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(c.<String>get("anchor"), anchor));
        predicates.add(cb.equal(c.<Integer>get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        try {
            JournalCollection collection = query.getSingleResult();
            return collection;
        } catch (Exception e) {
            return null;
        }
    }
    
	/**
	 * returns a specific journal collection for one year with a given anchor
	 * 
	 * @param anchor the anchor of the journal collections
	 * @param year the year
	 * @return the journal collection
	 * 
	 */
	public static JournalCollection getCollection(String anchor, int year) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(c.<String>get("anchor"), anchor));
        predicates.add(cb.equal(c.<Integer>get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        try {
            JournalCollection collection = query.getSingleResult();
            em.close();
            return collection;
        } catch (Exception e) {
            em.close();
            return null;
        }
    }
    
	/**
	 * lists the anchors of all journal collections stored in the database.
	 * 
	 * @return the list of anchors
	 * 
	 */
	public static List<String> listCollections() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT anchor FROM JournalCollection");
        @SuppressWarnings("unchecked")
        List<String> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        em.close();
        return packs;
    }
    
	/**
	 * lists the anchors of all journal collections stored in the database.
	 * @param em the entity manager of the database
	 * @return the list of anchors
	 * 
	 */
	public static List<String> listCollections(EntityManager em) {
        Query query = em.createQuery("SELECT anchor FROM JournalCollection");
        @SuppressWarnings("unchecked")
        List<String> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        return packs;
    }
    
	/**
	 * returns a list of journal collections for one year
	 * 
	 * @param year the year
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections(int year) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.equal(c.<Integer>get("year"), year));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        List<JournalCollection> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        em.close();
        return packs;
    }
    
	/**
	 * returns a list of journal collections for one year
	 * 
	 * @param year the year
	 * @param em the entity manager of the database
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections(EntityManager em, int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.equal(c.<Integer>get("year"), year));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        List<JournalCollection> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        return packs;
    }
    
	/**
	 * returns all journal collections stored in the database.
	 * 
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.like(c.<String>get("anchor"), "%"));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        List<JournalCollection> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        em.close();
        return packs;
    }
    
	/**
	 * returns all journal collections stored in the database.
	 * @param em the entity manager of the database
	 * @return the list of journal collections
	 * 
	 */
	public static List<JournalCollection> getCollections(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalCollection> q = cb.createQuery(JournalCollection.class);
        Root<JournalCollection> c = q.from(JournalCollection.class);
        q.select(c).where(cb.like(c.<String>get("anchor"), "%"));
        TypedQuery<JournalCollection> query = em.createQuery(q);
        List<JournalCollection> packs = query.getResultList();
        LOGGER.info("found " + packs.size() + " journal collections");
        return packs;
    }
    
	/**
	 * deletes all journal collections with a given anchor from the database.
	 * @param anchor the anchor of the journal collection
	 * 
	 */
	 public static void deleteCollection(String anchor) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<JournalCollection> deletePacks = cb.createCriteriaDelete(JournalCollection.class);
        Root<JournalCollection> cUser = deletePacks.from(JournalCollection.class);
        deletePacks.where(cb.equal(cUser.get("anchor"), anchor));
        em.createQuery(deletePacks).executeUpdate();
        tx.commit();
        em.close();
    }
    
	 /**
	  * persists the given journal collection to the database.
	  * @param collection the journal collection to be persisted
	  * 
	  */
	 public static void persistCollection(JournalCollection collection) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(collection);
        tx.commit();
        em.close();
    }
    
	 /**
	  * persists a list of journal collections to the database.
	  * @param collections the list of journal collections to be persisted
	  * 
	  */
	 public static void persistCollections(List<JournalCollection> collections ) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (JournalCollection collection : collections)
            em.persist(collection);
        tx.commit();
        em.close();
    }
    
	 /**
	  * persists a list of journal collections to the database.
	  * @param em the entity manager of the database
	  * @param collections the list of journal collections to be persisted
	  * 
	  */
	 public static void persistCollections(EntityManager em, List<JournalCollection> collections ) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (JournalCollection collection : collections)
            em.persist(collection);
        tx.commit();
    }
    
	 /**
	  * persists a set of journal collections to the database, stored in a <code>Hashtable</code> with the anchors as keys
	  * @param collections the list of journal collections to be persisted
	  * 
	  */
	 public static void persistCollections(Hashtable<String,JournalCollection> collections ) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Enumeration<String> anchors  = collections.keys();
        while (anchors.hasMoreElements())
            em.persist(collections.get(anchors.nextElement()));
        tx.commit();
        em.close();
    }
}
