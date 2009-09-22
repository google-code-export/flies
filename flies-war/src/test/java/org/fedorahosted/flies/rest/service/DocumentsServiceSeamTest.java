package org.fedorahosted.flies.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.dbunit.operation.DatabaseOperation;
import org.fedorahosted.flies.ContentType;
import org.fedorahosted.flies.LocaleId;
import org.fedorahosted.flies.rest.ApiKeyHeaderDecorator;
import org.fedorahosted.flies.rest.client.IDocumentsResource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.Documents;
import org.fedorahosted.flies.rest.dto.Resource;
import org.fedorahosted.flies.rest.dto.TextFlow;
import org.fedorahosted.flies.rest.dto.TextFlowTarget;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test(groups = { "seam-tests" })
public class DocumentsServiceSeamTest extends DBUnitSeamTest {

	ClientRequestFactory clientRequestFactory;
	IDocumentsResource docsService;

	@BeforeClass
	public void prepareRestEasyClientFramework() throws Exception {

		ResteasyProviderFactory instance = ResteasyProviderFactory
				.getInstance();
		RegisterBuiltin.register(instance);

		clientRequestFactory = new ClientRequestFactory(
				new SeamMockClientExecutor(this), (URI)null);

		clientRequestFactory.getPrefixInterceptors().registerInterceptor(
				new ApiKeyHeaderDecorator("admin",
						"12345678901234567890123456789012"));

		docsService = clientRequestFactory
				.createProxy(IDocumentsResource.class, 
					"/restv1/projects/p/sample-project/iterations/i/1.1/documents");

	}

	@Override
	protected void prepareDBUnitOperations() {
	    beforeTestOperations.add(new DataSetOperation(
		    "org/fedorahosted/flies/test/model/DocumentsData.dbunit.xml",
		    DatabaseOperation.CLEAN_INSERT));
	    afterTestOperations.add(new DataSetOperation(
			    "org/fedorahosted/flies/test/model/DocumentsData.dbunit.xml",
			    DatabaseOperation.DELETE_ALL));
	}

	public void getZero() throws Exception {
	    expectDocs(0);
	}

	private void expectDocs(int expectDocs) {
	    ClientResponse<Documents> response = docsService.getDocuments();

	    assertThat(response.getStatus(), is(200));
	    assertThat(response.getEntity(), notNullValue());
	    assertThat(response.getEntity().getDocuments().size(), is(expectDocs));
	}
	
	private Document newDoc(String id, Resource... resources) {
		ContentType contentType = ContentType.TextPlain;
		Integer version = 1;
		Document doc = new Document(id, id+"name", id+"path", contentType, version, LocaleId.EN);
		for (Resource textFlow : resources) {
			doc.getResources().add(textFlow);
		}
		return doc;
	}
	
	private TextFlow newTextFlow(String id, String sourceContent, String targetLocale, String targetContent) {
		TextFlow textFlow = new TextFlow(id, LocaleId.EN);
	    textFlow.setContent(sourceContent);
	    TextFlowTarget target = new TextFlowTarget(textFlow, LocaleId.fromJavaName(targetLocale));
	    target.setContent(targetContent);
		textFlow.addTarget(target);
		return textFlow;
	}

	private void putDoc1() {
		Documents docs = new Documents();
		Document doc = newDoc("foo.properties", 
				newTextFlow("FOOD", "Slime Mould", "de_DE", "Sauerkraut"));
		docs.getDocuments().add(doc);
		Response response = docsService.put(docs);
		assertThat(response.getStatus(), is(200));
	}
	
	private void postDoc2() {
	    Documents docs = new Documents();
	    docs.getDocuments().add(newDoc("test.properties",
	    		newTextFlow("HELLO", "Hello World", "fr", "Bonjour le Monde")));
	    Response response = docsService.post(docs);
	    assertThat(response.getStatus(), is(200));
	}
	
	public void putGet() throws Exception {
	    getZero();
	    putDoc1();
	    expectDocs(1);
	}


	
	public void putPostGet() throws Exception {
	    getZero();
	    putDoc1();
	    expectDocs(1);
	    postDoc2();
	    expectDocs(2);
	}

}
