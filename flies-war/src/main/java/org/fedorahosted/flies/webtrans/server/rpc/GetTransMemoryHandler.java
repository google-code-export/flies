package org.fedorahosted.flies.webtrans.server.rpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.model.HTextFlow;
import org.fedorahosted.flies.model.HTextFlowTarget;
import org.fedorahosted.flies.search.DefaultNgramAnalyzer;
import org.fedorahosted.flies.search.LevenshteinUtil;
import org.fedorahosted.flies.security.FliesIdentity;
import org.fedorahosted.flies.util.ShortString;
import org.fedorahosted.flies.webtrans.server.ActionHandlerFor;
import org.fedorahosted.flies.webtrans.shared.model.TranslationMemoryItem;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemory;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemoryResult;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemory.SearchType;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("webtrans.gwt.GetTransMemoryHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(GetTranslationMemory.class)
public class GetTransMemoryHandler extends AbstractActionHandler<GetTranslationMemory, GetTranslationMemoryResult>
{

   private static final int MAX_RESULTS = 10;

   @Logger
   private Log log;

   @In
   private FullTextEntityManager entityManager;

   @Override
   public GetTranslationMemoryResult execute(GetTranslationMemory action, ExecutionContext context) throws ActionException
   {
      FliesIdentity.instance().checkLoggedIn();

      final String searchText = action.getQuery();
      ShortString abbrev = new ShortString(searchText);
      final SearchType searchType = action.getSearchType();
      log.info("Fetching TM matches({0}) for \"{1}\"", searchType, abbrev);

      LocaleId localeID = action.getLocaleId();
      ArrayList<TranslationMemoryItem> results;
      String queryText;
      switch (searchType)
      {
      case RAW:
         queryText = searchText;
         break;

      case FUZZY:
         // search by N-grams
         queryText = QueryParser.escape(searchText);
         break;

      case EXACT:
         queryText = "\"" + QueryParser.escape(searchText) + "\"";
         break;

      default:
         throw new RuntimeException("Unknown query type: " + searchType);
      }

      try
      {
         QueryParser parser = new QueryParser(Version.LUCENE_29, "content", new DefaultNgramAnalyzer());
         Query textQuery = parser.parse(queryText);
         FullTextQuery ftQuery = entityManager.createFullTextQuery(textQuery, HTextFlow.class);
         ftQuery.enableFullTextFilter("translated").setParameter("locale", localeID);
         ftQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
         List<Object[]> matches = ftQuery.setMaxResults(MAX_RESULTS).getResultList();
         Map<TMKey, TranslationMemoryItem> matchesMap = new LinkedHashMap<TMKey, TranslationMemoryItem>();
         for (Object[] match : matches)
         {
            float score = (Float) match[0];
            HTextFlow textFlow = (HTextFlow) match[1];
            if (textFlow == null)
            {
               continue;
            }
            HTextFlowTarget target = textFlow.getTargets().get(localeID);
            // double check in case of caching issues
            if (target.getState() != ContentState.Approved)
            {
               continue;
            }
            String textFlowContent = textFlow.getContent();
            String targetContent = target.getContent();

            int levDistance = LevenshteinUtil.getLevenshteinSubstringDistance(searchText, textFlowContent);
            int maxDistance = searchText.length();
            int percent = 100 * (maxDistance - levDistance) / maxDistance;

            TMKey key = new TMKey(textFlowContent, targetContent);
            TranslationMemoryItem item = matchesMap.get(key);
            if (item == null)
            {
               item = new TranslationMemoryItem(textFlowContent, targetContent, score, percent);
               matchesMap.put(key, item);
            }
            item.addTransUnitId(textFlow.getId());
         }
         results = new ArrayList<TranslationMemoryItem>(matchesMap.values());
      }
      catch (ParseException e)
      {
         if (searchType == SearchType.RAW)
         {
            log.warn("Can't parse raw query '" + queryText + "'");
         }
         else
         {
            // escaping failed!
            log.error("Can't parse query '" + queryText + "'", e);
         }
         results = new ArrayList<TranslationMemoryItem>(0);
      }

      /**
       * NB just because this Comparator returns 0 doesn't mean the matches are
       * identical.
       */
      Comparator<TranslationMemoryItem> comp = new Comparator<TranslationMemoryItem>()
      {

         @Override
         public int compare(TranslationMemoryItem m1, TranslationMemoryItem m2)
         {
            int result;
            result = compare(m1.getSimilarityPercent(), m2.getSimilarityPercent());
            if (result != 0)
               return -result;
            result = compare(m1.getSource().length(), m2.getSource().length());
            if (result != 0)
               return result; // shorter matches are preferred, if similarity is
                              // the same
            result = compare(m1.getRelevanceScore(), m2.getRelevanceScore());
            if (result != 0)
               return -result;
            return m1.getSource().compareTo(m2.getSource());
         }

         private int compare(int a, int b)
         {
            if (a < b)
               return -1;
            if (a > b)
               return 1;
            return 0;
         }

         private int compare(float a, float b)
         {
            if (a < b)
               return -1;
            if (a > b)
               return 1;
            return 0;
         }

      };

      Collections.sort(results, comp);

      log.info("Returning {0} TM matches for \"{1}\"", results.size(), abbrev);
      return new GetTranslationMemoryResult(results);
   }

   @Override
   public void rollback(GetTranslationMemory action, GetTranslationMemoryResult result, ExecutionContext context) throws ActionException
   {
   }

   static class TMKey
   {

      private final String textFlowContent;
      private final String targetContent;

      public TMKey(String textFlowContent, String targetContent)
      {
         this.textFlowContent = textFlowContent;
         this.targetContent = targetContent;
      }

      public String getTextFlowContent()
      {
         return textFlowContent;
      }

      public String getTargetContent()
      {
         return targetContent;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (obj instanceof TMKey)
         {
            TMKey o = (TMKey) obj;
            return equal(textFlowContent, o.textFlowContent) && equal(targetContent, o.targetContent);
         }
         return false;
      }

      private static boolean equal(String s1, String s2)
      {
         return s1 == null ? s2 == null : s1.equals(s2);
      }

      @Override
      public int hashCode()
      {
         int result = 1;
         result = 37 * result + textFlowContent != null ? textFlowContent.hashCode() : 0;
         result = 37 * result + targetContent != null ? targetContent.hashCode() : 0;
         return result;
      }

   }

}
