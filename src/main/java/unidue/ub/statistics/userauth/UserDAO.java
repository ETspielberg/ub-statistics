package unidue.ub.statistics.userauth;

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
 * DAO for retrieving users, roles and permissions.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class UserDAO {

	private static final Logger LOGGER = Logger.getLogger(UserDAO.class);

	/**
	 * Build DAO-entity
	 */
	public UserDAO() {
	}

	/**
	 * retrieves the <code>User</code> by its email.
	 * 
	 * @param email
	 *            the email
	 * @return user the user
	 * 
	 */
	public static User getUser(String email) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<User> q = cb.createQuery(User.class);
		Root<User> c = q.from(User.class);
		q.select(c).where(cb.equal(c.get("email"), email));
		TypedQuery<User> query = em.createQuery(q);
		List<User> users = query.getResultList();
		LOGGER.info("found " + users.size() + " users");
		em.close();
		if (users.size() == 1)
			return users.get(0);
		else
			return null;
	}
	
	/**
	 * retrieves the list of user emails registered.
	 * 
	 * @return users the list of user emails
	 * 
	 */
	public static List<String> listUsers() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT email FROM User");
		@SuppressWarnings("unchecked")
		List<String> users = query.getResultList();
		em.close();
		return users;
	}


	/**
	 * deletes the <code>User</code> with the given email as well as all the corresponding roles.
	 * 
	 * @param email
	 *            the email of the user
	 * 
	 */
	public static void deleteUser(String email) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaDelete<User> deleteUser = cb.createCriteriaDelete(User.class);
		Root<User> cUser = deleteUser.from(User.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(cUser.get("email"), email));
		deleteUser.where(predicates.toArray(new Predicate[] {}));
		Query query = em.createQuery(deleteUser);
		query.executeUpdate();
		
		CriteriaDelete<UserRole> deleteRoles = cb.createCriteriaDelete(UserRole.class);
		Root<UserRole> cRoles = deleteRoles.from(UserRole.class);
		List<Predicate> predicatesRoles = new ArrayList<Predicate>();
		predicatesRoles.add(cb.equal(cRoles.get("email"), email));
		deleteRoles.where(predicates.toArray(new Predicate[] {}));
		Query queryRoles = em.createQuery(deleteRoles);
		queryRoles.executeUpdate();
		
		tx.commit();
		em.close();
	}
}
