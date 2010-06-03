package org.fedorahosted.flies.dao;

import org.fedorahosted.flies.model.HProject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("projectDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class ProjectDAO extends AbstractDAOImpl<HProject, Long>{

	public ProjectDAO() {
		super(HProject.class);
	}
	
	public ProjectDAO(Session session) {
		super(HProject.class, session);
	}
	
	public HProject getBySlug(String slug){
		return (HProject) getSession().createCriteria(HProject.class)
			.add( Restrictions.naturalId()
		        .set("slug", slug)
		    	)
		    .setCacheable(true)
		    .setComment("ProjectDAO.getBySlug")
		    .uniqueResult();
	}
	
}
