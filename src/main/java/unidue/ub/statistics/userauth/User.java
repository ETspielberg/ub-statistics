package unidue.ub.statistics.userauth;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Plain old java object holding the user with their password and salt. The
 * fields can be persisted.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
@Table
public class User implements Serializable {

	private static final long serialVersionUID = -4656759219348212715L;

	@Id
	@GeneratedValue
	private Integer id;

	private String email;
	private String password;
	private String salt;

	/**
	 * Build user-entity
	 */

	public User() {
	}

	/**
	 * returns the email
	 * 
	 * @return email the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * sets the email
	 * 
	 * @param email
	 *            the email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * returns the password
	 * 
	 * @return password the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * sets the password
	 * 
	 * @param password
	 *            the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * returns the salt
	 * 
	 * @return salt the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * sets the salt
	 * 
	 * @param salt
	 *            the salt
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

}