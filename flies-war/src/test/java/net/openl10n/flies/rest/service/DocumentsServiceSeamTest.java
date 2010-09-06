package net.openl10n.flies.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import net.openl10n.flies.FliesDBUnitSeamTest;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.dao.ProjectIterationDAO;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HProjectIteration;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.rest.client.FliesClientRequestFactory;
import net.openl10n.flies.rest.client.IDocumentsResource;
import net.openl10n.flies.rest.dto.deprecated.Document;
import net.openl10n.flies.rest.dto.deprecated.Documents;
import net.openl10n.flies.rest.dto.deprecated.SimpleComment;
import net.openl10n.flies.rest.dto.deprecated.TextFlow;
import net.openl10n.flies.rest.dto.deprecated.TextFlowTarget;
import net.openl10n.flies.rest.dto.po.HeaderEntry;
import net.openl10n.flies.rest.dto.po.PoHeader;
import net.openl10n.flies.rest.dto.po.PoTargetHeader;
import net.openl10n.flies.rest.dto.po.PoTargetHeaders;
import net.openl10n.flies.rest.dto.po.PotEntryData;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = { "seam-tests" })
public class DocumentsServiceSeamTest extends FliesDBUnitSeamTest
{

   private static final String AUTH_KEY = "b6d7044e9ee3b2447c28fb7c50d86d98";
   private static final String USERNAME = "admin";
   private static final String DOCUMENTS_DATA_DBUNIT_XML = "net/openl10n/flies/test/model/DocumentsData.dbunit.xml";
   private static final String PROJECTS_DATA_DBUNIT_XML = "net/openl10n/flies/test/model/ProjectsData.dbunit.xml";
   private static final LocaleId DE_DE = LocaleId.fromJavaName("de_DE");
   private static final LocaleId FR = LocaleId.fromJavaName("fr");

   private final Logger log = LoggerFactory.getLogger(DocumentsServiceSeamTest.class);

   String projectSlug = "sample-project";
   String iter = "1.1";
   IDocumentsResource docsService;

   @BeforeClass
   public void prepareRestEasyClientFramework() throws Exception
   {
      docsService = prepareRestEasyClientFramework(projectSlug, iter);
   }

   public IDocumentsResource prepareRestEasyClientFramework(String projectSlug, String iter) throws Exception
   {
      FliesClientRequestFactory clientRequestFactory = new FliesClientRequestFactory(new URI("http://example.com/"), USERNAME, AUTH_KEY, new SeamMockClientExecutor(this));
      return clientRequestFactory.getDocuments(new URI("/restv1/projects/p/" + projectSlug + "/iterations/i/" + iter + "/documents"));
   }

   @Override
   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation(DOCUMENTS_DATA_DBUNIT_XML, DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation(PROJECTS_DATA_DBUNIT_XML, DatabaseOperation.CLEAN_INSERT));
      afterTestOperations.add(new DataSetOperation(PROJECTS_DATA_DBUNIT_XML, DatabaseOperation.DELETE_ALL));
      afterTestOperations.add(new DataSetOperation(DOCUMENTS_DATA_DBUNIT_XML, DatabaseOperation.DELETE_ALL));
   }

   public void getZero() throws Exception
   {
      // log.info("getZero()");
      expectDocs(true, false);
   }

   private void expectDocs(boolean checkRevs, boolean checkLinks, Document... docs)
   {
      ClientResponse<Documents> response = docsService.getDocuments();

      assertThat(response.getStatus(), is(200));
      assertThat(response.getEntity(), notNullValue());
      Set<String> expectedDocs = new TreeSet<String>();
      for (Document doc : docs)
      {
         expectedDocs.add(doc.toString());
      }
      // assertThat(response.getEntity().getDocuments().size(),
      // is(Arrays.asList(docs).size()));
      Set<String> actualDocs = new TreeSet<String>();
      for (Document doc : response.getEntity().getDocuments())
      {
         if (!checkLinks)
            // leave links out of the XML comparison
            doc.getLinks().clear();
         if (!checkRevs)
            clearRevs(doc);
         // The XML should include PoHeader, PotEntryData and target comments.
         // FIXME check this
         final String docXml = doc.toString();
         actualDocs.add(docXml);
         log.debug("actual doc: " + doc);
      }
      assertThat(actualDocs, is(expectedDocs));
   }

   private void clearRevs(Document doc)
   {
      doc.setRevision(null);
      final List<TextFlow> textFlows = doc.getTextFlows();
      if (textFlows != null)
         for (TextFlow res : textFlows)
         {
            res.setRevision(null);
            if (res instanceof TextFlow)
            {
               TextFlow tf = (TextFlow) res;
               for (TextFlowTarget tft : tf.getTargets().getTargets())
               {
                  tft.setResourceRevision(null);
               }
            }
         }
   }

   private Document newDoc(String id, TextFlow... textFlows)
   {
      ContentType contentType = ContentType.TextPlain;
      Integer revision = null;
      Document doc = new Document(id, id + "name", id + "path", contentType, revision, LocaleId.EN);
      for (TextFlow textFlow : textFlows)
      {
         doc.getTextFlows().add(textFlow);
      }
      return doc;
   }

   private TextFlow newTextFlow(String id, String sourceContent, String sourceComment, LocaleId targetLocale, String targetContent, String targetComment)
   {
      TextFlow textFlow = new TextFlow(id, LocaleId.EN);
      textFlow.setContent(sourceContent);
      if (sourceComment != null)
         textFlow.getOrAddComment().setValue(sourceComment);
      TextFlowTarget target = new TextFlowTarget(textFlow, targetLocale);
      target.setContent(targetContent);
      if (targetComment != null)
         target.getOrAddComment().setValue(targetComment);
      textFlow.addTarget(target);
      return textFlow;
   }

   private Document putPo1()
   {
      Documents docs = new Documents();
      TextFlow textflow = newTextFlow("FOOD", "Slime Mould", "POT comment", DE_DE, "Sauerkraut", "translator comment");
      TextFlowTarget target = textflow.getTarget(DE_DE);
      PotEntryData poData = textflow.getOrAddExtension(PotEntryData.class);
      poData.setId("FOOD");
      poData.setContext("context");
      poData.setExtractedComment(new SimpleComment("Tag: title"));
      List<String> flags = poData.getFlags();
      flags.add("no-c-format");
      flags.add("flag2");
      List<String> refs = poData.getReferences();
      refs.add("ref1.xml:7");
      refs.add("ref1.xml:21");
      Document doc = newDoc("foo.pot", textflow);

      PoHeader poHeader = doc.getOrAddExtension(PoHeader.class);
      poHeader.setComment("poheader comment");
      List<HeaderEntry> poEntries = poHeader.getEntries();
      poEntries.add(new HeaderEntry("Project-Id-Version", "en"));

      PoTargetHeaders targetHeaders = doc.getOrAddExtension(PoTargetHeaders.class);
      PoTargetHeader targetHeader = new PoTargetHeader();
      targetHeader.setComment("target comment");
      List<HeaderEntry> entries = targetHeader.getEntries();
      entries.add(new HeaderEntry("Project-Id-Version", "ja"));
      targetHeader.setTargetLanguage(DE_DE);
      targetHeaders.getHeaders().add(targetHeader);

      docs.getDocuments().add(doc);
      log.debug(docs.toString());
      Response response = docsService.put(docs);
      assertThat(response.getStatus(), is(200));
      return doc;
   }

   private Document putDoc1()
   {
      Documents docs = new Documents();
      Document doc = newDoc("foo.properties", newTextFlow("FOOD", "Slime Mould", "slime mould comment", DE_DE, "Sauerkraut", null));
      docs.getDocuments().add(doc);
      Response response = docsService.put(docs);
      assertThat(response.getStatus(), is(200));
      return doc;
   }

   private Document putDoc1a()
   {
      Documents docs = new Documents();
      Document doc = newDoc("foo.properties", newTextFlow("HELLO", "Hello World", null, FR, "Bonjour le Monde", "bon jour comment"));
      docs.getDocuments().add(doc);
      Response response = docsService.put(docs);
      assertThat(response.getStatus(), is(200));
      return doc;
   }

   private void putZero()
   {
      Documents docs = new Documents();
      Response response = docsService.put(docs);
      assertThat(response.getStatus(), is(200));
   }

   private Document postDoc2()
   {
      Documents docs = new Documents();
      Document doc = newDoc("test.properties", newTextFlow("HELLO", "Hello World", "hello comment", FR, "Bonjour le Monde", null));
      docs.getDocuments().add(doc);
      Response response = docsService.post(docs);
      assertThat(response.getStatus(), is(200));
      return doc;
   }

   public void put1Get() throws Exception
   {
      log.info("putGet()");
      getZero();
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      expectDocs(true, false, doc1);
   }

   public void put1Post2Get() throws Exception
   {
      log.info("putPostGet()");
      getZero();
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      expectDocs(true, false, doc1);
      Document doc2 = postDoc2();
      doc2.setRevision(1);
      TextFlow tf2 = (TextFlow) doc2.getTextFlows().get(0);
      tf2.setRevision(1);
      TextFlowTarget tft2 = tf2.getTarget(FR);
      tft2.setResourceRevision(1);
      expectDocs(true, false, doc1, doc2);
   }

   public void put1Post2Put1() throws Exception
   {
      log.info("put2Then1()");
      getZero();
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      Document doc2 = postDoc2();
      doc2.setRevision(1);
      TextFlow tf2 = (TextFlow) doc2.getTextFlows().get(0);
      tf2.setRevision(1);
      TextFlowTarget tft2 = tf2.getTarget(FR);
      tft2.setResourceRevision(1);
      expectDocs(true, false, doc1, doc2);
      // this put should have the effect of deleting doc2
      putDoc1();
      // should be identical to doc1 from before, including revisions
      expectDocs(true, false, doc1);
      // use dto to check that doc2 is marked obsolete
      verifyObsoleteDocument(doc2.getId());
   }

   public void put1Put0Put1() throws Exception
   {
      log.info("TEST: put1Put0Put1()");
      getZero();
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      expectDocs(true, false, doc1);
      putZero(); // doc1 becomes obsolete, rev 2
      getZero();
      putDoc1(); // doc1 resurrected, rev 3
      doc1.setRevision(3);
      tf1.setRevision(1);
      tft1.setResourceRevision(1);
      expectDocs(true, false, doc1);
   }

   public void put1Put0Put1a() throws Exception
   {
      log.info("TEST: put1Put0Put1a()");
      getZero();
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      expectDocs(true, false, doc1);
      putZero(); // doc1 becomes obsolete, rev 2
      getZero();
      Document doc1a = putDoc1a(); // doc1 resurrected, rev 3
      doc1a.setRevision(3);
      TextFlow tf1a = (TextFlow) doc1a.getTextFlows().get(0);
      tf1a.setRevision(3);
      TextFlowTarget tft1a = tf1a.getTarget(FR);
      tft1a.setResourceRevision(3);
      expectDocs(true, false, doc1a);
   }

   public void putPoPotGet() throws Exception
   {
      log.info("TEST: putPoPotGet()");
      getZero();
      Document po1 = putPo1();
      expectDocs(false, false, po1);
   }

   public void put1Put1a() throws Exception
   {
      log.info("TEST: put1Then1a()");
      log.info("getZero()");
      getZero();
      log.info("putDoc1()");
      Document doc1 = putDoc1();
      doc1.setRevision(1);
      TextFlow tf1 = (TextFlow) doc1.getTextFlows().get(0);
      tf1.setRevision(1);
      TextFlowTarget tft1 = tf1.getTarget(DE_DE);
      tft1.setResourceRevision(1);
      log.info("expect doc1");
      expectDocs(true, false, doc1);
      // this should completely replace doc1's textflow FOOD with HELLO
      log.info("putDoc1a()");
      Document doc1a = putDoc1a();
      doc1a.setRevision(2);
      TextFlow tf1a = (TextFlow) doc1a.getTextFlows().get(0);
      tf1a.setRevision(2);
      TextFlowTarget tft1a = tf1a.getTarget(FR);
      tft1a.setResourceRevision(2);
      log.info("expect doc1a");
      expectDocs(true, false, doc1a);
      // use dto to check that the HTextFlow FOOD (from doc1) is marked obsolete
      log.info("verifyObsoleteResource");
      verifyObsoleteResource(doc1.getId(), "FOOD");
      log.info("putDoc1() again");
      putDoc1(); // same as original doc1, but different doc rev
      doc1.setRevision(3);
      log.info("expect doc1b");
      expectDocs(true, false, doc1);
   }

   private void verifyObsoleteDocument(final String docID) throws Exception
   {
      new FacesRequest()
      {
         protected void invokeApplication() throws Exception
         {
            ProjectIterationDAO projectIterationDAO = (ProjectIterationDAO) getInstance("projectIterationDAO");
            HProjectIteration iteration = projectIterationDAO.getBySlug(projectSlug, iter);
            HDocument hDocument = iteration.getAllDocuments().get(docID);
            Assert.assertTrue(hDocument.isObsolete());
         }
      }.run();
   }

   private void verifyObsoleteResource(final String docID, final String resourceID) throws Exception
   {
      new FacesRequest()
      {
         protected void invokeApplication() throws Exception
         {
            ProjectIterationDAO projectIterationDAO = (ProjectIterationDAO) getInstance("projectIterationDAO");
            HProjectIteration iteration = projectIterationDAO.getBySlug(projectSlug, iter);
            HDocument hDocument = iteration.getAllDocuments().get(docID);
            HTextFlow hResource = hDocument.getAllTextFlows().get(resourceID);
            Assert.assertNotNull(hResource);
            Assert.assertTrue(hResource.isObsolete());
         }
      }.run();
   }

   public void getBadProject() throws Exception
   {
      log.info("getBadProject()");
      IDocumentsResource nonexistentDocsService = prepareRestEasyClientFramework("nonexistentProject", "99.9");
      ClientResponse<Documents> response = nonexistentDocsService.getDocuments();
      assertThat(response.getStatus(), is(404));
   }

}
