package org.fedorahosted.flies.webtrans.gwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.gwt.model.TransMemory;
import org.fedorahosted.flies.gwt.rpc.GetTranslationMemory;
import org.fedorahosted.flies.gwt.rpc.GetTranslationMemoryResult;
import org.fedorahosted.flies.repository.model.HTextFlow;
import org.fedorahosted.flies.repository.model.HTextFlowTarget;
import org.fedorahosted.flies.security.FliesIdentity;
import org.hibernate.Session;
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
public class GetTransMemoryHandler implements ActionHandler<GetTranslationMemory, GetTranslationMemoryResult> {

	private static final int MAX_RESULTS = 50;
	private static final String LIKE_ESCAPE = "~";
	private static final char LUCENE_ESCAPE = '\\';
	// list of special chars taken from
	// http://lucene.apache.org/java/2_4_1/queryparsersyntax.html#Escaping%20Special%20Characters
	private static final List<String> LUCENE_SPECIAL = 
		Arrays.asList("+ - && || ! ( ) { } [ ] ^ \" ~ * ? : \\".split(" "));

	@Logger 
	private Log log;
	
	@In Session session;
    
	@In
	private FullTextEntityManager entityManager;
    
	@Override
	public GetTranslationMemoryResult execute(GetTranslationMemory action,
			ExecutionContext context) throws ActionException {
		FliesIdentity.instance().checkLoggedIn();
		
		final String searchText = action.getQuery();
		log.info("Fetching {0} TM matches for \"{1}\"", 
				action.isFuzzySearch() ? "fuzzy" : "exact", 
				searchText);
		
		LocaleId localeID = action.getLocaleId();
		if (action.isFuzzySearch()) {
			String luceneQuery = toLuceneQuery(searchText);
			List<HTextFlow> matches = findMatchingTextFlows(luceneQuery);
			ArrayList<TransMemory> results = new ArrayList<TransMemory>(matches.size());
			for (HTextFlow match : matches) {
				Map<LocaleId, HTextFlowTarget> matchTargets = match.getTargets();
				HTextFlowTarget target = matchTargets.get(localeID);
				if (target != null) {
					TransMemory mem = new TransMemory(
							match.getContent(), 
							target.getContent(), 
							match.getDocument().getDocId());
					results.add(mem);
				}
			}
			return new GetTranslationMemoryResult(results);
		} else {
			
			// TODO this should probably use Hibernate Search for efficiency
			// TODO filter by status Approved and by locale
			final String hqlQuery = toHQLQuery(searchText);
			org.hibernate.Query query = session.createQuery(
					"from HTextFlow tf where lower(tf.content) like :q escape '"+LIKE_ESCAPE+"'")
					.setParameter("q", hqlQuery);
			
			
			List<HTextFlow> textFlows = query 
					.setMaxResults(MAX_RESULTS)
					.list();
			int size = textFlows.size();
			
			ArrayList<TransMemory> results = new ArrayList<TransMemory>(size);
			
			for(HTextFlow textFlow : textFlows) {
				HTextFlowTarget target = textFlow.getTargets().get(localeID);
				if(target != null) {
					TransMemory memory = new TransMemory(
							textFlow.getContent(), 
							target.getContent(),
							textFlow.getDocument().getDocId());
					results.add(memory);
				}
			}
			return new GetTranslationMemoryResult(results);
		}		
	}

	static String toHQLQuery(String substring) {
		return "%"+
			substring.toLowerCase()
			.replace(LIKE_ESCAPE, LIKE_ESCAPE+LIKE_ESCAPE)
			.replace("%", LIKE_ESCAPE+"%")
			.replace("_", LIKE_ESCAPE+"_")
				+"%";
	}
	
	static String toLuceneQuery(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		int i = 0;
		outer: while (i < s.length()) {
			for (String special : LUCENE_SPECIAL) {
				if (s.regionMatches(i, special, 0, special.length())) {
					sb.append(LUCENE_ESCAPE);
					sb.append(special);
					i += special.length();
					continue outer;
				}
			}
			sb.append(s.charAt(i));
			i++;
		}
		return sb.toString();
	}
	
    private List<HTextFlow> findMatchingTextFlows(String searchQuery) {
        FullTextQuery query;
        try {
            query = constructQuery(searchQuery);
        } catch (ParseException e) {
        	log.warn("Can't parse query '"+searchQuery+"'");
            return Collections.emptyList(); 
        }
        List<HTextFlow> items = query
            .setMaxResults(MAX_RESULTS)
            .getResultList();
        return items;
    }

    private FullTextQuery constructQuery(String searchText) throws ParseException
    {
		// TODO filter by status Approved and by locale
        // TODO wildcard escaping?  stemming?  fuzzy matching?
        QueryParser parser = new QueryParser("content", new StandardAnalyzer());
//        parser.setAllowLeadingWildcard(true);
        Query luceneQuery = parser.parse(searchText);
        return entityManager.createFullTextQuery(luceneQuery, HTextFlow.class);
    }

	@Override
	public Class<GetTranslationMemory> getActionType() {
		return GetTranslationMemory.class;
	}

	@Override
	public void rollback(GetTranslationMemory action,
			GetTranslationMemoryResult result, ExecutionContext context)
			throws ActionException {
	}
}
