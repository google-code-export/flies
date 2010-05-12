package org.fedorahosted.flies.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.dbunit.operation.DatabaseOperation;
import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.rest.ApiKeyHeaderDecorator;
import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.ContentQualifier;
import org.fedorahosted.flies.rest.client.IDocumentResource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.Documents;
import org.fedorahosted.flies.rest.dto.Link;
import org.fedorahosted.flies.rest.dto.Relationships;
import org.fedorahosted.flies.rest.dto.TextFlow;
import org.fedorahosted.flies.rest.dto.TextFlowTarget;
import org.fedorahosted.flies.rest.dto.TextFlowTargets;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test(groups={"seam-tests"},suiteName="DocumentService")
public class DocumentServiceSeamTest extends FliesDBUnitSeamTest {
	
	ClientRequestFactory clientRequestFactory;
	URI baseUri = URI.create("/restv1/");
	
	private static final String url = "projects/p/sample-project/iterations/i/1.0/documents/d/";
	
	@BeforeClass
	public void prepareRestEasyClientFramework() throws Exception {
		ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
		RegisterBuiltin.register(instance);

		clientRequestFactory = 
			new ClientRequestFactory(
					new SeamMockClientExecutor(this), baseUri);
		
		clientRequestFactory.getPrefixInterceptors().registerInterceptor(new ApiKeyHeaderDecorator("admin", "12345678901234567890123456789012"));

		
	}
	
	private IDocumentResource getDocumentService(String docId){
		return clientRequestFactory.createProxy(IDocumentResource.class, baseUri.resolve(url).resolve(docId));
		
	}
	
	@Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
        		new DataSetOperation("META-INF/testdata/AccountData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
        		new DataSetOperation("META-INF/testdata/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
                new DataSetOperation("org/fedorahosted/flies/rest/service/DocumentTestData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        
    }


	public void getDocumentThatDoesntExist(){
		IDocumentResource documentResource = getDocumentService("my,doc,does,not,exist.txt");
		ClientResponse<Document> clientResponse = documentResource.get(null);
		assertThat ( clientResponse.getResponseStatus(), is(Status.NOT_FOUND) );
		assertThat ( clientResponse.getEntity(String.class), is("Document not found"));
	}

	public void getDocument() throws URIException {
		String docUri = "my,path,document.txt";
		IDocumentResource documentResource = getDocumentService(docUri);
		ClientResponse<Document> response = documentResource.get(null);
		assertThat( response.getResponseStatus(), is(Status.OK) ) ;
		Document doc = response.getEntity();
		assertThat( doc.getId(), is("/my/path/document.txt") );
		assertThat( doc.getName(), is("document.txt") );
		assertThat( doc.getContentType(), is(ContentType.TextPlain) );
		assertThat( doc.getLang(), is(LocaleId.EN_US) );
		assertThat( doc.getRevision(), is(1) );

		Link link = doc.getLinks().findLinkByRel(Relationships.SELF);
		assertThat( link, notNullValue() );
		assertThat( URIUtil.decode(link.getHref().toString()), endsWith(url+docUri) );
		
		link = doc.getLinks().findLinkByRel(Relationships.DOCUMENT_CONTAINER);
		assertThat( link, notNullValue() );
		assertThat( link.getHref().toString(), endsWith("iterations/i/1.0") );
	}
	
	public void getDocumentWithResources() throws URIException {
		IDocumentResource documentResource = getDocumentService("my,path,document.txt");
		ClientResponse<Document> response = documentResource.get( ContentQualifier.ALL );
		assertThat( response.getResponseStatus(), is(Status.OK) ) ;
		Document doc = response.getEntity();
		assertThat( doc.getTextFlows().size(), is(1) );

		response = documentResource.get( ContentQualifier.SOURCE );
		assertThat( response.getResponseStatus(), is(Status.OK) ) ;
		doc = response.getEntity();
		assertThat("Should have one textFlow", doc.getTextFlows().size(), is(1) );
		// FIXME breaking test disabled
//		assertThat("No targets should be included", ((TextFlow)doc.getResources().get(0)).getExtension(TextFlowTargets.class), nullValue() );

		LocaleId nbLocale = new LocaleId("nb-NO");
		response = documentResource.get( ContentQualifier.fromLocales(nbLocale));
		assertThat( response.getResponseStatus(), is(Status.OK) ) ;
		doc = response.getEntity();
		assertThat("should have one textFlow", doc.getTextFlows().size(), is(1) );
		
		LocaleId deLocale = new LocaleId("de-DE");
		response = documentResource.get( ContentQualifier.fromLocales( nbLocale, deLocale));
		assertThat( response.getResponseStatus(), is(Status.OK) ) ;
		doc = response.getEntity();
		List<TextFlow> textFlows = doc.getTextFlows(); 
		assertThat( textFlows.size(), is(1) );
		TextFlow tf = (TextFlow) textFlows.get(0);
		assertThat( tf, notNullValue());
		assertThat("should have a textflow with this id", tf.getId(), is("tf1") );

		TextFlowTarget tfTarget = tf.getTarget(nbLocale);
		assertThat("expected nb-NO target", tfTarget, notNullValue());
		assertThat("expected translation for nb-NO", tfTarget.getContent(), is("hei verden"));
		
		tfTarget = tf.getTarget(deLocale);
		assertThat("exected de-DE target", tfTarget, notNullValue());
		assertThat("expected translation for de-DE",  tfTarget.getContent(), is("hello welt"));
	}
	
	public void putNewDocument() {
		String docUrl = "my,fancy,document.txt";
		IDocumentResource documentResource = getDocumentService(docUrl);
		Document doc = new Document("/my/fancy/document.txt", ContentType.TextPlain);
		Response response = documentResource.put(doc);

		assertThat( response.getStatus(), is(Status.CREATED.getStatusCode()) );
		assertThat( response.getMetadata().getFirst("Location").toString() , endsWith(url+docUrl) );

		ClientResponse<Document> documentResponse = documentResource.get(null);
		
		assertThat( documentResponse.getResponseStatus(), is(Status.OK) );
		
		doc = documentResponse.getEntity(); 
		assertThat( doc.getRevision(), is(1) );
		Link link = doc.getLinks().findLinkByRel(Relationships.SELF); 
		assertThat( link, notNullValue() );
		assertThat( link.getHref().toString(), endsWith(url+docUrl) );
		
		link = doc.getLinks().findLinkByRel(Relationships.DOCUMENT_CONTAINER); 
		assertThat( link, notNullValue() );
		assertThat( link.getType(), is( MediaTypes.APPLICATION_FLIES_PROJECT_ITERATION_XML) );
	}

	public void putNewDocumentWithResources() throws Exception {
		String docUrl = "my,fancy,document.txt";
		IDocumentResource documentResource = getDocumentService(docUrl);
		Document doc = new Document("/my/fancy/document.txt", ContentType.TextPlain);
		List<TextFlow> textFlows = doc.getTextFlows();
		
		TextFlow textFlow = new TextFlow("tf1");
		textFlow.setContent("hello world!");
		textFlows.add(textFlow);
		
		TextFlow tf3 = new TextFlow("tf3");
		tf3.setContent("more text");
		textFlows.add(tf3);
		
		Marshaller m = null;
		JAXBContext jc = JAXBContext.newInstance(Documents.class);
		m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(doc, System.out);

		
		Response response = documentResource.put(doc);

		assertThat( response.getStatus(), is(Status.CREATED.getStatusCode()) );
		assertThat( response.getMetadata().getFirst("Location").toString() , endsWith(url+docUrl) );

		ClientResponse<Document> documentResponse = documentResource.get(ContentQualifier.SOURCE);
		
		assertThat( documentResponse.getResponseStatus(), is(Status.OK) );
		
		doc = documentResponse.getEntity(); 

		assertThat( doc.getRevision(), is(1) );
		
		assertThat("Should have textFlows", doc.getTextFlows(), notNullValue() );
		assertThat("Should have 2 textFlows", doc.getTextFlows().size(), is(2) );
		assertThat("Should have tf1 textFlow", doc.getTextFlows().get(0).getId(), is("tf1") );
		assertThat("Container1 should have tf3 textFlow", doc.getTextFlows().get(1).getId(), is(tf3.getId()));
		assertThat("No targets should be included", ((TextFlow)doc.getTextFlows().get(0)).getExtension(TextFlowTargets.class), nullValue() );
		
		textFlow = (TextFlow) doc.getTextFlows().get(0);
		textFlow.setId("tf2");
		
		response = documentResource.put(doc);
		assertThat( response.getStatus(), is(205) );
		documentResponse = documentResource.get(ContentQualifier.SOURCE);
		assertThat( documentResponse.getResponseStatus(), is(Status.OK) );
		doc = documentResponse.getEntity(); 
		assertThat( doc.getRevision(), is(2) );
		assertThat("Should have textFlows", doc.getTextFlows(), notNullValue() );
		assertThat("Should have two textFlows", doc.getTextFlows().size(), is(2) );
		assertThat("should have same id", doc.getTextFlows().get(0).getId(), is("tf2") );
	}

}
