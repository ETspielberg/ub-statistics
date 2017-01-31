package unidue.ub.statistics.userauth;

import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Holding salted authentication information.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class SaltedAuthInfo implements SaltedAuthenticationInfo {
	private static final long serialVersionUID = -5467967895187234984L;

	private final String email;
	private final String password;
	private final String salt;

	/**
	 * store salted authentication information.
	 * 
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param salt
	 *            the salt
	 * 
	 */
	public SaltedAuthInfo(String email, String password, String salt) {
		this.email = email;
		this.password = password;
		this.salt = salt;
	}

	/**
	 * build the principal collection.
	 * 
	 * @return token the principal collection
	 * 
	 */
	@Override
	public PrincipalCollection getPrincipals() {
		PrincipalCollection coll = new SimplePrincipalCollection(email, email);
		return coll;
	}

	/**
	 * build credentials.
	 * 
	 * @return password
	 *            the credentials
	 * 
	 */
	@Override
	public Object getCredentials() {
		return password;
	}

	/**
	 * get credentials salt.
	 * 
	 * @return ByteSource
	 *            the credentials salt
	 * 
	 */
	@Override
	public ByteSource getCredentialsSalt() {
		return new SimpleByteSource(Base64.decode(salt));
	}

}
