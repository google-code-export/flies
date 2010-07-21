package org.fedorahosted.flies.rest.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.dto.Account;
import org.jboss.resteasy.client.ClientResponse;

//@Path("/accounts/u/{username}")
public interface IAccountResource
{

   @GET
   @Produces(
   {MediaTypes.APPLICATION_FLIES_ACCOUNT_XML, MediaTypes.APPLICATION_FLIES_ACCOUNT_JSON})
   public ClientResponse<Account> get();

   @PUT
   @Consumes(
   {MediaTypes.APPLICATION_FLIES_ACCOUNT_XML, MediaTypes.APPLICATION_FLIES_ACCOUNT_JSON})
   public Response put(Account account);

}
