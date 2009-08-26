package org.fedorahosted.flies.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import net.openl10n.api.Namespaces;
import net.openl10n.api.rest.project.Project;

public interface LocProjectResource {

	@GET
	@Produces({ "application/openl10n.project+xml", "application/json" })
	public Project get(
			@QueryParam("ext") @DefaultValue("") String extensions
			);

	@POST
	@Consumes( { "application/openl10n.project+xml", "application/json" })
	public Response post(Project project);

	@PUT
	@Consumes( { "application/openl10n.project+xml", "application/json" })
	public Response put(Project project);

	@Path("documents/{documentId}")
	public DocumentResource getDocument(String documentId);

}
