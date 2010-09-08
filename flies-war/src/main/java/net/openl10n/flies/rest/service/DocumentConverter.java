package net.openl10n.flies.rest.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.shared.ActionException;
import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.dao.TextFlowDAO;
import net.openl10n.flies.dao.TextFlowTargetDAO;
import net.openl10n.flies.exception.FliesException;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HLocale;
import net.openl10n.flies.model.HSimpleComment;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.model.HTextFlowHistory;
import net.openl10n.flies.model.HTextFlowTarget;
import net.openl10n.flies.model.po.HPoHeader;
import net.openl10n.flies.model.po.HPoTargetHeader;
import net.openl10n.flies.model.po.HPotEntryData;
import net.openl10n.flies.model.po.PoUtility;
import net.openl10n.flies.rest.MediaTypes;
import net.openl10n.flies.rest.dto.Link;
import net.openl10n.flies.rest.dto.deprecated.Document;
import net.openl10n.flies.rest.dto.deprecated.Relationships;
import net.openl10n.flies.rest.dto.deprecated.SimpleComment;
import net.openl10n.flies.rest.dto.deprecated.TextFlow;
import net.openl10n.flies.rest.dto.deprecated.TextFlowTarget;
import net.openl10n.flies.rest.dto.deprecated.TextFlowTargets;
import net.openl10n.flies.rest.dto.po.HeaderEntry;
import net.openl10n.flies.rest.dto.po.PoHeader;
import net.openl10n.flies.rest.dto.po.PoTargetHeader;
import net.openl10n.flies.rest.dto.po.PoTargetHeaders;
import net.openl10n.flies.rest.dto.po.PotEntryData;
import net.openl10n.flies.service.LocaleService;

import org.hibernate.Session;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name("documentConverter")
public class DocumentConverter
{

   @Logger
   private Log log;

   @In
   private TextFlowDAO textFlowDAO;
   @In
   private TextFlowTargetDAO textFlowTargetDAO;
   @In
   private Session session;
   @In
   private LocaleService localeServiceImpl;

   ClassValidator<HTextFlow> resValidator = new ClassValidator<HTextFlow>(HTextFlow.class);
   ClassValidator<HTextFlowTarget> tftValidator = new ClassValidator<HTextFlowTarget>(HTextFlowTarget.class);
   ClassValidator<HSimpleComment> commentValidator = new ClassValidator<HSimpleComment>(HSimpleComment.class);
   ClassValidator<HPotEntryData> potEntryValidator = new ClassValidator<HPotEntryData>(HPotEntryData.class);
   ClassValidator<HPoHeader> poValidator = new ClassValidator<HPoHeader>(HPoHeader.class);
   ClassValidator<HPoTargetHeader> poTargetValidator = new ClassValidator<HPoTargetHeader>(HPoTargetHeader.class);

   /**
    * Recursively copies from the source Document to the destination HDocument.
    * Increments toHDoc's revision number if any resources were changed
    * 
    * @param fromDoc source Document
    * @param toHDoc destination HDocument
    * @throws ActionException
    */
   public void copy(Document fromDoc, HDocument toHDoc) throws FliesException
   {
      log.debug("copy Document to HDocument");
      boolean docChanged = false;
      int nextDocRev = 1;
      if (!session.contains(toHDoc))
      {
         // new document
         docChanged = true;
         log.debug("CHANGED: Document {0} is new", toHDoc.getDocId());
      }
      else
      {
         nextDocRev = toHDoc.getRevision() + 1;
      }
      // changing these attributes probably shouldn't
      // invalidate existing translations, so we don't
      // bother incrementing the doc rev
      toHDoc.setDocId(fromDoc.getId());
      toHDoc.setName(fromDoc.getName());
      toHDoc.setPath(fromDoc.getPath());
      toHDoc.setContentType(fromDoc.getContentType());
      toHDoc.setLocale(localeServiceImpl.getSupportedLanguageByLocale(fromDoc.getLang()));
      // toHDoc.setProject(container); // this must be done by the caller

      // don't copy revision; we don't accept revision from the client
      List<TextFlow> fromDocResources = fromDoc.getTextFlows();

      List<HTextFlow> hResources;
      Map<String, HTextFlow> oldResourceMap = new HashMap<String, HTextFlow>();
      List<HTextFlow> oldResources = toHDoc.getTextFlows();
      for (HTextFlow oldResource : oldResources)
      {
         oldResourceMap.put(oldResource.getResId(), oldResource);
      }
      // We create an empty list for HDocument.resources, and build it up
      // in the order of fromDoc's resources. This ensures that we preserve
      // the order of the list.
      hResources = new ArrayList<HTextFlow>(fromDocResources.size());
      for (TextFlow fromRes : fromDocResources)
      {
         HTextFlow hRes = null;
         if (session.contains(toHDoc))
         {
            // document already exists, see if the resource does too
            log.debug("get HDocument from database");
            hRes = textFlowDAO.getById(toHDoc, fromRes.getId());
         }
         boolean resChanged = false;
         if (hRes == null)
         {
            resChanged = true; // this will cause res.revision to be set
            // below
            log.debug("add HTextFlow");
            hRes = toHDoc.create(fromRes, nextDocRev);
         }
         else
         {
            hRes.setObsolete(false);
         }
         hResources.add(hRes);
         log.debug("add document");
         // session.save(hRes);

         resChanged |= copy(fromRes, hRes, nextDocRev);
         if (resChanged)
         {
            hRes.setRevision(nextDocRev);
            docChanged = true;
         }
         if (oldResourceMap.remove(fromRes.getId()) == null)
         {
            docChanged = true;
            log.debug("CHANGED: Resource {0}:{1} was added", toHDoc.getDocId(), hRes.getResId());
         }

         InvalidValue[] invalidValues = resValidator.getInvalidValues(hRes);
         if (invalidValues.length != 0)
         {
            String message = "TextFlow with content '" + hRes.getContent() + "' is invalid: " + Arrays.asList(invalidValues);
            log.error(message);
         }
      }
      if (fromDoc.hasExtensions())
         for (Object ext : fromDoc.getExtensions())
         {
            if (ext instanceof PoHeader)
            {
               PoHeader fromHeader = (PoHeader) ext;
               HPoHeader toHeader = toHDoc.getPoHeader();
               if (toHeader == null)
               {
                  toHeader = new HPoHeader();
                  toHDoc.setPoHeader(toHeader);
                  // toHPoHeader.setDocument(toHDoc);
                  docChanged = true;
               }
               HSimpleComment toComment = toHeader.getComment();
               if (toComment == null)
               {
                  toComment = new HSimpleComment();
                  toHeader.setComment(toComment);
                  docChanged = true;
               }
               String fromComment = fromHeader.getComment().getValue();
               if (!equal(fromComment, toComment.getComment()))
               {
                  toComment.setComment(fromComment);
                  docChanged = true;
               }
               String fromEntries = PoUtility.listToHeader(fromHeader.getEntries());
               if (!equal(toHeader.getEntries(), fromEntries))
               {
                  toHeader.setEntries(fromEntries);
                  docChanged = true;
               }
               InvalidValue[] invalidValues = poValidator.getInvalidValues(toHeader);
               if (invalidValues.length != 0)
               {
                  String message = "PO header for document '" + toHeader.getDocument().getDocId() + "' is invalid: " + Arrays.asList(invalidValues);
                  log.error(message);
               }
            }
            else if (ext instanceof PoTargetHeaders)
            {
               PoTargetHeaders fromHeaders = (PoTargetHeaders) ext;
               Map<HLocale, HPoTargetHeader> toHeaders = toHDoc.getPoTargetHeaders();
               for (PoTargetHeader fromHeader : fromHeaders.getHeaders())
               {
                  // List<HeaderEntry> fromEntries =
                  // fromHeader.getEntries();
                  LocaleId localeId = fromHeader.getTargetLanguage();
                  HLocale hLocale = localeServiceImpl.getSupportedLanguageByLocale(localeId);
                  HPoTargetHeader toHeader = toHeaders.get(hLocale);
                  if (toHeader == null)
                  {
                     toHeader = new HPoTargetHeader();
                     toHeader.setDocument(toHDoc);
                     toHeader.setTargetLanguage(hLocale);
                     toHeaders.put(hLocale, toHeader);
                     docChanged = true;
                  }
                  HSimpleComment toComment = toHeader.getComment();
                  if (toComment == null)
                  {
                     toComment = new HSimpleComment();
                     toHeader.setComment(toComment);
                     docChanged = true;
                  }
                  String fromComment = fromHeader.getComment().getValue();
                  if (!equal(fromComment, toComment.getComment()))
                  {
                     toComment.setComment(fromComment);
                     docChanged = true;
                  }
                  String fromEntries = PoUtility.listToHeader(fromHeader.getEntries());
                  if (!equal(toHeader.getEntries(), fromEntries))
                  {
                     toHeader.setEntries(fromEntries);
                     docChanged = true;
                  }
                  InvalidValue[] invalidValues = poTargetValidator.getInvalidValues(toHeader);
                  if (invalidValues.length != 0)
                  {
                     String message = "PO target header for document '" + toHeader.getDocument().getDocId() + "' is invalid: " + Arrays.asList(invalidValues);
                     log.error(message);
                  }
               }
            }
            else
            {
               throw new RuntimeException("Unknown Document extension " + ext.getClass() + " - please ensure your client is up to date");
            }
         }

      // even if we just move around resources without changing them,
      // the document is considered changed
      if (!oldResources.equals(hResources))
      {
         // mark any removed resources as obsolete
         for (HTextFlow oldResource : oldResourceMap.values())
         {
            oldResource.setObsolete(true);
            log.debug("CHANGED: Resource {0}:{1} was removed", toHDoc.getDocId(), oldResource.getResId());
         }
         toHDoc.setTextFlows(hResources);
         docChanged = true;
      }
      if (docChanged)
         toHDoc.setRevision(nextDocRev);
   }

   private static boolean equal(String a, String b)
   {
      return a == null ? b == null : a.equals(b);
   }

   /**
    * Returns true if the content (or a comment) of htf was changed
    * 
    * @throws ActionException
    */
   private boolean copy(TextFlow fromTf, HTextFlow htf, int nextDocRev) throws FliesException
   {
      log.debug("copy TextFlow to HTextFlow");
      boolean changed = false;
      if (!fromTf.getContent().equals(htf.getContent()))
      {
         changed = true;
         log.debug("CHANGED: TextFlow {0}:{1} content changed", htf.getDocument().getDocId(), htf.getResId());

         // save old version to history
         HTextFlowHistory history = new HTextFlowHistory(htf);
         htf.getHistory().put(htf.getRevision(), history);

         // make sure to set the status of any targets to NeedReview
         for (HTextFlowTarget target : htf.getTargets().values())
         {
            // TODO not sure if this is the correct state
            target.setState(ContentState.NeedReview);
         }

         htf.setRevision(nextDocRev);
         htf.setContent(fromTf.getContent());
      }

      htf.setContent(fromTf.getContent());
      for (Object ext : fromTf.getExtensions())
      {
         if (ext instanceof PotEntryData)
         {
            PotEntryData potEntryData = (PotEntryData) ext;
            HPotEntryData hPotEntryData = htf.getPotEntryData();
            if (hPotEntryData == null)
            {
               hPotEntryData = new HPotEntryData();
               // hPotEntryData.setTextFlow(htf);
               htf.setPotEntryData(hPotEntryData);
            }
            changed |= copy(potEntryData, hPotEntryData);
            InvalidValue[] invalidValues = potEntryValidator.getInvalidValues(hPotEntryData);
            if (invalidValues.length != 0)
            {
               String message = "POT entry for TextFlow with id '" + htf.getResId() + "' is invalid: " + Arrays.asList(invalidValues);
               log.error(message);
            }
         }
         else if (ext instanceof TextFlowTargets)
         {
            // do nothing here, we want to do targets last:
            // if the comment changes, the resourceRev will have to be
            // incremented
         }
         else if (ext instanceof SimpleComment)
         {
            SimpleComment simpleComment = (SimpleComment) ext;
            HSimpleComment hComment = htf.getComment();
            if (hComment == null)
            {
               changed = true;
               log.debug("CHANGED: TextFlow {0}:{1} comment changed", htf.getDocument().getDocId(), htf.getResId());
               // NB HTextFlowHistory doesn't record comments
               hComment = new HSimpleComment();
               htf.setComment(hComment);
            }
            else
            {
               if (!hComment.getComment().equals(simpleComment.getValue()))
                  changed = true;
            }
            hComment.setComment(simpleComment.getValue());
            InvalidValue[] invalidValues = commentValidator.getInvalidValues(hComment);
            if (invalidValues.length != 0)
            {
               String message = "Comment for TextFlow with id '" + htf.getResId() + "' is invalid: " + Arrays.asList(invalidValues);
               log.error(message);
            }
         }
         else
         {
            throw new RuntimeException("Unknown TextFlow extension " + ext.getClass());
         }
      }
      TextFlowTargets targets = fromTf.getTargets();

      if (targets != null)
      {
         for (TextFlowTarget target : targets.getTargets())
         {
            HTextFlowTarget hTarget = null;
            HLocale hLocale = localeServiceImpl.getSupportedLanguageByLocale(target.getLang());
            if (session.contains(htf))
            {
               hTarget = textFlowTargetDAO.getByNaturalId(htf, hLocale);
            }
            if (hTarget == null)
            {
               hTarget = new HTextFlowTarget();
               hTarget.setLocale(hLocale);
               log.debug("set locale:" + hTarget.getLocale().getLocaleId());

               hTarget.setTextFlow(htf);
               Integer tfRev;

               if (changed || htf.getRevision() == null)
                  tfRev = nextDocRev;
               else
                  tfRev = htf.getRevision();

               hTarget.setState(target.getState());
               hTarget.setContent(target.getContent());
               log.debug("set TextFlowTarget:" + hTarget.getContent());
               copy(target, hTarget, htf);
               hTarget.setTextFlowRevision(tfRev);
            }
            else
            {
               copy(target, hTarget, htf);
            }
            htf.getTargets().put(hTarget.getLocale(), hTarget);
            log.debug("check HTextFlowTarget:" + htf.getTargets().get(hTarget.getLocale()).getContent());
            InvalidValue[] invalidValues = tftValidator.getInvalidValues(hTarget);
            if (invalidValues.length != 0)
            {
               String message = "TextFlowTarget with id '" + hTarget.getTextFlow().getResId() + "' is invalid: " + Arrays.asList(invalidValues);
               log.error(message);
            }
         }
      }
      return changed;
   }

   private boolean copy(PotEntryData fromPotEntryData, HPotEntryData toHPotEntryData)
   {
      boolean changed = false;
      toHPotEntryData.setContext(fromPotEntryData.getContext());
      SimpleComment fromExtractedSimpleComment = fromPotEntryData.getExtractedComment();
      String fromExtractedComment = fromExtractedSimpleComment == null ? null : fromExtractedSimpleComment.getValue();

      HSimpleComment toExtractedComment = toHPotEntryData.getExtractedComment();
      if (fromExtractedComment != null && toExtractedComment == null)
      {
         toExtractedComment = new HSimpleComment();
         toHPotEntryData.setExtractedComment(toExtractedComment);
      }
      if (fromExtractedComment == null)
      {
         if (toExtractedComment != null)
         {
            changed = true;
            toHPotEntryData.setExtractedComment(null);
         }
      }
      else
      {
         changed |= !fromExtractedComment.equals(toExtractedComment.getComment());
         toExtractedComment.setComment(fromExtractedComment);
      }
      String flags = PoUtility.concatFlags(fromPotEntryData.getFlags());
      changed |= !flags.equals(toHPotEntryData.getFlags());
      toHPotEntryData.setFlags(flags);
      String references = PoUtility.concatRefs(fromPotEntryData.getReferences());
      changed |= !references.equals(toHPotEntryData.getReferences());
      toHPotEntryData.setReferences(references);
      return changed;
   }


   private void copy(TextFlowTarget target, HTextFlowTarget hTarget, HTextFlow htf) throws FliesException
   {
      log.debug("copy textflowtarget to HtextFlowTarget");
      boolean changed = false;
      changed |= !target.getContent().equals(hTarget.getContent());
      hTarget.setContent(target.getContent());
      changed |= !target.getLang().equals(hTarget.getLocale());
      hTarget.setLocale(localeServiceImpl.getSupportedLanguageByLocale(target.getLang()));
      hTarget.setTextFlowRevision(htf.getRevision());
      changed |= !target.getState().equals(hTarget.getState());
      hTarget.setState(target.getState());
      hTarget.setTextFlow(htf);
      if (target.hasComment())
      {
         HSimpleComment hComment = hTarget.getComment();
         if (hComment == null)
         {
            hComment = new HSimpleComment();
            hTarget.setComment(hComment);
         }
         changed |= !target.getComment().equals(hComment.getComment());
         hComment.setComment(target.getComment().getValue());
      }
      else
      {
         changed |= (hTarget.getComment() != null);
      }
   }

   public void addLinks(Document doc, URI docUri, URI iterationUri)
   {
      // add self relation
      Link link = new Link(docUri, Relationships.SELF);
      doc.getLinks().add(link);

      // add container relation
      link = new Link(iterationUri, Relationships.DOCUMENT_CONTAINER, MediaTypes.APPLICATION_FLIES_PROJECT_ITERATION_XML);
      doc.getLinks().add(link);
   }

   public TextFlow copy(HTextFlow htf)
   {
      log.debug("copy HTextFlow to TextFlow");
      TextFlow textFlow = new TextFlow(htf.getResId());
      HSimpleComment comment = htf.getComment();
      if (comment != null)
      {
         textFlow.getOrAddComment().setValue(comment.getComment());
      }
      log.debug("get textflow:" + htf.getContent());
      textFlow.setContent(htf.getContent());
      textFlow.setLang(htf.getDocument().getLocale().getLocaleId());
      textFlow.setRevision(htf.getRevision());

      for (HLocale locale : htf.getTargets().keySet())
      {
         log.debug("get textflowtarget locale:" + locale.getLocaleId().getId());
         log.debug(textFlowDAO.toString());
         HTextFlowTarget hTextFlowTarget = textFlowTargetDAO.getByNaturalId(htf, locale);
         if (hTextFlowTarget != null)
         {
            TextFlowTarget textFlowTarget = new TextFlowTarget(textFlow, locale.getLocaleId());
            HSimpleComment tftComment = hTextFlowTarget.getComment();
            if (tftComment != null)
            {
               textFlowTarget.getOrAddComment().setValue(tftComment.getComment());
            }
            log.debug("set textflowtarget:" + hTextFlowTarget.getContent());
            textFlowTarget.setContent(hTextFlowTarget.getContent());
            textFlowTarget.setResourceRevision(hTextFlowTarget.getTextFlowRevision());
            textFlowTarget.setState(hTextFlowTarget.getState());
            textFlow.addTarget(textFlowTarget);
         }
      }

      HPotEntryData fromHPotEntryData = htf.getPotEntryData();
      if (fromHPotEntryData != null)
      {
         PotEntryData toPotEntryData = textFlow.getOrAddExtension(PotEntryData.class);
         toPotEntryData.setId(htf.getResId());
         toPotEntryData.setContext(fromHPotEntryData.getContext());
         HSimpleComment extractedComment = fromHPotEntryData.getExtractedComment();
         toPotEntryData.setExtractedComment(HSimpleComment.toSimpleComment(extractedComment));
         List<String> toFlags = toPotEntryData.getFlags();
         toFlags.addAll(PoUtility.splitFlags(fromHPotEntryData.getFlags()));
         List<String> toReferences = toPotEntryData.getReferences();
         toReferences.addAll(PoUtility.splitRefs(fromHPotEntryData.getReferences()));
      }

      return textFlow;
   }

   public Document copyDocument(HDocument hDoc, int levels)
   {
      log.debug("copy hdocument to document");
      Document doc = new Document(hDoc.getDocId(), hDoc.getName(), hDoc.getPath(), hDoc.getContentType(), hDoc.getRevision(), hDoc.getLocale().getLocaleId());
      if (levels != 0)
      {
         List<TextFlow> docResources = doc.getTextFlows();
         for (HTextFlow hRes : hDoc.getTextFlows())
         {

            docResources.add(copy(hRes));
         }
         HPoHeader fromPoHeader = hDoc.getPoHeader();
         if (fromPoHeader != null)
         {
            PoHeader toPoHeader = doc.getOrAddExtension(PoHeader.class);
            String fromComment = fromPoHeader.getComment() != null ? fromPoHeader.getComment().getComment() : null;
            toPoHeader.setComment(fromComment);
            List<HeaderEntry> toEntries = toPoHeader.getEntries();
            toEntries.addAll(PoUtility.headerToList(fromPoHeader.getEntries()));
         }
         Collection<HPoTargetHeader> fromTargetHeaders = hDoc.getPoTargetHeaders().values();
         if (!fromTargetHeaders.isEmpty())
         {
            PoTargetHeaders toTargetHeaders = doc.getOrAddExtension(PoTargetHeaders.class);
            for (HPoTargetHeader fromHeader : fromTargetHeaders)
            {
               PoTargetHeader toHeader = new PoTargetHeader();
               String fromComment = fromHeader.getComment() != null ? fromHeader.getComment().getComment() : null;
               toHeader.setComment(fromComment);
               List<HeaderEntry> toEntries = toHeader.getEntries();
               toEntries.addAll(PoUtility.headerToList(fromHeader.getEntries()));
               toHeader.setTargetLanguage(fromHeader.getTargetLanguage().getLocaleId());
               toTargetHeaders.getHeaders().add(toHeader);
            }
         }
      }
      return doc;
   }

   public Document copyDocument(HDocument hdoc, boolean deep)
   {
      if (deep)
         return copyDocument(hdoc, Integer.MAX_VALUE);
      else
         return copyDocument(hdoc, 0);
   }

}
