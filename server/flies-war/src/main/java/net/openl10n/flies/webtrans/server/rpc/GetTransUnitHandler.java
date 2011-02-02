/*
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package net.openl10n.flies.webtrans.server.rpc;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.dao.TextFlowDAO;
import net.openl10n.flies.model.HLocale;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.model.HTextFlowTarget;
import net.openl10n.flies.security.FliesIdentity;
import net.openl10n.flies.service.LocaleService;
import net.openl10n.flies.webtrans.server.ActionHandlerFor;
import net.openl10n.flies.webtrans.shared.model.TransUnit;
import net.openl10n.flies.webtrans.shared.model.TransUnitId;
import net.openl10n.flies.webtrans.shared.rpc.GetTransUnitResult;
import net.openl10n.flies.webtrans.shared.rpc.GetTransUnit;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("webtrans.gwt.GetTransUnitHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(GetTransUnit.class)
public class GetTransUnitHandler extends AbstractActionHandler<GetTransUnit, GetTransUnitResult>
{
   @Logger
   Log log;

   @In
   private TextFlowDAO textFlowDAO;

   @In
   private LocaleService localeServiceImpl;

   @Override
   public GetTransUnitResult execute(GetTransUnit action, ExecutionContext context) throws ActionException
   {
      FliesIdentity.instance().checkLoggedIn();

      log.info("Fetching Transunit for {0}", action.getId());

      HTextFlow textFlow = textFlowDAO.findById(action.getId(), false);
      TransUnitId tuId = new TransUnitId(textFlow.getId());
      TransUnit tu = new TransUnit(tuId, action.getWorkspaceId().getLocaleId(), textFlow.getContent(), CommentsUtil.toString(textFlow.getComment()), "", ContentState.New);
      HLocale hLocale = localeServiceImpl.getSupportedLanguageByLocale(action.getWorkspaceId().getLocaleId());
      HTextFlowTarget target = textFlow.getTargets().get(hLocale);
      if (target != null)
      {
         tu.setTarget(target.getContent());
         tu.setStatus(target.getState());
      }

      return new GetTransUnitResult(tu);
   }

   @Override
   public void rollback(GetTransUnit action, GetTransUnitResult result, ExecutionContext context) throws ActionException
   {
   }

}
