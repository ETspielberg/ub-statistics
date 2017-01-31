package unidue.ub.statistics.userauth;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Plain old java object holding the user connected roles with the corresponding
 * email. The fields can be persisted.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
@Table
public class UserRole implements Serializable {
	private static final long serialVersionUID = 8432414340180447723L;

	@Id
	@GeneratedValue
	private Integer id;

	private String roleName;
	private String email;

	/**
	 * Build user-role-entity
	 */
	public UserRole() {
	}

	/**
	 * returns the role name
	 * 
	 * @return roleName the role name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * sets the role name
	 * 
	 * @param roleName
	 *            the role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

}
