package org.fedorahosted.flies.core.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ws.rs.core.EntityTag;

import org.apache.commons.lang.StringUtils;
import org.fedorahosted.flies.account.action.RegisterAction;
import org.fedorahosted.flies.core.model.HProject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("projectDAO")
@AutoCreate
public class ProjectDAO {

	@In
	Session session;
	
	public HProject getBySlug(String slug){
		return (HProject) session.createCriteria(HProject.class)
			.add( Restrictions.naturalId()
		        .set("slug", slug)
		    	)
		    .setCacheable(true)
		    .setComment("ProjectDAO.getBySlug")
		    .uniqueResult();
	}
	
	/**
	 * Retrieves the ETag for the Project
	 * 
	 * This algorithm takes into account changes in Project Iterations as well.
	 * 
	 * @param slug Project slug
	 * @return calculated EntityTag or null if project does not exist
	 */
	public EntityTag getETag(String slug) {
		Integer projectVersion = (Integer) session.createQuery(
		"select p.versionNum from HProject p where slug =:slug")
		.setParameter("slug", slug)
		.uniqueResult();
		
		if(projectVersion == null)
			return null;
		
		List<Integer> iterationVersions =  session.createQuery(
		"select i.versionNum from HProjectIteration i where i.project.slug =:slug")
		.setParameter("slug", slug).list();

		String hash = RegisterAction.generateHash(projectVersion + ':' + StringUtils.join(iterationVersions, ':'));
		
		return EntityTag.valueOf( hash );
	}
	
}
