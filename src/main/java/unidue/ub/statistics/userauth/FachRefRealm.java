package unidue.ub.statistics.userauth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Realm used by apache shiro framework.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class FachRefRealm extends JdbcRealm {

	/**
	 * build salted authentication info.
	 * 
	 * @param token
	 *            the authentication token
	 * 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// identify account to log to
		UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
		final String email = userPassToken.getUsername();
		if (email == null) {
			return null;
		}
		// read password hash and salt from db
		final User user = UserDAO.getUser(email);
		if (user == null) {
			return null;
		}
		// return salted credentials
		SaltedAuthenticationInfo info = new SaltedAuthInfo(email, user.getPassword(), user.getSalt());
		return info;
	}

	/**
	 * set the roles and permissions according to authorization info.
	 * 
	 * @param principals
	 *            the principal collection
	 * @return authorizationInfo the authorization information
	 * 
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthenticationException {
		String email = (String) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.setRoles(UserRoleDAO.getRoles(email));
		authorizationInfo.setStringPermissions(RolePermissionDAO.getPermission(email));
		return authorizationInfo;
	}

	protected String dataSourceName;

	/**
	 * return the name of the data source.
	 * 
	 * @return dataSourceName the name of the data source
	 * 
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}
}
