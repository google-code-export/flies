package org.fedorahosted.flies.rest.service;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.model.IterationProject;
import org.fedorahosted.flies.core.model.ProjectIteration;
import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.ProjectIterationRef;
import org.fedorahosted.flies.rest.dto.ProjectIterationRefs;
import org.fedorahosted.flies.rest.dto.ProjectRef;
import org.fedorahosted.flies.rest.dto.Project;
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
@Path("/projects")
public class ProjectService{

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

	public Response addProject(String projectSlug,
			org.fedorahosted.flies.rest.dto.Project project) {
		checkPermissions();
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Path("/p/{projectSlug}")
	@Produces({ MediaTypes.APPLICATION_FLIES_PROJECT_XML, MediaType.APPLICATION_JSON })
	public org.fedorahosted.flies.rest.dto.Project getProject(
			@PathParam("projectSlug") String projectSlug) {
		checkPermissions();

		org.fedorahosted.flies.core.model.Project p = projectDAO.getBySlug(projectSlug);
		if(p == null)
			throw new NotFoundException("Project not found: "+projectSlug);
		
		return toMini(p);
	}

	private Project toMini(org.fedorahosted.flies.core.model.Project p){
		Project proj = new Project();
		proj.setId(p.getSlug());
		proj.setName(p.getName());
		proj.setDescription(p.getDescription());
		if(p instanceof IterationProject){
			IterationProject itProject = (IterationProject) p;
			for(ProjectIteration pIt : itProject.getProjectIterations()){
				proj.getIterations().add(
						new ProjectIterationRef(
								new org.fedorahosted.flies.rest.dto.ProjectIteration(
										pIt.getSlug(),
										pIt.getName(), 
										pIt.getDescription()
								)
						)
					);
			}
		}
		
		return proj;
	}
	
	@GET
	@Produces({ MediaTypes.APPLICATION_FLIES_PROJECTS_XML, MediaType.APPLICATION_JSON })
	public ProjectRefs getProjects() {
		checkPermissions();
		ProjectRefs projectRefs = new ProjectRefs();
		
		List<org.fedorahosted.flies.core.model.Project> projects = session.createQuery("select p from Project p").list();
		
		for(org.fedorahosted.flies.core.model.Project p : projects){
			org.fedorahosted.flies.rest.dto.Project proj = 
				new org.fedorahosted.flies.rest.dto.Project(p.getSlug(), p.getName(), p.getDescription());
			projectRefs.getProjects().add( new ProjectRef( proj ));
		}
		
		return projectRefs;
	}

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
