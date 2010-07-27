package org.fedorahosted.flies.adapter.po;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.resources.LocaleInputSourcePair;
import org.fedorahosted.flies.rest.dto.deprecated.Document;
import org.fedorahosted.flies.rest.dto.deprecated.SimpleComment;
import org.fedorahosted.flies.rest.dto.deprecated.TextFlow;
import org.fedorahosted.flies.rest.dto.deprecated.TextFlowTarget;
import org.fedorahosted.flies.rest.dto.po.HeaderEntry;
import org.fedorahosted.flies.rest.dto.po.PoHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeaders;
import org.fedorahosted.flies.rest.dto.po.PotEntryData;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.MessageStreamParser;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableSet;

public class PoReader
{

   public static final ContentType PO_CONTENT_TYPE = new ContentType("application/x-gettext");

   public static final LocaleId PO_SOURCE_LANGUAGE = LocaleId.EN_US;

   public static final ImmutableSet<String> POT_HEADER_FIELDS = ImmutableSet.of(HeaderFields.KEY_ProjectIdVersion, HeaderFields.KEY_ReportMsgidBugsTo, HeaderFields.KEY_PotCreationDate, HeaderFields.KEY_MimeVersion, HeaderFields.KEY_ContentType, HeaderFields.KEY_ContentTransferEncoding);

   public static final ImmutableSet<String> PO_HEADER_FIELDS = ImmutableSet.of(HeaderFields.KEY_PoRevisionDate, HeaderFields.KEY_LastTranslator, HeaderFields.KEY_LanguageTeam, HeaderFields.KEY_Language, "Plural-Forms", "X-Generator");

   public PoReader()
   {
   }

   public void extractTarget(Document document, InputSource inputSource, LocaleId targetLocaleId)
   {
      MessageStreamParser messageParser = createParser(inputSource);

      List<TextFlow> resources = document.getTextFlows();
      Map<String, TextFlow> resourceMap = new HashMap<String, TextFlow>(resources.size());
      for (TextFlow res : resources)
      {
         resourceMap.put(res.getContent(), res);
      }

      while (messageParser.hasNext())
      {
         Message message = messageParser.next();

         if (message.isHeader())
         {
            // add target header data
            PoTargetHeader poHeader = new PoTargetHeader();
            extractPoHeader(message, poHeader);
            poHeader.setTargetLanguage(targetLocaleId);
            PoTargetHeaders targetHeaders = document.getOrAddExtension(PoTargetHeaders.class);
            targetHeaders.getHeaders().add(poHeader);
         }
         else if (message.isObsolete())
         {
            // TODO append obsolete
         }
         else if (message.isPlural())
         {
            // TODO skip for now
         }
         else
         {
            TextFlow tf = (TextFlow) resourceMap.get(message.getMsgid());
            if (tf != null)
            {
               String id = createId(message);

               matchIdOrFail(tf.getId(), id);

               // add the target content (msgstr)
               TextFlowTarget tfTarget = new TextFlowTarget(tf, targetLocaleId);
               tfTarget.setContent(message.getMsgstr());
               tfTarget.setState(getContentState(message));
               tf.addTarget(tfTarget);

               // add the PO comment
               tfTarget.getExtensions().add(new SimpleComment(StringUtils.join(message.getComments(), "\n")));
            }
            else
            {
               // TODO append obsolete

            }
         }
      }

   }

   public void extractTarget(Document document, LocaleInputSourcePair localeInputSourcePair)
   {
      extractTarget(document, localeInputSourcePair.getInputSource(), localeInputSourcePair.getLocaleId());
   }

   public void extractTargets(Document documentPart, LocaleInputSourcePair... localeInputSourcePairs)
   {
      // TODO: parsing in parallel might be faster than one-by-one.
      for (LocaleInputSourcePair localeInputSourcePair : localeInputSourcePairs)
      {
         extractTarget(documentPart, localeInputSourcePair);
      }
   }

   private static void extractPotHeader(Message message, PoHeader potHeader)
   {
      potHeader.getComment().setValue(StringUtils.join(message.getComments(), "\n"));

      HeaderFields hf = HeaderFields.wrap(message);
      for (String key : hf.getKeys())
      {
         String val = hf.getValue(key);
         if (POT_HEADER_FIELDS.contains(key))
         {
            potHeader.getEntries().add(new HeaderEntry(key, val));
         }
         // we add any custom fields to the PO only, not the POT
      }
   }

   private static void extractPoHeader(Message message, PoHeader poHeader)
   {
      poHeader.getComment().setValue(StringUtils.join(message.getComments(), "\n"));

      HeaderFields hf = HeaderFields.wrap(message);
      for (String key : hf.getKeys())
      {
         String val = hf.getValue(key);
         if (PO_HEADER_FIELDS.contains(key))
         {
            poHeader.getEntries().add(new HeaderEntry(key, val));
         }
         else if (!POT_HEADER_FIELDS.contains(key))
         {
            // we add any custom fields to the PO only, not the POT
            // TODO this should be configurable
            poHeader.getEntries().add(new HeaderEntry(key, val));
         }
      }
   }

   public void extractTemplate(Document document, InputSource inputSource, LocaleId sourceLocaleId)
   {
      MessageStreamParser messageParser = createParser(inputSource);

      document.setLang(sourceLocaleId);
      document.setContentType(PO_CONTENT_TYPE);
      List<TextFlow> resources = document.getTextFlows();

      boolean headerFound = false;
      while (messageParser.hasNext())
      {
         Message message = messageParser.next();

         if (message.isHeader())
         {
            if (headerFound)
               throw new IllegalStateException("found a second header!");
            headerFound = true;

            // store POT data
            PoHeader potHeader = new PoHeader();
            extractPotHeader(message, potHeader);
            document.getExtensions(true).add(potHeader);

         }
         else if (message.isObsolete())
         {
            // TODO append obsolete
         }
         else if (message.isPlural())
         {
            // TODO skip for now
         }
         else
         {
            String id = createId(message);
            // add the content (msgid)
            TextFlow tf = new TextFlow(id, sourceLocaleId);
            tf.setContent(message.getMsgid());
            resources.add(tf);

            // add the entry header POT fields
            tf.getExtensions().add(createFromMessage(id, message));
         }

      }
   }

   private static PotEntryData createFromMessage(String id, Message message)
   {
      PotEntryData data = new PotEntryData(id);
      if (message.getMsgctxt() != null)
      {
         data.setContext(message.getMsgctxt());
      }
      data.getExtractedComment().setValue(StringUtils.join(message.getExtractedComments(), "\n"));
      data.getFlags().addAll(message.getFormats());
      data.getReferences().addAll(message.getSourceReferences());
      return data;
   }

   public void extractTemplate(Document document, InputSource inputSource)
   {
      extractTemplate(document, inputSource, PO_SOURCE_LANGUAGE);
   }

   public void extractTemplate(Document document, LocaleInputSourcePair localeInputSourcePair)
   {
      extractTemplate(document, localeInputSourcePair.getInputSource(), localeInputSourcePair.getLocaleId());
   }

   public void extract(Document document, InputSource inputSource, LocaleId targetLocaleId, LocaleId sourceLocaleId)
   {
      MessageStreamParser messageParser = createParser(inputSource);

      document.setLang(sourceLocaleId);
      document.setContentType(PO_CONTENT_TYPE);

      List<TextFlow> resources = document.getTextFlows();

      boolean headerFound = false;
      while (messageParser.hasNext())
      {
         Message message = messageParser.next();

         if (message.isHeader())
         {
            if (headerFound)
               throw new IllegalStateException("found a second header!");
            headerFound = true;

            // store POT data
            PoHeader potHeader = new PoHeader();
            extractPotHeader(message, potHeader);
            document.getExtensions(true).add(potHeader);

            // store PO data
            PoHeader poHeader = new PoHeader();
            extractPoHeader(message, poHeader);
            document.getExtensions(true).add(poHeader);

         }
         else if (message.isObsolete())
         {
            // append obsolete
         }
         else if (message.isPlural())
         {
            // skip for now
         }
         else
         {
            String id = createId(message);
            // add the content (msgid)
            TextFlow tf = new TextFlow(id, sourceLocaleId);
            tf.setContent(message.getMsgid());
            resources.add(tf);

            // add the entry header POT fields
            tf.getExtensions().add(createFromMessage(id, message));

            // add the target content (msgstr)
            TextFlowTarget tfTarget = new TextFlowTarget(tf, targetLocaleId);
            tfTarget.setContent(message.getMsgstr());
            tfTarget.setState(getContentState(message));
            tf.addTarget(tfTarget);

            // add the PO comment
            tfTarget.getExtensions().add(new SimpleComment(StringUtils.join(message.getComments(), "\n")));

         }

      }

   }

   public void extract(Document document, InputSource inputSource, LocaleId targetLocaleId)
   {
      extract(document, inputSource, targetLocaleId, PO_SOURCE_LANGUAGE);
   }

   public void extract(Document document, LocaleInputSourcePair localeInputSourcePair)
   {
      extract(document, localeInputSourcePair.getInputSource(), localeInputSourcePair.getLocaleId());
   }

   public void extract(Document document, LocaleInputSourcePair localeInputSourcePair, LocaleId sourceLocaleId)
   {
      extract(document, localeInputSourcePair.getInputSource(), localeInputSourcePair.getLocaleId(), sourceLocaleId);
   }

   private static MessageStreamParser createParser(InputSource inputSource)
   {
      MessageStreamParser messageParser;
      if (inputSource.getCharacterStream() != null)
         messageParser = new MessageStreamParser(inputSource.getCharacterStream());
      else if (inputSource.getByteStream() != null)
      {
         if (inputSource.getEncoding() != null)
            messageParser = new MessageStreamParser(inputSource.getByteStream(), Charset.forName(inputSource.getEncoding()));
         else
            messageParser = new MessageStreamParser(inputSource.getByteStream(), Charset.forName("UTF-8"));
      }
      else if (inputSource.getSystemId() != null)
      {
         try
         {
            URL url = new URL(inputSource.getSystemId());

            if (inputSource.getEncoding() != null)
               messageParser = new MessageStreamParser(url.openStream(), Charset.forName(inputSource.getEncoding()));
            else
               messageParser = new MessageStreamParser(url.openStream(), Charset.forName("UTF-8"));
         }
         catch (IOException e)
         {
            // TODO throw stronger typed exception
            throw new RuntimeException("failed to get input from url in inputSource", e);
         }
      }
      else
         // TODO throw stronger typed exception
         throw new RuntimeException("not a valid inputSource");

      return messageParser;
   }

   private static ContentState getContentState(Message message)
   {
      if (message.getMsgstr() == null || message.getMsgstr().isEmpty())
         return ContentState.New;
      else if (message.isFuzzy())
         return ContentState.NeedReview;
      else
         return ContentState.Approved;
   }

   private static String createId(Message message)
   {
      String hashBase = message.getMsgctxt() == null ? message.getMsgid() : message.getMsgctxt() + "\u0000" + message.getMsgid();
      return generateHash(hashBase);
   }

   public static String generateHash(String key)
   {
      try
      {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         md5.reset();
         return new String(Hex.encodeHex(md5.digest(key.getBytes("UTF-8"))));
      }
      catch (Exception exc)
      {
         throw new RuntimeException(exc);
      }
   }

   private static void matchIdOrFail(String id, String id2)
   {
      if (!id.equals(id2))
      {
         throw new RuntimeException("id matching failed!");
      }
   }

}
