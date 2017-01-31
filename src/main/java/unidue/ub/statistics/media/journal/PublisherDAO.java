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
 * Methods to retrieve, delete and persist <code>Publisher</code> objects.
 * 
 * @author Eike Spielberg
 *
 */
public class PublisherDAO {
    
    private static final Logger LOGGER = Logger.getLogger(PublisherDAO.class);
    
    public static Publisher getPublisher(String name) {
        Publisher publisher = new Publisher();
        try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Publisher> q = cb.createQuery(Publisher.class);
        Root<Publisher> c = q.from(Publisher.class);
        q.select(c).where(cb.equal(c.get("name"), name));
        TypedQuery<Publisher> query = em.createQuery(q);
        publisher = query.getSingleResult();
        em.close();
        }catch (Exception e) {
            publisher = null;
        }
        return publisher;
    }
    
    public static List<String> listPublishers() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT name FROM Publisher");
        @SuppressWarnings("unchecked")
        List<String> publishers = query.getResultList();
        LOGGER.info("found " + publishers.size() + " publishers");
        em.close();
        return publishers;
    }
    
    public static List<Publisher> getPublishers() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Publisher> q = cb.createQuery(Publisher.class);
        Root<Publisher> c = q.from(Publisher.class);
        q.select(c).where(cb.like(c.<String>get("name"), "%"));
        TypedQuery<Publisher> query = em.createQuery(q);
        List<Publisher> publishers = query.getResultList();
        LOGGER.info("found " + publishers.size() + " publishers");
        em.close();
        return publishers;
    }
    
    public static void deletePublisher(String name) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaDelete<Publisher> deletePublisher = cb.createCriteriaDelete(Publisher.class);
        Root<Publisher> cUser = deletePublisher.from(Publisher.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(cUser.get("name"), name));
        deletePublisher.where(predicates.toArray(new Predicate[] {}));
        Query query = em.createQuery(deletePublisher);
        query.executeUpdate();
        
        tx.commit();
        em.close();
    }
    
    public static void persistPublisher(Publisher publisher) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(publisher);
        tx.commit();
        em.close();
    }
}
