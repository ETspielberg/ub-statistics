package unidue.ub.statistics.media.journal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

/**
 * Methods to retrieve, delete and persist <code> AnchorOrder</code> objects.
 * 
 * @author Eike Spielberg
 *
 */
public class AnchorOrderDAO {

	private static final Logger LOGGER = Logger.getLogger(JournalCollectionDAO.class);

	/**
	 * retrieves a <code>AnchorOrder</code> from the database by the anchor of
	 * the collection.
	 * 
	 * @param anchor
	 *            the name of the collection
	 * @param em
	 *            the entity manager
	 * @return collection a <code>Collection</code> object
	 * 
	 */
	public static AnchorOrder getAnchorOrder(String anchor, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AnchorOrder> q = cb.createQuery(AnchorOrder.class);
		Root<AnchorOrder> c = q.from(AnchorOrder.class);
		q.select(c).where(cb.equal(c.get("anchor"), anchor));
		TypedQuery<AnchorOrder> query = em.createQuery(q);
		try {
		AnchorOrder collection = query.getSingleResult();
		return collection;
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * retrieves all <code>AnchorOrder</code> objects from the database.
	 * 
	 * @return a list of <code>Collection</code> objects
	 * 
	 */
	public static List<AnchorOrder> getAnchorOrders() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AnchorOrder> q = cb.createQuery(AnchorOrder.class);
		Root<AnchorOrder> c = q.from(AnchorOrder.class);
		q.select(c).where(cb.like(c.<String>get("anchor"), "%"));
		TypedQuery<AnchorOrder> query = em.createQuery(q);
		List<AnchorOrder> anchorOrder = query.getResultList();
		LOGGER.info("found " + anchorOrder.size() + " anchor order relations");
		em.close();
		return anchorOrder;
	}

	/**
	 * deletes a <code>AnchorOrder</code> from the database by the anchor of
	 * the collection.
	 * 
	 * @param anchor
	 *            the name of the collection
	 */
	public static void deleteAnchorOrder(String anchor) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<AnchorOrder> deletePacks = cb.createCriteriaDelete(AnchorOrder.class);
		Root<AnchorOrder> c = deletePacks.from(AnchorOrder.class);
		deletePacks.where(cb.equal(c.get("anchor"), anchor));
		em.createQuery(deletePacks).executeUpdate();
		tx.commit();
		em.close();
	}

	/**
	 * persists a <code>AnchorOrder</code> object to the database
	 * 
	 * @param anchorOrder
	 *            the object to be persisted
	 * @param em
	 *            the entity manager
	 * 
	 */
	public static void persistAnchorOrder(AnchorOrder anchorOrder, EntityManager em) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(anchorOrder);
		tx.commit();
	}

	/**
	 * persists a list of <code>AnchorOrder</code> objects to the database
	 * 
	 * @param anchorOrders
	 *            the objects to be persisted
	 * 
	 */
	public static void persistAnchorOrders(List<AnchorOrder> anchorOrders) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (AnchorOrder anchorOrder : anchorOrders) {
			em.persist(anchorOrder);
		}
		tx.commit();
		em.close();
	}

}
