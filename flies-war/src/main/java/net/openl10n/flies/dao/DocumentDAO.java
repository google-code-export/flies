package net.openl10n.flies.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.common.TransUnitCount;
import net.openl10n.flies.common.TransUnitWords;
import net.openl10n.flies.common.TranslationStats;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HProjectIteration;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.model.StatusCount;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("documentDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class DocumentDAO extends AbstractDAOImpl<HDocument, Long>
{

   public DocumentDAO()
   {
      super(HDocument.class);
   }

   public DocumentDAO(Session session)
   {
      super(HDocument.class, session);
   }

   public HDocument getByDocId(HProjectIteration iteration, String id)
   {
      return (HDocument) getSession().createCriteria(HDocument.class).add(Restrictions.naturalId().set("docId", id).set("projectIteration", iteration)).setCacheable(true).setComment("DocumentDAO.getById").uniqueResult();
   }

   public Set<LocaleId> getTargetLocales(HDocument hDoc)
   {
      @SuppressWarnings("unchecked")
      List<LocaleId> locales = getSession().createQuery("select tft.locale from HTextFlowTarget tft where tft.textFlow.document = :document").setParameter("document", hDoc).list();
      return new HashSet<LocaleId>(locales);
   }

   /**
    * @see ProjectIterationDAO#getStatisticsForContainer(Long, LocaleId)
    * @param docId
    * @param localeId
    * @return
    */
   public TranslationStats getStatistics(long docId, LocaleId localeId)
   {
      Session session = getSession();

      // calculate unit counts
      @SuppressWarnings("unchecked")
      List<StatusCount> stats = session.createQuery("select new net.openl10n.flies.model.StatusCount(tft.state, count(tft)) " + "from HTextFlowTarget tft " + "where tft.textFlow.document.id = :id " + "  and tft.locale.localeId = :locale " + "  and tft.textFlow.obsolete = :obsolete " + "group by tft.state").setParameter("id", docId).setParameter("obsolete", false).setParameter("locale", localeId).setCacheable(true).list();
      Long totalCount = (Long) session.createQuery("select count(tf) from HTextFlow tf where tf.document.id = :id and tf.obsolete = :obsolete").setParameter("id", docId).setParameter("obsolete", false).setCacheable(true).uniqueResult();

      TransUnitCount stat = new TransUnitCount();
      for (StatusCount count : stats)
      {
         stat.set(count.status, count.count.intValue());
      }
      int newCount = totalCount.intValue() - stat.get(ContentState.Approved) - stat.get(ContentState.NeedReview);
      stat.set(ContentState.New, newCount);

      // calculate word counts
      @SuppressWarnings("unchecked")
      List<StatusCount> wordStats = session.createQuery("select new net.openl10n.flies.model.StatusCount(tft.state, sum(tft.textFlow.wordCount)) " + "from HTextFlowTarget tft where tft.textFlow.document.id = :id " + "  and tft.locale.localeId = :locale " + "  and tft.textFlow.obsolete = :obsolete " + "group by tft.state").setParameter("id", docId).setParameter("obsolete", false).setParameter("locale", localeId).list();
      Long totalWordCount = (Long) session.createQuery("select sum(tf.wordCount) from HTextFlow tf where tf.document.id = :id and tf.obsolete = :obsolete").setParameter("id", docId).setParameter("obsolete", false).uniqueResult();

      TransUnitWords wordCount = new TransUnitWords();
      for (StatusCount count : wordStats)
      {
         wordCount.set(count.status, count.count.intValue());
      }
      long newWordCount = totalWordCount.longValue() - wordCount.get(ContentState.Approved) - wordCount.get(ContentState.NeedReview);
      wordCount.set(ContentState.New, (int) newWordCount);

      TranslationStats transStats = new TranslationStats(stat, wordCount);
      return transStats;
   }

   public void syncRevisions(HDocument doc, HTextFlow... textFlows)
   {
      int rev = doc.getRevision();
      syncRevisions(doc, rev, textFlows);
   }

   public void syncRevisions(HDocument doc, int revision, HTextFlow... textFlows)
   {
      doc.setRevision(revision);
      for (HTextFlow textFlow : textFlows)
      {
         textFlow.setRevision(revision);
      }
   }

}
