package org.fedorahosted.flies.core.action;

import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import net.openl10n.packaging.jpa.project.HProject;

import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.model.IterationProject;
import org.fedorahosted.flies.core.model.Project;
import org.fedorahosted.flies.core.model.ProjectSeries;
import org.fedorahosted.flies.core.model.ProjectIteration;
import org.fedorahosted.flies.repository.util.TranslationStatistics;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.NaturalIdentifier;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

@Name("projectIterationHome")
@Scope(ScopeType.CONVERSATION)
public class ProjectIterationHome extends MultiSlugHome<ProjectIteration>{
	
	@Logger
	Log log;
	
	@In(value="#{projectHome.instance}", scope=ScopeType.CONVERSATION, required=false)
	IterationProject project;
	
	@In(create=true)
	ProjectDAO projectDAO;
	
	@Override
	protected ProjectIteration createInstance() {
		ProjectIteration iteration = new ProjectIteration();
		iteration.setProject(project);
		return iteration;
	}

	private String managementType;
	
	public String getManagementType() {
		return managementType;
	}
	
	public void setManagementType(String managementType) {
		this.managementType = managementType;
	}
	
	@Begin(join=true)
	public void validateSuppliedId(){
		getInstance(); // this will raise an EntityNotFound exception
					   // when id is invalid and conversation will not
		               // start
		Conversation c = Conversation.instance();
		c.setDescription(getMultiSlug());
	}

	@Override
	protected ProjectIteration loadInstance() {
		Session session = (Session) getEntityManager().getDelegate();
		return (ProjectIteration) session.createCriteria(ProjectIteration.class)
			.add( Restrictions.naturalId()
		        .set("project", projectDAO.getBySlug( getSlug(0) ) )
		        .set("slug", getId() )
		    )
			.setCacheable(true).uniqueResult();
	}

	public void verifySlugAvailable(ValueChangeEvent e) {
	    String slug = (String) e.getNewValue();
	    validateSlug(slug, e.getComponent().getId());
	}
	
	public boolean validateSlug(String slug, String componentId){
	    if (!isSlugAvailable(slug)) {
	    	FacesMessages.instance().addToControl(
	    			componentId, "This slug is not available");
	    	return false;
	    }
	    return true;
	}
	
	public boolean isSlugAvailable(String slug) {
    	try{
    		getEntityManager().createQuery("from ProjectIteration t where t.slug = :slug and t.project = :project")
    		.setParameter("slug", slug)
    		.setParameter("project", getInstance().getProject()).getSingleResult();
    		return false;
    	}
    	catch(NoResultException e){
    		// pass
    	}
    	return true;
	}
	
	@Override
	public String persist() {
		if(!validateSlug(getInstance().getSlug(), "slug"))
			return null;
		if(getInstance().getContainer() == null){
			HProject container = new HProject();
			container.setProjectId(getInstance().getProject().getSlug() + "/" + getInstance().getSlug());
			container.setName(getInstance().getName());
			getEntityManager().persist(container);
			getInstance().setContainer(container);
		}
		return super.persist();
	}
	
	public List<ProjectSeries> getAvailableProjectSeries(){
		return getEntityManager().createQuery("from ProjectSeries where project = :project")
			.setParameter("project", getInstance().getProject()).getResultList();
	}

	public void cancel(){}
	
	//@In PublicanImporter publicanImporter;
	
	@Override
	public String update() {
		String retValue = super.update();
//		if(ManagementTypes.TYPE_LOCAL.equals(getManagementType()) && !getInstance().getLocalDirectory().isEmpty()){
//			publicanImporter.process(getInstance().getLocalDirectory(), getInstance().getId());
//		}
		return retValue;
	}
	
	
}
