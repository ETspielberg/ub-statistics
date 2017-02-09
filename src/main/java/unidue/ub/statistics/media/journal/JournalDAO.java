package unidue.ub.statistics.media.journal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Methods to retrieve, delete and persist <code>Journal</code> objects.
 * 
 * @author Eike Spielberg
 *
 */
public class JournalDAO {
    
    /**
	 * returns a journal for a given issn
	 * 
	 * @param issn the issn
	 * @return the journal
	 * 
	 */
	public static Journal getJournalByIssn(String issn) {
        try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Journal> q = cb.createQuery(Journal.class);
        Root<Journal> c = q.from(Journal.class);
        q.select(c).where(cb.equal(c.get("issn"), issn));
        TypedQuery<Journal> query = em.createQuery(q);
        Journal journal = query.getSingleResult();
        em.close();
        return journal;
        }catch (Exception e) {
            return null;
        }
    }
    
	/**
	 * returns a journal for a given issn
	 * 
	 * @param issn the issn
	 * @param em the entity manager
	 * @return the journal
	 * 
	 */
	public static Journal getJournalByIssn(EntityManager em, String issn) {
        try {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Journal> q = cb.createQuery(Journal.class);
        Root<Journal> c = q.from(Journal.class);
        q.select(c).where(cb.equal(c.get("issn"), issn));
        TypedQuery<Journal> query = em.createQuery(q);
        Journal journal = query.getSingleResult();
        return journal;
        }catch (Exception e) {
            return null;
        }
    }
    
	/**
	 * returns a journal for a given ZDB ID
	 * 
	 * @param zdbID the ZDB ID
	 * @return the journal
	 * 
	 */
	public static Journal getJournalByZDBID(String zdbID) {
        try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Journal> q = cb.createQuery(Journal.class);
        Root<Journal> c = q.from(Journal.class);
        q.select(c).where(cb.equal(c.get("zdbID"), zdbID));
        TypedQuery<Journal> query = em.createQuery(q);
        Journal journal = query.getSingleResult();
        em.close();
        return journal;
        }catch (Exception e) {
            return null;
        }
    }
    
	/**
	 * returns a journal for a given ZDB ID
	 * 
	 * @param zdbID the ZDB ID
	 * @param em entity manager
	 * @return the journal
	 * 
	 */
	public static Journal getJournalByZDBID(EntityManager em, String zdbID) {
        try {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Journal> q = cb.createQuery(Journal.class);
        Root<Journal> c = q.from(Journal.class);
        q.select(c).where(cb.equal(c.get("zdbID"), zdbID));
        TypedQuery<Journal> query = em.createQuery(q);
        Journal journal = query.getSingleResult();
        return journal;
        }catch (Exception e) {
            return null;
        }
    }
    
	/**
	 * persists a journal to the database
	 * 
	 * @param journal the journal to be persisted
	 * 
	 */
	public static void persistJournal(Journal journal) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(journal);
        tx.commit();
        em.close();
    }
    
	/**
	 * persists a list of journals to the database
	 * 
	 * @param journals the list of journals to be persistedl
	 * 
	 */
	public static void persistJournals(List<Journal> journals) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (Journal journal : journals)
        	em.persist(journal);
        tx.commit();
        em.close();
    }
	
	/**
     * deletes all journals from the database
     * 
     * 
     */
    public static void deleteJournals() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Journal> deleteJournals = cb.createCriteriaDelete(Journal.class);
        Root<Journal> journals = deleteJournals.from(Journal.class);
        deleteJournals.where(cb.like(journals.<String>get("zdbID"), "%"));
        em.createQuery(deleteJournals).executeUpdate();
        tx.commit();
        em.close();
    }
}
