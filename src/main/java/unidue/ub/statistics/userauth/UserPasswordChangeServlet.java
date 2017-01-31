package unidue.ub.statistics.userauth;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.jdom2.Element;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Registers new users to the databse
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/settings/passwordChange")
public class UserPasswordChangeServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(UserPasswordChangeServlet.class);

	/**
	 * retrieves the user parameters from the post-request, registrates the
	 * user, and prepares the user's home directory
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGetPost(MCRServletJob job) throws Exception {
		Element output = new Element("passwordChange");
		HttpServletRequest req = job.getRequest();
		String newPassword = getParameter(req, "newPassword");
		String newPasswordCheck = getParameter(req, "newPasswordCheck");
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		Element message = new Element("message");
		if (currentUser.isAuthenticated()) {
			String email;
			if (currentUser.hasRole("userAdmin")) {
				email = getParameter(req, "email");
				if (email.isEmpty())
				    email = (String) req.getSession().getAttribute("email");
				output.setAttribute("userAdmin","");
			}
			else { 
				email = (String) req.getSession().getAttribute("email");
				String friendlyName = (String) req.getSession().getAttribute("friendlyName");
				output.setAttribute("loggedInAs", friendlyName);
			}
			if (!newPassword.isEmpty()) {
				if (newPassword.equals(newPasswordCheck) || currentUser.hasRole("userAdmin")) {
				    LOGGER.info("changing password for " + email);
					updatePassword(email, newPassword);
					message.addContent("login.message.passwordChanged");
					output.setAttribute("success","true");
				} else {
					message.addContent("login.message.passwordsNotTheSame");
				}
			} else {
				message.addContent("login.message.passwordNeeded");
			}
		} else
			message.addContent("login.message.notAuthenticated");
		output.addContent(message);
		sendOutput(job,output);
	}

	/**
	 * registrates the user in the database
	 * 
	 * 
	 * @param user
	 *            the user
	 * @param plainTextPassword
	 *            the password
	 */
	public void registrate(User user, String plainTextPassword) {
		generatePassword(user, plainTextPassword);
		UserRole role = new UserRole();
		role.setEmail(user.getEmail());
		role.setRoleName("guest");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(user);
		em.persist(role);
		tx.commit();
		em.close();
	}

	public void updatePassword(String email, String newPassword) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
        tx.begin();
        
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> q = cb.createQuery(User.class);
		Root<User> c = q.from(User.class);
		q.select(c).where(cb.equal(c.get("email"), email));
		TypedQuery<User> query = em.createQuery(q);
		User user = query.getSingleResult();
		generatePassword(user, newPassword);
		tx.commit();
		em.clear();
	}

	private void generatePassword(User user, String plainTextPassword) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		Object salt = rng.nextBytes();
		String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
		user.setPassword(hashedPasswordBase64);
		user.setSalt(salt.toString());
	}

	private String getParameter(HttpServletRequest req, String name) {
		String value = req.getParameter(name);
		return value == null ? "" : value.trim();
	}
}
