package org.fedorahosted.flies.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.EntityTag;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.common.TransUnitCount;
import org.fedorahosted.flies.model.HDocument;
import org.fedorahosted.flies.model.HProjectIteration;
import org.fedorahosted.flies.model.StatusCount;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("documentDAO")
@AutoCreate
public class DocumentDAO extends AbstractDAOImpl<HDocument, Long>{

	public DocumentDAO() {
		super(HDocument.class);
	}
	
	public DocumentDAO(Session session) {
		super(HDocument.class, session);
	}

	public EntityTag getETag(HProjectIteration iteration, String id) {
		return EntityTag.valueOf("x");
	}
	
	public HDocument getByDocId(HProjectIteration iteration, String id){
		return (HDocument) getSession().createCriteria(HDocument.class)
			.add( Restrictions.naturalId()
		        .set("docId", id)
		        .set("projectIteration", iteration)
		    	)
		    .setCacheable(true)
		    .setComment("DocumentDAO.getById")
		    .uniqueResult();
	}

	public Set<LocaleId> getTargetLocales(HDocument hDoc) {
		List<LocaleId> locales = (List<LocaleId>) getSession().createQuery(
				"select tft.locale from HTextFlowTarget tft where tft.textFlow.document = :document")
			.setParameter("document", hDoc).list();
		return new HashSet<LocaleId>(locales);
	}
	
	public TransUnitCount getStatistics(long docId, LocaleId localeId) {
		List<StatusCount> stats = getSession().createQuery(
				"select new org.fedorahosted.flies.model.StatusCount(tft.state, count(tft)) " +
				"from HTextFlowTarget tft " +
				"where tft.textFlow.document.id = :id " +
				"  and tft.locale = :locale "+  
				"  and tft.textFlow.obsolete = :obsolete "+
				"group by tft.state"
			).setParameter("id", docId)
			.setParameter("obsolete", false)
			.setParameter("locale", localeId)
			.setCacheable(true)
			.list();
		
		
		Long totalCount = (Long) getSession().createQuery("select count(tf) from HTextFlow tf where tf.document.id = :id")
			.setParameter("id", docId)
			.setCacheable(true).uniqueResult();

		TransUnitCount stat = new TransUnitCount();
		for(StatusCount count: stats){
			stat.set(count.status, count.count.intValue());
		}
		
		stat.set(ContentState.New, totalCount.intValue() - stat.get(ContentState.Approved) -stat.get(ContentState.NeedReview));
		
		return stat;
	}
}
