package org.fedorahosted.flies.webtrans.gwt;

import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.fedorahosted.flies.LocaleId;
import org.fedorahosted.flies.core.model.StatusCount;
import org.fedorahosted.flies.gwt.rpc.GetProjectStatusCount;
import org.fedorahosted.flies.gwt.rpc.GetProjectStatusCountResult;
import org.fedorahosted.flies.repository.util.TranslationStatistics;
import org.fedorahosted.flies.rest.dto.TextFlowTarget.ContentState;
import org.fedorahosted.flies.webtrans.TranslationWorkspace;
import org.fedorahosted.flies.webtrans.TranslationWorkspaceManager;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("webtrans.gwt.GetProjectStatusCountHandler")
@Scope(ScopeType.STATELESS)
public class GetProjectStatusCountHandler implements ActionHandler<GetProjectStatusCount, GetProjectStatusCountResult> {

		@Logger Log log;
		
		@In Session session;

		@In TranslationWorkspaceManager translationWorkspaceManager;
		
		@Override
		public GetProjectStatusCountResult execute(GetProjectStatusCount action,
				ExecutionContext context) throws ActionException {
			org.fedorahosted.flies.LocaleId fliesLocaleId = new org.fedorahosted.flies.LocaleId(action.getLocaleId().getValue());		
			
			List<StatusCount> stats = session.createQuery(
					"select new org.fedorahosted.flies.core.model.StatusCount(tft.state, count(tft)) " +
			        "from HTextFlowTarget tft where tft.textFlow.document.project.id = :id " +
			        "  and tft.locale = :locale "+ 
					"group by tft.state"
				).setParameter("id", action.getProjectContainerId().getId())
				 .setParameter("locale", fliesLocaleId)
				 .list();
			
			
			Long totalCount = (Long) session.createQuery("select count(tf) from HTextFlow tf where tf.document.project.id = :id")
				.setParameter("id", action.getProjectContainerId().getId())
				.uniqueResult();
			
			TranslationStatistics stat = new TranslationStatistics();
			for(StatusCount count: stats){
				stat.set(count.status, count.count);
			}
			
			stat.set(ContentState.New, totalCount - stat.getNotApproved());
			//LocaleId localeId = new LocaleId(action.getLocaleId().getValue());
			TranslationWorkspace workspace = translationWorkspaceManager.getWorkspace(action.getProjectContainerId().getId(), fliesLocaleId);
			
			return new GetProjectStatusCountResult(action.getProjectContainerId(), stat.getNew(),stat.getFuzzyMatch()+stat.getForReview(), stat.getApproved(), workspace.getLatestEventOffset());

		}

		@Override
		public Class<GetProjectStatusCount> getActionType() {
			// TODO Auto-generated method stub
			return GetProjectStatusCount.class;
		}

		@Override
		public void rollback(GetProjectStatusCount action, GetProjectStatusCountResult result,
				ExecutionContext context) throws ActionException {
		}

}
