package unidue.ub.statistics.userauth;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

/**
 * Controls all the user logging processes, sends errors as xml to be displayed
 * via XSLT.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/userLogging")
public class UserLoggingServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private final static String userDir;

	private static final Logger LOGGER = Logger.getLogger(UserLoggingServlet.class);

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	public UserLoggingServlet() {
		Factory<org.apache.shiro.mgt.SecurityManager> shiroFactory = new IniSecurityManagerFactory();
		org.apache.shiro.mgt.SecurityManager securityManager = shiroFactory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}

	/**
	 * retrieves the logout parameter from the get-request and performs the
	 * logout
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doGet(MCRServletJob job) throws IOException, TransformerException, SAXException {
		Element output = new Element("userLogging");
		if (job.getRequest().getParameter("logout") != null) {
			org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
			currentUser.logout();
			job.getResponse().sendRedirect("/index.html");
		}
		getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(output));
	}

	/**
	 * retrieves the login parameters from the post-request and performs the
	 * login
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doPost(MCRServletJob job) throws Exception {
		Element output = new Element("userLogging");
		HttpServletRequest req = job.getRequest();
		String email = getParameter(req, "email");
		String password = getParameter(req, "password");
		boolean rememberMe = "true".equals(getParameter(req, "rememberMe"));
		boolean b = false;
		if (email == null)
			output.addContent((new Element("message")).addContent("login.message.noUserGiven"));
		else if (password == null)
			output.addContent((new Element("message")).addContent("login.message.noPasswordGiven"));
		else {
			b = tryLogin(email, password, rememberMe);
			if (b) {
				SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(req);
				if (savedRequest != null)
					job.getResponse().sendRedirect(savedRequest.getRequestUrl());
				else
					job.getResponse().sendRedirect("fachref/start");
			} else
				output.addContent((new Element("message")).addContent("login.message.loginFailed"));
		}
		getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(output));
	}

	/**
	 * performs the login and signals, whether login was succesful
	 * 
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param rememberMe
	 *            the remember-me-token
	 * 
	 * @return success returns true if login was successful
	 * 
	 */
	public boolean tryLogin(String email, String password, Boolean rememberMe) {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(email, password);
			token.setRememberMe(rememberMe);

			try {
			    String friendlyName;
				currentUser.login(token);

				// save username in the session
				File userFile = new File(userDir + "/" + email, "user_data.xml");
				if (userFile.exists()) {
					SAXBuilder builder = new SAXBuilder();
					org.jdom2.Document document;
					document = (org.jdom2.Document) builder.build(userFile);

					Element rootNode = document.getRootElement();
					friendlyName = rootNode.getChild("details").getChild("fullname").getValue();
					currentUser.getSession().setAttribute("friendlyName", friendlyName);
					currentUser.getSession().setAttribute("email", email);
					LOGGER.info("User " + friendlyName + " logged in successfully.");
				} else {
					LOGGER.info("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");
					friendlyName = email;
				}
				currentUser.getSession().setAttribute("username", email);

				return true;
			} catch (UnknownAccountException uae) {
				LOGGER.info("There is no user with username of " + token.getPrincipal());
			} catch (IncorrectCredentialsException ice) {
				LOGGER.info("Password for account " + token.getPrincipal() + " was incorrect!");
			} catch (LockedAccountException lae) {
				LOGGER.info("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return true;
		}

		return false;
	}

	/**
	 * performs the logout
	 * 
	 * 
	 */
	public void logout() {
		org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
	}

	private String getParameter(HttpServletRequest req, String name) {
		String value = req.getParameter(name);
		return value == null ? "" : value.trim();
	}

}
