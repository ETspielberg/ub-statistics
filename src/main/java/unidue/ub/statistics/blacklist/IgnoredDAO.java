package unidue.ub.statistics.blacklist;

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
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;


/**
 * Methods to save and access <code>Ignored</code> by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class IgnoredDAO {
    
    private static final Logger LOGGER = Logger.getLogger(IgnoredDAO.class);
	
    /**
     * checks, whether a specific <code>Ignored</code> object is in the database
     * 
     * 
     * @param identifier	the identifier of the object to be checked
     * @param em	the entity manager for the database
     *            
     * @return boolean true, if the database contains the desired document analysis  
     */
    public static boolean contains(String identifier, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ignored> q = cb.createQuery(Ignored.class);
        Root<Ignored> c = q.from(Ignored.class);
        q.select(c).where(cb.equal(c.<String>get("identifier"), identifier));
        TypedQuery<Ignored> query = em.createQuery(q);
        if (query.getResultList().size() == 0) {
            return false;
        } else if (query.getResultList().size() == 1){
        	Ignored ignored = query.getSingleResult();
    	    if (ignored.getExpire() > System.currentTimeMillis()){
        	    LOGGER.info("found in ignored index, will be ignored");
        	    return true;
        	}
        	else
        		return false;
        } else {
            LOGGER.info("found multiple entries for " + identifier + ". Please check!");
            return false;
        }
	}
    
    /**
     * returns a list of all <code>Ignored</code> objects in the database
     * 
     * 
     * @return the list of all <code>Ignored</code> objects in the database
     */
    public static List<Ignored> listIgnored() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("tools");
    EntityManager em = emf.createEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Ignored> q = cb.createQuery(Ignored.class);
    Root<Ignored> c = q.from(Ignored.class);
    q.select(c).where(cb.like(c.<String>get("who"), "%"));
    return em.createQuery(q).getResultList();
    }
	
    /**
     * removes an <code>Ignored</code> object to the database
     * 
     * 
     * @param identifier	the identifier of the <code>Ignored</code> object to be removed
     */
    public static void removeIgnored(String identifier) {
		EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
		EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Ignored> delete = cb.createCriteriaDelete(Ignored.class);
        Root<Ignored> c = delete.from(Ignored.class);
        delete.where(cb.equal(c.get("identifier"), identifier));
        Query query = em.createQuery(delete);
        query.executeUpdate();
        tx.commit();
        em.close();
	}
	
    /**
     * persists a single <code>Ignored</code> object to the database
     * 
     * 
     * @param ignored
     *            the <code>Ignored</code> object to be persisted
     */
    public static void persistIgnorance(Ignored ignored) {
		EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(ignored);
		tx.commit();
		em.close();
	}
}
