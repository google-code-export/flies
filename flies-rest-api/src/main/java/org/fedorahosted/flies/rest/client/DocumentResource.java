package org.fedorahosted.flies.rest.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.DocumentRefs;
import org.fedorahosted.flies.rest.dto.Documents;
import org.jboss.resteasy.client.ClientResponse;

public interface DocumentResource {

	@GET
	@Path("/d/{documentId}")
	@Produces({ MediaTypes.APPLICATION_FLIES_DOCUMENT_XML, MediaType.APPLICATION_JSON })
	public ClientResponse<Document> getDocument(@PathParam("documentId") String documentId,
			@QueryParam("includeTargets") String includeTargets);

	/**
	 * Add/Update one Document.
	 */
	@PUT
	@Path("/d/{documentId}")
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENT_XML, MediaType.APPLICATION_JSON })
	public Response putDocument(Document document);

	@GET
	@Produces({ MediaTypes.APPLICATION_FLIES_DOCUMENTREFS_XML, MediaType.APPLICATION_JSON })
	public ClientResponse<DocumentRefs> getDocumentRefs();
	
	/**
	 * Adds multiple Documents
	 */
	@POST
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	public Response addDocuments(Documents documents);
	
	/**
	 * Replaces the existing set of documents (possibly should be POST to ., not ./replace)
	 * @param documents
	 * @return
	 */
	@PUT
	@Consumes({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	public Response replace(Documents documents);

	@GET
	@Path("/all")
	@Produces({ MediaTypes.APPLICATION_FLIES_DOCUMENTS_XML, MediaType.APPLICATION_JSON })
	public ClientResponse<Documents> getDocuments();
}
