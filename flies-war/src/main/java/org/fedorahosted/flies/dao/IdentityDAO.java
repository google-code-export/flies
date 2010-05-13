package org.fedorahosted.flies.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.fedorahosted.flies.model.HAccount;
import org.fedorahosted.flies.model.HAccountRole;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.management.PasswordHash;

@Name("identityDAO")
@AutoCreate
public class IdentityDAO {

	@In
	private EntityManager entityManager;
	
	@In 
	private AccountDAO accountDAO;

	public boolean roleExists(String role) {
		return getRole(role) != null;
	}
	
	public HAccountRole getRole(String roleName) {
		Session session = (Session) entityManager.getDelegate();
		return (HAccountRole) session.createCriteria(HAccountRole.class)
			.add( Restrictions.naturalId()
		        .set("name", roleName))
		    .uniqueResult();
	}

	public List<HAccount> listMembers(String role) {
		Session session = (Session) entityManager.getDelegate();
		return session.createQuery("from HAccount account where :role member of account.roles")
			.setParameter("role", getRole(role))
			.list();
	}

	public boolean createRole(String roleName, String... includesRoles) {
		HAccountRole role = new HAccountRole();
		role.setName(roleName);
		for (String includeRole : includesRoles) {
			Set<HAccountRole> groups = role.getGroups();
			if(groups == null) {
				groups = new HashSet<HAccountRole>();
				role.setGroups(groups);
			}
			groups.add(getRole(includeRole));
		}
		entityManager.persist(role);
		return true;
	}

	@SuppressWarnings("deprecation")
	public HAccount createUser(String username, String password, boolean enabled) {
		HAccount account = new HAccount();
		account.setUsername(username);
		// TODO add a @PasswordSalt field to HAccount
		// otherwise, Seam uses the @UserPrincipal field as salt
		String saltPhrase = username;
		String passwordHash = PasswordHash.instance().generateSaltedHash(password, saltPhrase, PasswordHash.ALGORITHM_MD5);
		account.setPasswordHash(passwordHash);
		account.setEnabled(enabled);
		entityManager.persist(account);
		return account;
	}

	public void grantRole(String username, String roleName) {
		HAccount account = accountDAO.getByUsername(username);
		HAccountRole role = getRole(roleName);
		account.getRoles().add(role);
	}

}
