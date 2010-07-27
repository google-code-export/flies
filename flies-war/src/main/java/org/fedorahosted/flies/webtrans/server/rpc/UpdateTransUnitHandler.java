package org.fedorahosted.flies.webtrans.server.rpc;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.model.HTextFlow;
import org.fedorahosted.flies.model.HTextFlowTarget;
import org.fedorahosted.flies.security.FliesIdentity;
import org.fedorahosted.flies.webtrans.server.ActionHandlerFor;
import org.fedorahosted.flies.webtrans.server.TranslationWorkspace;
import org.fedorahosted.flies.webtrans.server.TranslationWorkspaceManager;
import org.fedorahosted.flies.webtrans.shared.model.DocumentId;
import org.fedorahosted.flies.webtrans.shared.model.ProjectIterationId;
import org.fedorahosted.flies.webtrans.shared.rpc.TransUnitUpdated;
import org.fedorahosted.flies.webtrans.shared.rpc.UpdateTransUnit;
import org.fedorahosted.flies.webtrans.shared.rpc.UpdateTransUnitResult;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("webtrans.gwt.UpdateTransUnitHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(UpdateTransUnit.class)
public class UpdateTransUnitHandler extends AbstractActionHandler<UpdateTransUnit, UpdateTransUnitResult>
{

   @Logger
   Log log;

   @In
   Session session;

   @In
   TranslationWorkspaceManager translationWorkspaceManager;

   @Override
   public UpdateTransUnitResult execute(UpdateTransUnit action, ExecutionContext context) throws ActionException
   {

      FliesIdentity.instance().checkLoggedIn();
      log.info("Updating TransUnit {0}: locale {1}, state {2}, content '{3}'", action.getTransUnitId(), action.getWorkspaceId().getLocaleId(), action.getContentState(), action.getContent());

      HTextFlow hTextFlow = (HTextFlow) session.get(HTextFlow.class, action.getTransUnitId().getValue());
      HTextFlowTarget target = hTextFlow.getTargets().get(action.getWorkspaceId().getLocaleId());
      ContentState prevStatus = ContentState.New;
      if (target == null)
      {
         target = new HTextFlowTarget(hTextFlow, action.getWorkspaceId().getLocaleId());
         hTextFlow.getTargets().put(action.getWorkspaceId().getLocaleId(), target);
      }
      else
      {
         prevStatus = target.getState();
      }
      target.setState(action.getContentState());
      target.setContent(action.getContent());
      // TODO update last modified by
      session.flush();

      TransUnitUpdated event = new TransUnitUpdated(new DocumentId(hTextFlow.getDocument().getId()), action.getTransUnitId(), prevStatus, action.getContentState());

      TranslationWorkspace workspace = translationWorkspaceManager.getOrRegisterWorkspace(action.getWorkspaceId());
      workspace.publish(event);

      return new UpdateTransUnitResult(true);
   }

   @Override
   public void rollback(UpdateTransUnit action, UpdateTransUnitResult result, ExecutionContext context) throws ActionException
   {
   }

}