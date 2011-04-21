/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.gin;

import org.zanata.webtrans.client.AppPresenter;
import org.zanata.webtrans.client.AppView;
import org.zanata.webtrans.client.Application;
import org.zanata.webtrans.client.DocumentListPresenter;
import org.zanata.webtrans.client.DocumentListView;
import org.zanata.webtrans.client.EventProcessor;
import org.zanata.webtrans.client.Resources;
import org.zanata.webtrans.client.SidePanel;
import org.zanata.webtrans.client.SidePanelPresenter;
import org.zanata.webtrans.client.TransMemoryDetailsPresenter;
import org.zanata.webtrans.client.TransMemoryDetailsView;
import org.zanata.webtrans.client.TransMemoryPresenter;
import org.zanata.webtrans.client.TransMemoryView;
import org.zanata.webtrans.client.TransUnitDetailsPresenter;
import org.zanata.webtrans.client.TransUnitDetailsView;
import org.zanata.webtrans.client.TransUnitNavigationPresenter;
import org.zanata.webtrans.client.TransUnitNavigationView;
import org.zanata.webtrans.client.TranslationEditorPresenter;
import org.zanata.webtrans.client.TranslationEditorView;
import org.zanata.webtrans.client.TranslationPresenter;
import org.zanata.webtrans.client.TranslationView;
import org.zanata.webtrans.client.UndoRedoPresenter;
import org.zanata.webtrans.client.UndoRedoView;
import org.zanata.webtrans.client.WebTransMessages;
import org.zanata.webtrans.client.WorkspaceUsersPresenter;
import org.zanata.webtrans.client.WorkspaceUsersView;
import org.zanata.webtrans.client.editor.HasPageNavigation;
import org.zanata.webtrans.client.editor.filter.TransFilterPresenter;
import org.zanata.webtrans.client.editor.filter.TransFilterView;
import org.zanata.webtrans.client.editor.table.TableEditorPresenter;
import org.zanata.webtrans.client.editor.table.TableEditorView;
import org.zanata.webtrans.client.rpc.CachingDispatchAsync;
import org.zanata.webtrans.client.rpc.DelegatingDispatchAsync;
import org.zanata.webtrans.shared.auth.Identity;
import org.zanata.webtrans.shared.model.WorkspaceContext;

import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;

import com.google.inject.Provider;
import com.google.inject.Singleton;

public class WebTransClientModule extends AbstractPresenterModule
{

   /**
    * The Binding EDSL is described in {@link com.google.inject.Binder}
    */
   @Override
   protected void configure()
   {
      bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class);
      bind(EventProcessor.class).in(Singleton.class);
      bind(Resources.class).in(Singleton.class);
      bind(WebTransMessages.class).in(Singleton.class);

      bindPresenter(AppPresenter.class, AppPresenter.Display.class, AppView.class);
      bindPresenter(DocumentListPresenter.class, DocumentListPresenter.Display.class, DocumentListView.class);
      bindPresenter(TranslationPresenter.class, TranslationPresenter.Display.class, TranslationView.class);
      bindPresenter(TransFilterPresenter.class, TransFilterPresenter.Display.class, TransFilterView.class);
      bindPresenter(TableEditorPresenter.class, TableEditorPresenter.Display.class, TableEditorView.class);
      bindPresenter(WorkspaceUsersPresenter.class, WorkspaceUsersPresenter.Display.class, WorkspaceUsersView.class);
      bindPresenter(TransMemoryPresenter.class, TransMemoryPresenter.Display.class, TransMemoryView.class);
      bindPresenter(TransMemoryDetailsPresenter.class, TransMemoryDetailsPresenter.Display.class, TransMemoryDetailsView.class);
      bindPresenter(TransUnitNavigationPresenter.class, TransUnitNavigationPresenter.Display.class, TransUnitNavigationView.class);
      bindPresenter(SidePanelPresenter.class, SidePanelPresenter.Display.class, SidePanel.class);
      bindPresenter(TranslationEditorPresenter.class, TranslationEditorPresenter.Display.class, TranslationEditorView.class);
      bindPresenter(TransUnitDetailsPresenter.class, TransUnitDetailsPresenter.Display.class, TransUnitDetailsView.class);
      bindPresenter(UndoRedoPresenter.class, UndoRedoPresenter.Display.class, UndoRedoView.class);

      bind(HasPageNavigation.class).to(TableEditorView.class).in(Singleton.class);

      // NB: if we bind directly to SeamDispatchAsync, we can't use
      // replace-class in
      // the module definition unless the replacement extends SeamDispatchAsync
      bind(CachingDispatchAsync.class).to(DelegatingDispatchAsync.class).in(Singleton.class);

      bind(Identity.class).toProvider(IdentityProvider.class).in(Singleton.class);
      bind(WorkspaceContext.class).toProvider(WorkspaceContextProvider.class).in(Singleton.class);

   }

   static class WorkspaceContextProvider implements Provider<WorkspaceContext>
   {
      @Override
      public WorkspaceContext get()
      {
         return Application.getWorkspaceContext();
      }
   }

   static class IdentityProvider implements Provider<Identity>
   {
      @Override
      public Identity get()
      {
         return Application.getIdentity();
      }
   }

}
