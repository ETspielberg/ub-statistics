package unidue.ub.statistics.userauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.jdom2.Element;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Registers new users to the databse
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/userRegistration")
public class UserRegistrationServlet extends FachRefServlet {

    private final static String userDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(UserRegistrationServlet.class);

    /**
     * retrieves the user parameters from the post-request, registrates the
     * user, and prepares the user's home directory
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws Exception {
        Element output = new Element("userRegistration");
        HttpServletRequest req = job.getRequest();
        String email = getParameter(req, "email");
        String plainTextPassword = getParameter(req, "password");

        if (UserDAO.getUser(email) != null) {
            output.addContent((new Element("message")).addContent("login.message.userExists"));
            sendOutput(job,output);
        } else if (plainTextPassword.equals("")) {
            output.addContent((new Element("message")).addContent("login.message.noPassword"));
            sendOutput(job,output);
        } else {
            User user = new User();
            user.setEmail(email);
            registrate(user, plainTextPassword);
            if (UserDAO.listUsers().size() == 1) {
                UserRole role = new UserRole();
                role.setEmail(email);
                role.setRoleName("userAdmin");
                UserRoleDAO.insert(role);
            }
            buildHomeDirectory(email);
            UsernamePasswordToken token = new UsernamePasswordToken(email, plainTextPassword);
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            try {
                currentUser.login(token);
                LOGGER.info("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");

                // save current email in the session, so we have access to our User
                // model
                currentUser.getSession().setAttribute("email", email);

            } catch (UnknownAccountException uae) {
                LOGGER.info("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                LOGGER.info("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
                LOGGER.info("The account for username " + token.getPrincipal() + " is locked.  " + "Please contact your administrator to unlock it.");
            }
            job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL() + "fachref/settings/User_Form.xed?email=" + email);
        }
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

    private void buildHomeDirectory(String email) throws IOException {
        File userDirectory = new File(userDir + "/" + email);
        if (!userDirectory.exists()) {
            userDirectory.mkdir();
            File stockControlDirectory = new File(userDir + "/" + email + "/stockControl");
            stockControlDirectory.mkdir();

            File uploadDirectory = new File(userDir + "/" + email + "/upload");
            uploadDirectory.mkdir();
            File archiveDirectory = new File(userDir + "/" + email + "/archive");
            archiveDirectory.mkdir();

            List<File> oldFiles = new ArrayList<>();
            List<File> newFiles = new ArrayList<>();
            oldFiles.add(new File(userDir, "csv.xml"));
            newFiles.add(new File(userDir + "/" + email + "/stockControl", "csv.xml"));
            oldFiles.add(new File(userDir, "default.xml"));
            newFiles.add(new File(userDir + "/" + email + "/stockControl", "default.xml"));
            oldFiles.add(new File(userDir, "user_data.xml"));
            newFiles.add(new File(userDir + "/" + email, "user_data.xml"));
            InputStream is = null;
            OutputStream os = null;
            for (int i = 0; i < oldFiles.size(); i++) {
                try {
                    is = new FileInputStream(oldFiles.get(i));
                    os = new FileOutputStream(newFiles.get(i));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                } finally {
                    is.close();
                    os.close();
                }
            }
        }
    }
}
