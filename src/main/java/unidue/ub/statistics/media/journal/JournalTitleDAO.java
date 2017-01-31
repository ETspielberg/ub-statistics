package unidue.ub.statistics.media.journal;

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
 * Methods to retrieve, delete and persist <code>JournalTitle</code> objects.
 * 
 * @author Eike Spielberg
 *
 */
public class JournalTitleDAO {
    
    private static final Logger LOGGER = Logger.getLogger(JournalTitleDAO.class);
    
    /**
	 * returns a list of journal titles for an issn
	 * 
	 * @param issn the issn of the journal titles
	 * @return the journal title
	 * 
	 */
	public static List<JournalTitle> getJournalTitlesByIssn(String issn) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        q.select(c).where(cb.equal(c.get("issn"), issn));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        em.close();
        if (journals.size() == 0)
        	return null;
        else 
        return journals;
    }
    
	/**
	 * returns a list of journal titles for an issn
	 * 
	 * @param issn the issn of the journal titles
	 * @param em the entity manager of the database
	 * @return the journal title
	 * 
	 */
	public static List<JournalTitle> getJournalTitlesByIssn(EntityManager em, String issn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        q.select(c).where(cb.equal(c.get("issn"), issn));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        if (journals.size() == 0)
            return null;
        else 
        return journals;
    }
    
	/**
	 * returns a list of journal titles for a ZDB ID
	 * 
	 * @param zdbID the ZDB ID of the journal titles
	 * @param em the entity manager of the database
	 * @return the journal title
	 * 
	 */
	public static List<JournalTitle> getJournalTitlesByZDBID(EntityManager em, String zdbID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        q.select(c).where(cb.equal(c.get("tdbID"), zdbID));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        if (journals.size() == 0)
            return null;
        else 
        return journals;
    }
    
    /**
	 * returns a specific journal title for a ZDB ID and a year
	 * 
	 * @param zdbID the ZDB ID of the journal
	 * @param year the year
	 * @param em the entity manager of the database
	 * @return the journal title
	 * 
	 */
	public static List<JournalTitle> getJournalTitlesByZDBID(EntityManager em, String zdbID,int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(c.<String>get("zdbID"), zdbID));
        predicates.add(cb.equal(c.<Integer>get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        if (journals.size() == 0)
            return null;
        else 
        return journals;
    }
    
    /**
	 * returns a specific journal title for an issn and a year
	 * 
	 * @param issn the issn of the journal titles
	 * @param year the year
	 * @param em the entity manager of the database
	 * @return the journal title
	 * 
	 */
	public static JournalTitle getJournalTitle(EntityManager em, String issn, int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(c.<String>get("issn"), issn));
        predicates.add(cb.equal(c.<Integer>get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        if (journals.size() == 0)
            return null;
        else 
        return journals.get(0);
    }
    
	/**
	 * returns a specific journal title for an issn and a year
	 * 
	 * @param issn the issn of the journal titles
	 * @param year the year
	 * @return the journal title
	 * 
	 */
	public static JournalTitle getJournalTitle(String issn, int year) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(c.<String>get("issn"), issn));
        predicates.add(cb.equal(c.<Integer>get("year"), year));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        em.close();
        if (journals.size() == 0)
            return null;
        else 
        return journals.get(0);
    }
    
	/**
	 * returns a list of the ISSNs of all journal titles stored in the database
	 * @return the list of ISSNs
	 * 
	 */
	public static List<String> listAllIssns() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT distinct issn FROM JournalTitle");
        @SuppressWarnings("unchecked")
        List<String> issns = query.getResultList();
        LOGGER.info("found " + issns.size() + " ISSNs");
        em.close();
        return issns;
    }
    
	/**
	 * returns a list of the ISSNs of all journal titles stored in the database
	 * 
	 * @param em the entity manager of the database
	 * @return the list of ISSNs
	 * 
	 */
	public static List<String> listAllIssns(EntityManager em) {
        Query query = em.createQuery("SELECT distinct issn FROM JournalTitle");
        @SuppressWarnings("unchecked")
        List<String> issns = query.getResultList();
        LOGGER.info("found " + issns.size() + " ISSNs");
        return issns;
    }
    
	/**
	 * returns a list of all journal titles stored in the database
	 * 
	 * @return the list of journal titles
	 * 
	 */
	public static List<JournalTitle> getJournals() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JournalTitle> q = cb.createQuery(JournalTitle.class);
        Root<JournalTitle> c = q.from(JournalTitle.class);
        q.select(c).where(cb.like(c.<String>get("issn"), "%"));
        TypedQuery<JournalTitle> query = em.createQuery(q);
        List<JournalTitle> journals = query.getResultList();
        LOGGER.info("found " + journals.size() + " journals");
        em.close();
        return journals;
    }
    
	/**
	 * deletes all journal titles with a given ISSN stored in the database
	 * 
	 * @param issn the ISSN of the journal titles
	 * @param em the entity manager of the database
	 * 
	 */
	public static void deletePackage(EntityManager em, String issn) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<JournalTitle> deletePacks = cb.createCriteriaDelete(JournalTitle.class);
        Root<JournalTitle> journals = deletePacks.from(JournalTitle.class);
        deletePacks.where(cb.equal(journals.<String>get("issn"), issn));
        em.createQuery(deletePacks).executeUpdate();
        tx.commit();
    }
    
	/**
	 * persists a journal title to the database
	 * 
	 * @param journalTitle the journal title to be persisted
	 * 
	 */
	public static void persistJournal(JournalTitle journalTitle) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(journalTitle);
        tx.commit();
        em.close();
    }
    
	/**
	 * persists a journal title to the database
	 * 
	 * @param journalTitle the journal title to be persisted
	 * @param em the entity manager of the database
	 * 
	 */
	public static void persistJournal(EntityManager em, JournalTitle journalTitle) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(journalTitle);
        tx.commit();
    }
    
	/**
	 * persists a list of journal titles to the database
	 * 
	 * @param journalTitles the journal titles to be persisted
	 * 
	 */
	public static void persistJournals(List<JournalTitle> journalTitles) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (JournalTitle journalTitle : journalTitles)
        	em.persist(journalTitle);
        tx.commit();
        em.close();
    }
    
	/**
	 * persists a list of journal titles to the database
	 * 
	 * @param journalTitles the journal titles to be persisted
	 * @param em the entity manager of the database
	 * 
	 */
	public static void persistJournals(EntityManager em, List<JournalTitle> journalTitles) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (JournalTitle journalTitle : journalTitles)
            em.persist(journalTitle);
        tx.commit();
    }
}
