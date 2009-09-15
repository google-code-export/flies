package org.fedorahosted.flies.rest.service;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fedorahosted.flies.core.dao.AccountDAO;
import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.model.HAccount;
import org.fedorahosted.flies.core.model.HIterationProject;
import org.fedorahosted.flies.core.model.HProject;
import org.fedorahosted.flies.core.model.HProjectIteration;
import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.Project;
import org.fedorahosted.flies.rest.dto.ProjectIteration;
import org.fedorahosted.flies.rest.dto.ProjectIterationRef;
import org.fedorahosted.flies.rest.dto.ProjectRef;
import org.fedorahosted.flies.rest.dto.ProjectRefs;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.validator.InvalidStateException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

@Name("projectService")
@Path("/projects/p/{projectSlug}")
public class ProjectService{

	@PathParam("projectSlug")
	String projectSlug;
	
	@Logger
	Log log;
	
	@In
	ProjectDAO projectDAO;
	
	@In
	Session session;
	
	@In 
	AccountDAO accountDAO;
	
	
	@Context
	HttpServletResponse response;

	@Context 
	HttpServletRequest request;

	@GET
	@Produces({ MediaTypes.APPLICATION_FLIES_PROJECT_XML, MediaType.APPLICATION_JSON })
	public Project get() {

		HProject hProject = projectDAO.getBySlug(projectSlug);
		if(hProject == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		return toMini(hProject);
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
						new ProjectIterationRef(
								new ProjectIteration(
										pIt.getSlug(),
										pIt.getName(), 
										pIt.getDescription()
								)
						)
					);
			}
		}
		
		return project;
	}
	
	@PUT
	@Consumes({ MediaTypes.APPLICATION_FLIES_PROJECT_XML, MediaType.APPLICATION_JSON })
	@Restrict("#{identity.loggedIn}")
	public Response put(Project project) throws URISyntaxException{
		
		HProject hProject = projectDAO.getBySlug(project.getId());
		if(hProject != null){
			return Response.status(409).build();
		}
		hProject = new org.fedorahosted.flies.core.model.HIterationProject();
		hProject.setSlug(project.getId());
		hProject.setName(project.getName());
		hProject.setDescription(project.getDescription());
		String apiKey = request.getHeader(FliesRestSecurityInterceptor.X_AUTH_TOKEN_HEADER);
		if(apiKey != null) {
			HAccount hAccount = accountDAO.getByApiKey(apiKey);
			if(hAccount != null && hAccount.getPerson() != null) {
				hProject.getMaintainers().add(hAccount.getPerson());
			}
		}
		try{
			session.save(hProject);
			return Response.created( new URI("/projects/p/"+hProject.getSlug()) ).build();
		}
        catch(InvalidStateException e){
        	return Response.status(Status.BAD_REQUEST).build();
        }
        catch(HibernateException e){
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
}
