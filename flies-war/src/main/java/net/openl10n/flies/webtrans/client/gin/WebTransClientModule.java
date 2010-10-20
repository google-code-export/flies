package net.openl10n.flies.webtrans.client.gin;

import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;
import net.customware.gwt.presenter.client.place.PlaceManager;
import net.openl10n.flies.webtrans.client.AppPresenter;
import net.openl10n.flies.webtrans.client.AppView;
import net.openl10n.flies.webtrans.client.Application;
import net.openl10n.flies.webtrans.client.DocumentListPresenter;
import net.openl10n.flies.webtrans.client.DocumentListView;
import net.openl10n.flies.webtrans.client.EventProcessor;
import net.openl10n.flies.webtrans.client.Resources;
import net.openl10n.flies.webtrans.client.SidePanel;
import net.openl10n.flies.webtrans.client.SidePanelPresenter;
import net.openl10n.flies.webtrans.client.TransMemoryDetailsPresenter;
import net.openl10n.flies.webtrans.client.TransMemoryDetailsView;
import net.openl10n.flies.webtrans.client.TransMemoryPresenter;
import net.openl10n.flies.webtrans.client.TransMemoryView;
import net.openl10n.flies.webtrans.client.TransUnitDetailsPresenter;
import net.openl10n.flies.webtrans.client.TransUnitDetailsView;
import net.openl10n.flies.webtrans.client.TransUnitNavigationPresenter;
import net.openl10n.flies.webtrans.client.TransUnitNavigationView;
import net.openl10n.flies.webtrans.client.TranslationEditorPresenter;
import net.openl10n.flies.webtrans.client.TranslationEditorView;
import net.openl10n.flies.webtrans.client.WebTransMessages;
import net.openl10n.flies.webtrans.client.WorkspaceUsersPresenter;
import net.openl10n.flies.webtrans.client.WorkspaceUsersView;
import net.openl10n.flies.webtrans.client.editor.CachedListEditorTableModel;
import net.openl10n.flies.webtrans.client.editor.HasPageNavigation;
import net.openl10n.flies.webtrans.client.editor.ListEditorPresenter;
import net.openl10n.flies.webtrans.client.editor.ListEditorTable;
import net.openl10n.flies.webtrans.client.editor.ListEditorTableDefinition;
import net.openl10n.flies.webtrans.client.filter.TransFilterPresenter;
import net.openl10n.flies.webtrans.client.filter.TransFilterView;
import net.openl10n.flies.webtrans.client.rpc.CachingDispatchAsync;
import net.openl10n.flies.webtrans.client.rpc.DelegatingDispatchAsync;
import net.openl10n.flies.webtrans.shared.auth.Identity;
import net.openl10n.flies.webtrans.shared.model.WorkspaceContext;


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
      bind(PlaceManager.class).in(Singleton.class);
      bind(EventProcessor.class).in(Singleton.class);
      bind(Resources.class).in(Singleton.class);
      bind(WebTransMessages.class).in(Singleton.class);

      bindPresenter(AppPresenter.class, AppPresenter.Display.class, AppView.class);
      bindPresenter(DocumentListPresenter.class, DocumentListPresenter.Display.class, DocumentListView.class);
      bindPresenter(TransFilterPresenter.class, TransFilterPresenter.Display.class, TransFilterView.class);

      bind(CachedListEditorTableModel.class).in(Singleton.class);
      bind(ListEditorTableDefinition.class).in(Singleton.class);
      
      bindPresenter(ListEditorPresenter.class, ListEditorPresenter.Display.class, ListEditorTable.class);
      
      bindPresenter(WorkspaceUsersPresenter.class, WorkspaceUsersPresenter.Display.class, WorkspaceUsersView.class);
      bindPresenter(TransMemoryPresenter.class, TransMemoryPresenter.Display.class, TransMemoryView.class);
      bindPresenter(TransMemoryDetailsPresenter.class, TransMemoryDetailsPresenter.Display.class, TransMemoryDetailsView.class);
      bindPresenter(TransUnitNavigationPresenter.class, TransUnitNavigationPresenter.Display.class, TransUnitNavigationView.class);
      bindPresenter(SidePanelPresenter.class, SidePanelPresenter.Display.class, SidePanel.class);
      bindPresenter(TranslationEditorPresenter.class, TranslationEditorPresenter.Display.class, TranslationEditorView.class);
      bindPresenter(TransUnitDetailsPresenter.class, TransUnitDetailsPresenter.Display.class, TransUnitDetailsView.class);

      bind(HasPageNavigation.class).to(ListEditorTable.class).in(Singleton.class);

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
