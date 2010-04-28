package org.fedorahosted.flies.rest.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.Document;
import org.jboss.resteasy.client.ClientResponse;

//@Path("/d/{documentId}")
public interface IDocumentResource {

	/**
	 * 
	 * @param resources  
	 * @return
	 */
	@GET
	@Produces({ MediaTypes.APPLICATION_FLIES_DOCUMENT_XML, MediaTypes.APPLICATION_FLIES_DOCUMENT_JSON })
	public ClientResponse<Document> get(
			@QueryParam("resources") ContentQualifier resources);

	/**
	 * Add/Update one Document.
	 */
	@PUT
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENT_XML, MediaTypes.APPLICATION_FLIES_DOCUMENT_JSON })
	public Response put(Document document);

}
