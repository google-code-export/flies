package org.fedorahosted.flies.rest.service;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.fedorahosted.flies.dao.AccountDAO;
import org.fedorahosted.flies.dao.ProjectDAO;
import org.fedorahosted.flies.model.HAccount;
import org.fedorahosted.flies.model.HIterationProject;
import org.fedorahosted.flies.model.HProject;
import org.fedorahosted.flies.model.HProjectIteration;
import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.AbstractProject;
import org.fedorahosted.flies.rest.dto.Link;
import org.fedorahosted.flies.rest.dto.Project;
import org.fedorahosted.flies.rest.dto.ProjectIterationInline;
import org.fedorahosted.flies.rest.dto.ProjectRes;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.validator.InvalidStateException;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

@Name("projectService")
@Path("/projects/p/{projectSlug}")
public class ProjectService {

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

	@In
	ETagUtils eTagUtils;
	
	@HeaderParam(HttpHeaderNames.ACCEPT)
	@DefaultValue(MediaType.APPLICATION_XML)
	MediaType accept;

	@Context
	private UriInfo uri;
	
	@GET
	@Produces( { MediaTypes.APPLICATION_FLIES_PROJECT_XML,
			MediaTypes.APPLICATION_FLIES_PROJECT_JSON,
			MediaType.APPLICATION_JSON })
	public Response get(@HeaderParam(HttpHeaderNames.IF_NONE_MATCH) EntityTag ifNoneMatch) {
		EntityTag etag = eTagUtils.generateTagForProject(projectSlug);

		if(etag == null)
			return Response.status(Status.NOT_FOUND).build();
		 		
		if(ifNoneMatch != null && etag.equals(ifNoneMatch)) {
			return Response.notModified(ifNoneMatch).build();
		}

		HProject hProject = projectDAO.getBySlug(projectSlug);
		
		ProjectRes project = toResource(hProject, accept);
		return Response.ok(project).tag(etag).build();
	}

	@PUT
	@Consumes( { MediaTypes.APPLICATION_FLIES_PROJECT_XML,
			MediaTypes.APPLICATION_FLIES_PROJECT_JSON,
			MediaType.APPLICATION_JSON })
	@Restrict("#{identity.loggedIn}")
	public Response put(Project project, @HeaderParam(HttpHeaderNames.IF_MATCH) EntityTag ifMatch) {

		EntityTag etag = eTagUtils.generateTagForProject(projectSlug);
		HProject hProject;
		
		if(etag == null) { 
			// this has to be a create operation
			
			if(ifMatch != null) {
				// the caller expected an existing resource at this location 
				return Response.status(Status.NOT_FOUND).build();
			}
			
			hProject = new org.fedorahosted.flies.model.HIterationProject();
			hProject.setSlug(project.getId());
			transfer(project, hProject);
		}
		else if(ifMatch != null && !etag.equals(ifMatch)) {
			return Response.status(Status.CONFLICT).build();
		}
		else {
			// it's an update operation
			hProject = projectDAO.getBySlug(project.getId());
			transfer(project, hProject);
		}
		
		try {
			ResponseBuilder response;
			if (!session.contains(hProject)) {
				session.save(hProject);
				session.flush();
				HAccount hAccount = accountDAO.getByUsername(Identity
						.instance().getCredentials().getUsername());
				if (hAccount != null && hAccount.getPerson() != null) {
					hProject.getMaintainers().add(hAccount.getPerson());
				}
				response =  Response.created( uri.getAbsolutePath() );
			} else {
				response = Response.ok();
			}
			session.flush();
			etag = eTagUtils.generateTagForProject(projectSlug);
			return response.tag(etag).build();
			
		} catch (InvalidStateException e) {
			String message = 
				String.format(
					"Project '%s' is invalid: \n %s", 
					projectSlug, StringUtils.join(e.getInvalidValues(),"\n"));
			log.warn(message + '\n' + project);
			log.debug(e,e);
			return Response.status(Status.BAD_REQUEST).entity(message)
					.build();
		} catch (HibernateException e) {
			log.error("Hibernate exception", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server error").build();
		}
	}

	public static void transfer(Project from, HProject to) {
		to.setName(from.getName());
		to.setDescription(from.getDescription());
	}
	
	public static void transfer(HProject from, AbstractProject to) {
		to.setId(from.getSlug());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
	}
	
	public static ProjectRes toResource(HProject hProject, MediaType mediaType) {
		ProjectRes project = new ProjectRes();
		transfer(hProject, project);
		if (hProject instanceof HIterationProject) {
			HIterationProject itProject = (HIterationProject) hProject;
			for (HProjectIteration pIt : itProject.getProjectIterations()) {
				ProjectIterationInline iteration = new ProjectIterationInline();
				ProjectIterationService.transfer(pIt, iteration);
				iteration.getLinks().add(
						new Link( URI.create("iterations/i/"+pIt.getSlug()), "self", 
								MediaTypes.createFormatSpecificType(
										MediaTypes.APPLICATION_FLIES_PROJECT_ITERATION,
										mediaType)
						));
				project.getIterations().add(iteration);
			}
		}

		return project;
	}

}
