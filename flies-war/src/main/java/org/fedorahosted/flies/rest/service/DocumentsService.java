package org.fedorahosted.flies.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fedorahosted.flies.core.dao.DocumentDAO;
import org.fedorahosted.flies.core.dao.ProjectDAO;
import org.fedorahosted.flies.core.dao.ProjectIterationDAO;
import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.Documents;
import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

@Name("documentsService")
@Path("/projects/p/{projectSlug}/iterations/i/{iterationSlug}/documents")
public class DocumentsService {
	
	@PathParam("projectSlug")
	private String projectSlug;
	
	@PathParam("iterationSlug")
	private String iterationSlug;
	
	@Context 
	private HttpServletRequest request;

	@Context
	UriInfo uri;
	
	@In
	ProjectDAO projectDAO;
	
	@In
	ProjectIterationDAO projectIterationDAO;
	
	@In
	DocumentDAO documentDAO;
	
	@In
	Session session;

	@In("DocumentsServiceActionImpl")
	DocumentsServiceAction impl;

	@POST
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	@Restrict("#{identity.loggedIn}")
	public Response post(Documents documents) {
	    return impl.post(documents);
	}

	@GET
	@Produces({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	public Response get() {
	    return impl.get();
	}

	@PUT
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	@Restrict("#{identity.loggedIn}")
	public Response put(Documents documents) {
	    impl.put(documents);
	    return Response.ok().build();
	}
	
	public String getProjectSlug() {
	    return projectSlug;
	}
	
	public String getIterationSlug() {
	    return iterationSlug;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public UriInfo getUri() {
		return uri;
	}
}
