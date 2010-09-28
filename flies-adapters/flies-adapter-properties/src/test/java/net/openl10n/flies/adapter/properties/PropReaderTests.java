package net.openl10n.flies.adapter.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.rest.dto.deprecated.Document;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class PropReaderTests
{
   private static final Logger log = LoggerFactory.getLogger(PropReaderTests.class);

   @BeforeClass
   public static void setupContainer()
   {
      // ProjectPackage.registerPartFactory(PoHeaderPart.FACTORY);
      // ProjectPackage.registerPartFactory(PotHeaderPart.FACTORY);
      // ProjectPackage.registerPartFactory(PotEntriesDataPart.FACTORY);
   }

   PropReader propReader = new PropReader();

   @Before
   public void setup() throws IOException
   {
   }

   @Test
   public void roundtripPropsToDocXmlToProps() throws Exception
   {
      Document docOut = new Document("test.properties", ContentType.TextPlain);
      InputStream testStream = getResourceAsStream("test.properties");

      propReader.extractTemplate(docOut, new InputSource(testStream));
      String[] locales = new String[] { "fr" };
      for (String locale : locales)
      {
         InputStream targetStream = getResourceAsStream("test_" + locale + ".properties");
         propReader.extractTarget(docOut, new InputSource(targetStream), new LocaleId(locale), ContentState.New);
      }
      JAXBContext jc = JAXBContext.newInstance(Document.class);
      // JAXBContext jc = JAXBContext.newInstance(Document.class.getPackage()
      // .getName());
      Marshaller marshal = jc.createMarshaller();
      StringWriter sw = new StringWriter();
      marshal.marshal(docOut, sw);
      marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      log.debug("{}", sw);

      Unmarshaller unmarshal = jc.createUnmarshaller();
      Document docIn = (Document) unmarshal.unmarshal(new StringReader(sw.toString()));

      PropWriter.write(docIn, new File("target/test-output"), true);

      // TODO check output files against input
   }

   private InputStream getResourceAsStream(String relativeResourceName) throws FileNotFoundException
   {
      InputStream stream = PropReaderTests.class.getResourceAsStream(relativeResourceName);
      if (stream == null)
         throw new FileNotFoundException(relativeResourceName);
      return stream;
   }
}
