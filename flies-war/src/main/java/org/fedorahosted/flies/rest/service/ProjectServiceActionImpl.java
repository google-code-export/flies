package org.fedorahosted.flies.rest.service;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fedorahosted.flies.core.dao.AccountDAO;
import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.model.HAccount;
import org.fedorahosted.flies.core.model.HIterationProject;
import org.fedorahosted.flies.core.model.HProject;
import org.fedorahosted.flies.core.model.HProjectIteration;
import org.fedorahosted.flies.rest.dto.Project;
import org.fedorahosted.flies.rest.dto.ProjectIteration;
import org.fedorahosted.flies.rest.dto.ProjectList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.validator.InvalidStateException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

@Name("projectServiceAction")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class ProjectServiceActionImpl implements ProjectServiceAction{

	@Logger
	Log log;
	
	@In
	ProjectDAO projectDAO;
	
	@In
	Session session;
	
	@In 
	AccountDAO accountDAO;
	
	
	@Override
	public Response get(String projectSlug) {
		HProject hProject = projectDAO.getBySlug(projectSlug);
		if(hProject == null)
			return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(toMini(hProject)).build();
	}

	@Override
	public Response put(Project project) {
		HProject hProject = projectDAO.getBySlug(project.getId());
		if(hProject == null){
			hProject = new org.fedorahosted.flies.core.model.HIterationProject();
			hProject.setSlug(project.getId());
			hProject.setName(project.getName());
			hProject.setDescription(project.getDescription());
		}
		else{
			hProject.setSlug(project.getId());
			hProject.setName(project.getName());
			hProject.setDescription(project.getDescription());
		}
		
		try{
			if(!session.contains(hProject)) {
				session.save(hProject);
				session.flush();
				HAccount hAccount = accountDAO.getByUsername(Identity.instance().getCredentials().getUsername());
				if(hAccount != null && hAccount.getPerson() != null) {
					hProject.getMaintainers().add(hAccount.getPerson());
				}
				session.flush();
				return Response.created( URI.create("/projects/p/"+hProject.getSlug()) ).build();
			}
			else{
				session.flush();
				return Response.ok().build();
			}
		}
        catch(InvalidStateException e){
        	return Response.status(Status.BAD_REQUEST).build();
        }
        catch(HibernateException e){
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private static Project toMini(HProject hProject){
		Project project = new Project();
		project.setId(hProject.getSlug());
		project.setName(hProject.getName());
		project.setDescription(hProject.getDescription());
		if(hProject instanceof HIterationProject){
			HIterationProject itProject = (HIterationProject) hProject;
			for(HProjectIteration pIt : itProject.getProjectIterations()){
				project.getIterations().add(
								new ProjectIteration(
										pIt.getSlug(),
										pIt.getName(), 
										pIt.getDescription()
								)
					);
			}
		}
		
		return project;
	}
	
}
