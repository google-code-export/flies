package net.openl10n.flies.rest.dto.v1;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationException;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.common.ResourceType;
import net.openl10n.flies.rest.JaxbUtil;
import net.openl10n.flies.rest.dto.Person;
import net.openl10n.flies.rest.dto.extensions.comment.SimpleComment;
import net.openl10n.flies.rest.dto.extensions.gettext.HeaderEntry;
import net.openl10n.flies.rest.dto.extensions.gettext.PoHeader;
import net.openl10n.flies.rest.dto.extensions.gettext.PoTargetHeader;
import net.openl10n.flies.rest.dto.extensions.gettext.PotEntryHeader;
import net.openl10n.flies.rest.dto.extensions.gettext.TextFlowExtension;
import net.openl10n.flies.rest.dto.extensions.gettext.TextFlowTargetExtension;
import net.openl10n.flies.rest.dto.resource.Resource;
import net.openl10n.flies.rest.dto.resource.ResourceMeta;
import net.openl10n.flies.rest.dto.resource.TextFlow;
import net.openl10n.flies.rest.dto.resource.TextFlowTarget;
import net.openl10n.flies.rest.dto.resource.TranslationsResource;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationTests
{

   protected ObjectMapper mapper;
   private final Logger log = LoggerFactory.getLogger(SerializationTests.class);

   @Before
   public void setup()
   {
      mapper = new ObjectMapper();
      // AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
      // mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
      // mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
   }

   private Person createPerson()
   {
      return new Person("id", "name");
   }

   @Test
   public void serializeAndDeserializePerson() throws JAXBException, JsonGenerationException, JsonMappingException, IOException
   {
      Person p = createPerson();
      JaxbUtil.validateXml(p);

      String output = mapper.writeValueAsString(p);

      Person p2 = mapper.readValue(output, Person.class);
      assertThat(p2, notNullValue());
      JaxbUtil.validateXml(p2);

      p2 = JaxbTestUtil.roundTripXml(p);
      assertThat(p2, notNullValue());
   }

   private PoHeader createPoHeader()
   {
      return new PoHeader("hello world");
   }

   @Test
   public void serializeAndDeserializeExtension() throws JsonGenerationException, JsonMappingException, IOException, JAXBException
   {
      // TODO are we actually trying to test serializing an extension where the type is not known?
      
      PoHeader e = createPoHeader();
      JaxbUtil.validateXml(e);

      String output = mapper.writeValueAsString(e);
      PoHeader e2 = mapper.readValue(output, PoHeader.class);
      JaxbUtil.validateXml(e2);
      assertThat(e2, instanceOf(PoHeader.class));

      e2 = JaxbTestUtil.roundTripXml(e, PoHeader.class);
      assertThat(e2, instanceOf(PoHeader.class));
   }

   @Test
   public void serializeAndDeserializeTranslationResource() throws JsonGenerationException, JsonMappingException, IOException, JAXBException
   {
      ResourceMeta res = new ResourceMeta("id");
      res.getExtensions(true).add(new PoHeader("comment", new HeaderEntry("h1", "v1"), new HeaderEntry("h2", "v2")));
      JaxbUtil.validateXml(res, PoHeader.class);

      String output = mapper.writeValueAsString(res);
      ResourceMeta res2 = mapper.readValue(output, ResourceMeta.class);

      assertThat(res2.getExtensions().size(), is(1));
      assertThat(res2.getExtensions().iterator().next(), instanceOf(PoHeader.class));
      assertThat(((PoHeader) res2.getExtensions().iterator().next()).getComment(), is("comment"));

      res2 = JaxbTestUtil.roundTripXml(res, PoHeader.class);
      assertThat(res2, notNullValue());
      assertThat(res2.getExtensions().size(), is(1));
      assertThat(res2.getExtensions().iterator().next(), instanceOf(PoHeader.class));
   }

   @Test
   public void serializeSourceResource() throws JsonGenerationException, JsonMappingException, IOException, JAXBException
   {
      Resource sourceResource = new Resource("Acls.pot");
      sourceResource.setType(ResourceType.FILE);
      sourceResource.setContentType(ContentType.PO);
      sourceResource.setLang(LocaleId.EN);
      TextFlow tf = new TextFlow();
      tf.setContent("ttff");
      TextFlow tf2 = new TextFlow();
      tf2.setContent("ttff2");
      sourceResource.getTextFlows().add(tf);
      sourceResource.getTextFlows().add(tf2);
      sourceResource.getExtensions(true).add(new PoHeader("comment", new HeaderEntry("h1", "v1"), new HeaderEntry("h2", "v2")));

      JaxbUtil.validateXml(sourceResource, Resource.class);

      String output = mapper.writeValueAsString(sourceResource);
      log.info(output);
      Resource res2 = mapper.readValue(output, Resource.class);

      assertThat(res2.getExtensions().size(), is(1));
      assertThat(res2.getExtensions().iterator().next(), instanceOf(PoHeader.class));
      assertThat(((PoHeader) res2.getExtensions().iterator().next()).getComment(), is("comment"));
   }

   @Test
   public void serializeAndDeserializeTextFlow() throws ValidationException, JsonGenerationException, JsonMappingException, IOException
   {
      TextFlow tf = new TextFlow();
      tf.setContent("ttff");
      SimpleComment comment = new SimpleComment("test");
      PotEntryHeader pot = new PotEntryHeader();
      pot.setContext("context");
      pot.getReferences().add("fff");
      pot.setExtractedComment("extractedComment");
      tf.getExtensions(true).add(comment);
      tf.getExtensions(true).add(pot);

      JaxbUtil.validateXml(tf, TextFlow.class);

      String output = mapper.writeValueAsString(tf);
      TextFlow res2 = mapper.readValue(output, TextFlow.class);

      assertThat(res2.getExtensions(true).size(), is(2));
      for (TextFlowExtension e : res2.getExtensions())
      {
         if (e instanceof SimpleComment)
         {
            assertThat(((SimpleComment) e).getValue(), is("test"));
         }
         if (e instanceof PotEntryHeader)
         {
            assertThat(((PotEntryHeader) e).getContext(), is("context"));
         }
      }
   }
   
   @Test
   public void serializeAndDeserializeTextFlowTarget() throws ValidationException, JsonGenerationException, JsonMappingException, IOException
   {
      TextFlowTarget tf = new TextFlowTarget();
      tf.setTranslator(createPerson());
      tf.setContent("ttff");
      SimpleComment comment = new SimpleComment("testcomment");
      tf.getExtensions(true).add(comment);

      JaxbUtil.validateXml(tf, TextFlowTarget.class);

      String output = mapper.writeValueAsString(tf);
      TextFlowTarget res2 = mapper.readValue(output, TextFlowTarget.class);

      assertThat(res2.getExtensions(true).size(), is(1));
      for (TextFlowTargetExtension e : res2.getExtensions())
      {
         if (e instanceof SimpleComment)
         {
            assertThat(((SimpleComment) e).getValue(), is("testcomment"));
         }
      }
   }

   @Test
   public void serializeAndDeserializeTranslation() throws JsonGenerationException, JsonMappingException, IOException, JAXBException
   {
      TranslationsResource entity = new TranslationsResource();
      TextFlowTarget target = new TextFlowTarget("rest1");
      target.setContent("hello world");
      target.setState(ContentState.Approved);
      target.setTranslator(new Person("root@localhost", "Admin user"));
      // for the convenience of test
      entity.getTextFlowTargets().add(target);
      entity.getExtensions(true);
      PoTargetHeader poTargetHeader = new PoTargetHeader("target header comment", new HeaderEntry("ht", "vt1"), new HeaderEntry("th2", "tv2"));

      entity.getExtensions(true).add(poTargetHeader);

      JaxbUtil.validateXml(entity, TranslationsResource.class);

      String output = mapper.writeValueAsString(entity);
      TranslationsResource res2 = mapper.readValue(output, TranslationsResource.class);

      assertThat(res2.getExtensions().size(), is(1));
      assertThat(res2.getExtensions().iterator().next(), instanceOf(PoTargetHeader.class));
      assertThat(((PoTargetHeader) res2.getExtensions().iterator().next()).getComment(), is("target header comment"));
   }

}
