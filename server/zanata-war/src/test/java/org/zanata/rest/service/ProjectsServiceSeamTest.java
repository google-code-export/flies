package org.zanata.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.net.URI;
import java.util.List;


import org.dbunit.operation.DatabaseOperation;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.zanata.ZanataDBUnitSeamTest;
import org.zanata.rest.client.ApiKeyHeaderDecorator;
import org.zanata.rest.client.IProjectsResource;
import org.zanata.rest.dto.Project;

@Test(groups = { "seam-tests" })
public class ProjectsServiceSeamTest extends ZanataDBUnitSeamTest
{

   ClientRequestFactory clientRequestFactory;
   IProjectsResource projectService;

   @BeforeClass
   public void prepareRestEasyClientFramework() throws Exception
   {
      ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
      RegisterBuiltin.register(instance);

      clientRequestFactory = new ClientRequestFactory(new SeamMockClientExecutor(this), new URI("/restv1/"));

      clientRequestFactory.getPrefixInterceptors().registerInterceptor(new ApiKeyHeaderDecorator("admin", "b6d7044e9ee3b2447c28fb7c50d86d98", "1.0SNAPSHOT"));

      projectService = clientRequestFactory.createProxy(IProjectsResource.class);

   }

   @Override
   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("META-INF/testdata/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }

   // TODO fix and re-enable this test
   @Test(enabled = false)
   public void retrieveListofProjects() throws Exception
   {
      ClientResponse<List<Project>> response = projectService.get();

      assertThat(response.getStatus(), is(200));
      assertThat(response.getEntity(), notNullValue());
      assertThat(response.getEntity().size(), is(1));

   }
}
