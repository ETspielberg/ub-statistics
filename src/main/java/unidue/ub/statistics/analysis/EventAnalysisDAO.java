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

import unidue.ub.statistics.stockcontrol.StockControlProperties;

/**
 * Methods to save and access <code>EventAnalysis</code> by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class EventAnalysisDAO {

	/**
     * Persists a single <code>EventAnalysis</code> to the database
     * 
     * 
     * @param analysis
     *            the event analysis to be persisted
     */
    public static void persistEventAnalysis(EventAnalysis analysis) {
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(analysis);
        tx.commit();
        em.close();
    }
    
    /**
     * persists a list of <code>EventAnalysis</code> objects to the database
     * 
     * 
     * @param analyses	the list of <code>EventAnalysis</code> objects to be persisted
     */
    public static void persistEventAnalyses(List<EventAnalysis> analyses) {
        EntityManager em = Persistence.createEntityManagerFactory("eventAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (EventAnalysis analysis : analyses) {
           em.persist(analysis);
        }
        tx.commit();
        em.close();
    }

    /**
     * retrieves a specific <code>EventAnalysis</code> from the database
     * 
     * 
     * @param collection	the collection of the document analyzed
     * @param shelfmark	 the  shelfmark of the document analyzed
     * @param em	the entity manager for the database
     *            
     * @return EventAnalysis the event analysis from the database  
     */
    public static EventAnalysis getSingleEventAnalysis(String collection, String shelfmark, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EventAnalysis> q = cb.createQuery(EventAnalysis.class);
        Root<EventAnalysis> c = q.from(EventAnalysis.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<String> get("shelfmark"), shelfmark));
        predicates.add(cb.equal(c.<String> get("collection"), collection));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<EventAnalysis> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return null;
        else if (query.getResultList().size() == 1)
            return query.getSingleResult();
        else
            return query.getResultList().get(0);
    }
    
    /**
     * retrieves a a list of <code>EventAnalysis</code> for a given author and <code>StockControlProperties</code> from the database
     * 
     * 
     * @param scp    the <code>StockControlProperties</code> used to calculate the entries
     * @param who  the author name of the <code>StockControlProperties</code>
     * @param em    the entity manager for the database
     *            
     * @return EventAnalysis the event analysis from the database  
     */
    public static List<EventAnalysis> getEventAnalyses(StockControlProperties scp, String who, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EventAnalysis> q = cb.createQuery(EventAnalysis.class);
        Root<EventAnalysis> c = q.from(EventAnalysis.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.get("stockControl"), scp.getStockControl()));
        predicates.add(cb.gt(c.<Integer> get("proposedPurchase"),0));
        predicates.add(cb.equal(c.get("author"), who));
        q.select(c).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<EventAnalysis> query = em.createQuery(q);
        return query.getResultList();
    }
}
