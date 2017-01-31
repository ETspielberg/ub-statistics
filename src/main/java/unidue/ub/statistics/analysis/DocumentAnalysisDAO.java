/**
 * 
 */
package unidue.ub.statistics.analysis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Methods to persist and access <code>DocumentAnalysis</code> by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class DocumentAnalysisDAO {

	/**
     * Persists a single <code>DocumentAnalysis</code> to the database
     * 
     * 
     * @param analysis
     *            the document analysis to be persisted
     */
    public static void persistDocumentAnalysis(DocumentAnalysis analysis) {
        EntityManager em = Persistence.createEntityManagerFactory("documentAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(analysis);
        tx.commit();
        em.close();
    }
    
    /**
     * persists a list of <code>DocumentAnalysis</code> objects to the database
     * 
     * 
     * @param analyses	the list of <code>DocumentAnalysis</code> objects to be persisted
     */
    public static void persistDocumentAnalyses(List<DocumentAnalysis> analyses) {
        EntityManager em = Persistence.createEntityManagerFactory("documentAnalysis").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (DocumentAnalysis analysis : analyses) {
           em.persist(analysis);
        }
        tx.commit();
        em.close();
    }

    /**
     * checks, whether a specific <code>DocumentAnalysis</code> is in the database
     * 
     * 
     * @param year	the year of the document analysis
     * @param collection	the collection of the document analyzed
     * @param shelf	the shelf of the document analyzed
     * @param em	the entity manager for the database
     *            
     * @return boolean true, if the database contains the desired document analysis  
     */
    public static boolean contains(int year, String collection, String shelf, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DocumentAnalysis> q = cb.createQuery(DocumentAnalysis.class);
        Root<DocumentAnalysis> c = q.from(DocumentAnalysis.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(c.<Integer> get("year"), year));
        predicates.add(cb.equal(c.<String> get("collection"), collection));
        predicates.add(cb.equal(c.<String> get("shelf"), shelf));
        q.select(c).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<DocumentAnalysis> query = em.createQuery(q);
        if (query.getResultList().size() == 0)
            return false;
        else
            return true;
    }
    
    public static double getAverageLoan(Hashtable<String, Integer> yearRange,Hashtable<String,String> range, String collection, EntityManager em) {
    	double averageLoan = 0.0;
    	String sql = "SELECT SUM(daysLoanedStudents), SUM(daysLoanedExtern), SUM(daysLoanedIntern), SUM(daysLoanedHapp), SUM(daysLoanedElse), SUM(daysStockLBS), SUM(daysStockLendableNonLBS) FROM DocumentAnalysis WHERE year BETWEEN (:yearStart AND :yearEnd) AND shelfmark BETWEEN (:rangeStart AND :rangeEnd) AND collection = :collection";
    	Query query = em.createQuery(sql);
        query.setParameter("collection", collection);
        query.setParameter("yearStart", yearRange.get("von"));
        query.setParameter("yearEnd", yearRange.get("bis"));
        query.setParameter("rangeStart", range.get("von"));
        query.setParameter("rangeEnd", range.get("bis"));
        Integer[] results = (Integer[]) query.getSingleResult();
        Integer daysLoaned = results[0] + results[1] + results[2] + results[4];
        Integer daysStockLendable = results[5] + results[6] - results[3];
        if (daysStockLendable != 0 )
        	averageLoan = (double) daysLoaned / (double) daysStockLendable;
    	return averageLoan;
    }
}
