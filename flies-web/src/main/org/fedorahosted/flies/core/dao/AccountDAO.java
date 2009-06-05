package org.fedorahosted.flies.core.dao;

import javax.persistence.EntityManager;

import org.fedorahosted.flies.core.model.Account;
import org.fedorahosted.flies.core.model.Project;
import org.fedorahosted.flies.core.model.ProjectTarget;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.Hex;

import java.security.MessageDigest;
import java.security.SecureRandom;

@Name("accountDAO")
@AutoCreate
public class AccountDAO {

	@In
	EntityManager entityManager;
	
	public Account getByUsername(String username){
		Session session = (Session) entityManager.getDelegate();
		return (Account) session.createCriteria(Account.class)
			.add( Restrictions.naturalId()
		        .set("username", username))
		    .uniqueResult();
	}

	public Account getByApiKey(String apikey) {
		Session session = (Session) entityManager.getDelegate();
		return (Account) session.createCriteria(Account.class).add(
				Restrictions.eq("apiKey", apikey)).uniqueResult();
	}

	public void createApiKey(Account account) {
		String username = account.getUsername();
		String apikey = createSaltedApiKey(username);
		account.setApiKey(apikey);
	}

	public static String createSaltedApiKey(String username) {
		try {
			byte[] salt = new byte[16];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] name = username.getBytes("UTF-8");

			// add salt
			byte[] salted = new byte[name.length + salt.length];
			System.arraycopy(name, 0, salted, 0, name.length);
			System.arraycopy(salt, 0, salted, name.length, salt.length);

			// generate md5 digest
			md5.reset();
			byte[] digest = md5.digest(salted);

			return new String(Hex.encodeHex(digest));
			
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}

	}
}
