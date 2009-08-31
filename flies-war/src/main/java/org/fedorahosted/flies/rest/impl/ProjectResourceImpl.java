package org.fedorahosted.flies.rest.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.model.IterationProject;
import org.fedorahosted.flies.core.model.Project;
import org.fedorahosted.flies.rest.ProjectIterationResource;
import org.fedorahosted.flies.rest.ProjectResource;
import org.fedorahosted.flies.rest.dto.ProjectIterationRef;
import org.fedorahosted.flies.rest.dto.ProjectIterationRefs;
import org.fedorahosted.flies.rest.dto.ProjectRef;
import org.fedorahosted.flies.rest.dto.ProjectRefs;
import org.hibernate.Session;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

@Name("projectResource")
@Path("/project/")
public class ProjectResourceImpl implements ProjectResource{

	@Logger
	Log log;
	
	@In
	ProjectDAO projectDAO;
	
	@In
	Session session;
	
	@Context
	HttpServletResponse response;

	@Context 
	HttpServletRequest request;

	@Override
	public Response addProject(String projectSlug,
			org.fedorahosted.flies.rest.dto.Project project) {
		checkPermissions();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.fedorahosted.flies.rest.dto.Project getProject(String projectSlug) {
		checkPermissions();

		Project p = projectDAO.getBySlug(projectSlug);
		if(p == null)
			throw new NotFoundException("Project not found: "+projectSlug);
		
		return toMini(p);
	}

	private org.fedorahosted.flies.rest.dto.Project toMini(Project p){
		return new org.fedorahosted.flies.rest.dto.Project();
	}
	
	@Override
	public ProjectIterationResource getProjectIterationResource(
			String projectSlug) {
		checkPermissions();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProjectRefs getProjects() {
		checkPermissions();
		ProjectRefs projectRefs = new ProjectRefs();
		
		List<Project> projects = session.createQuery("select p from Project p").list();
		
		for(Project p : projects){
			org.fedorahosted.flies.rest.dto.Project proj = 
				new org.fedorahosted.flies.rest.dto.Project(p.getSlug(), p.getName(), p.getDescription());
			projectRefs.getProjects().add( new ProjectRef( proj ));
		}
		
		return projectRefs;
	}

	@Override
	public Response updateProject(String projectSlug,
			org.fedorahosted.flies.rest.dto.Project project) {
		checkPermissions();
		// TODO Auto-generated method stub
		return null;
	}

	private void checkPermissions(){
		String authToken = request.getHeader("X-Auth-Token");
		log.info("Attempted to authenticate with token {0}", authToken);
		if(!"bob".equals(authToken)){
			throw new UnauthorizedException();
		}
	}	
	
	
}
