package net.openl10n.flies.webtrans.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import net.openl10n.flies.common.TransUnitCount;
import net.openl10n.flies.webtrans.client.editor.HasTransUnitCount;
import net.openl10n.flies.webtrans.client.editor.filter.ContentFilter;
import net.openl10n.flies.webtrans.client.events.DocumentSelectionEvent;
import net.openl10n.flies.webtrans.client.events.DocumentSelectionHandler;
import net.openl10n.flies.webtrans.client.events.NotificationEvent;
import net.openl10n.flies.webtrans.client.events.TransUnitUpdatedEvent;
import net.openl10n.flies.webtrans.client.events.TransUnitUpdatedEventHandler;
import net.openl10n.flies.webtrans.client.events.NotificationEvent.Severity;
import net.openl10n.flies.webtrans.client.rpc.CachingDispatchAsync;
import net.openl10n.flies.webtrans.shared.model.DocumentId;
import net.openl10n.flies.webtrans.shared.model.DocumentInfo;
import net.openl10n.flies.webtrans.shared.model.DocumentStatus;
import net.openl10n.flies.webtrans.shared.model.WorkspaceContext;
import net.openl10n.flies.webtrans.shared.rpc.GetDocumentList;
import net.openl10n.flies.webtrans.shared.rpc.GetDocumentListResult;
import net.openl10n.flies.webtrans.shared.rpc.GetProjectStatusCount;
import net.openl10n.flies.webtrans.shared.rpc.GetProjectStatusCountResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class DocumentListPresenter extends WidgetPresenter<DocumentListPresenter.Display> implements HasDocumentSelectionHandlers
{

   public static final Place PLACE = new Place("DocumentListList");

   public interface Display extends WidgetDisplay
   {

      void setList(ArrayList<DocumentInfo> sortedList);

      void clearSelection();

      void setSelection(DocumentInfo document);

      void ensureSelectionVisible();

      void setFilter(ContentFilter<DocumentInfo> filter);

      void removeFilter();

      HasValue<String> getFilterTextBox();

      HasTransUnitCount getTransUnitCountBar();

      HasSelectionHandlers<DocumentInfo> getDocumentList();
   }

   private final DispatchAsync dispatcher;
   private final WorkspaceContext workspaceContext;
   private final Map<DocumentId, DocumentStatus> statuscache = new HashMap<DocumentId, DocumentStatus>();
   private DocumentInfo currentDocument;
   private final TransUnitCount projectCount = new TransUnitCount();

   private final WebTransMessages messages;

   @Inject
   public DocumentListPresenter(Display display, EventBus eventBus, WorkspaceContext workspaceContext, CachingDispatchAsync dispatcher, final WebTransMessages messages)
   {
      super(display, eventBus);
      this.workspaceContext = workspaceContext;
      this.dispatcher = dispatcher;
      this.messages = messages;
      Log.info("DocumentListPresenter()");
      loadDocumentList();
   }

   @Override
   public Place getPlace()
   {
      return PLACE;
   }

   @Override
   protected void onBind()
   {

      registerHandler(display.getDocumentList().addSelectionHandler(new SelectionHandler<DocumentInfo>()
      {
         @Override
         public void onSelection(SelectionEvent<DocumentInfo> event)
         {
            currentDocument = event.getSelectedItem();
            display.setSelection(currentDocument);
            fireEvent(new DocumentSelectionEvent(currentDocument));
         }
      }));

      // display.setProjectStatusBar(prStatusPresenter.getDisplay().asWidget());

      registerHandler(eventBus.addHandler(TransUnitUpdatedEvent.getType(), new TransUnitUpdatedEventHandler()
      {
         @Override
         public void onTransUnitUpdated(TransUnitUpdatedEvent event)
         {
            DocumentStatus doc = statuscache.get(event.getDocumentId());
            if (doc == null)
               return; // GetProjectStatusCount hasn't returned yet!
               // ContentState status = event.getPreviousStatus();
               // doc.setStatus(status, doc.getStatus(status)-1);
               // status = event.getNewStatus();
               // doc.setStatus(status, doc.getStatus(status)+1);
               // TreeNode<DocName> node =
               // display.getTree().getNodeByKey(doc.getDocumentid());
               // node.setName(node.getObject().getName() + " ("+
               // calPercentage(doc.getUntranslated(), doc.getFuzzy(),
               // doc.getTranslated()) +"%)");
         }

      }));

      // registerHandler(getDisplay().getTree().addSelectionHandler(new
      // SelectionHandler<TreeItem>() {
      // @Override
      // public void onSelection(SelectionEvent<TreeItem> event) {
      // DocName selectedDocName = (DocName)
      // event.getSelectedItem().getUserObject();
      // if (selectedDocName != null) // folders have null names
      // setValue(selectedDocName.getId(), true);
      // }
      // }));
      //		
      // registerHandler(display.getReloadButton().addClickHandler(new
      // ClickHandler() {
      // @Override
      // public void onClick(ClickEvent event) {
      // refreshDisplay();
      // }
      // }));

      registerHandler(display.getFilterTextBox().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               display.removeFilter();
            }
            else
            {
               basicContentFilter.setPattern(event.getValue());
               display.setFilter(basicContentFilter);
            }
         }
      }));

      registerHandler(eventBus.addHandler(TransUnitUpdatedEvent.getType(), new TransUnitUpdatedEventHandler()
      {
         @Override
         public void onTransUnitUpdated(TransUnitUpdatedEvent event)
         {
            projectCount.decrement(event.getPreviousStatus());
            projectCount.increment(event.getNewStatus());
            getDisplay().getTransUnitCountBar().setCount(projectCount);
         }
      }));

      // TODO get rid of this
      // It is fetching stats for all documents in the workspace,
      // but then it adds them all up
      // and discards the individual document stats.
      dispatcher.execute(new GetProjectStatusCount(), new AsyncCallback<GetProjectStatusCountResult>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
         }

         @Override
         public void onSuccess(GetProjectStatusCountResult result)
         {
            ArrayList<DocumentStatus> liststatus = result.getStatus();
            for (DocumentStatus doc : liststatus)
            {
               projectCount.add(doc.getCount());
            }
            display.getTransUnitCountBar().setCount(projectCount);

         }
      });

   }

   final class BasicContentFilter implements ContentFilter<DocumentInfo>
   {
      private String pattern = "";

      @Override
      public boolean accept(DocumentInfo value)
      {
         return value.getName().contains(pattern);
      }

      public void setPattern(String pattern)
      {
         this.pattern = pattern;
      }
   }

   private final BasicContentFilter basicContentFilter = new BasicContentFilter();

   private long calPercentage(long untranslated, long fuzzy, long translated)
   {

      if (translated < 0 || untranslated < 0 || fuzzy < 0 || (translated + untranslated + fuzzy) == 0)
      {
         return 0;
      }
      else
      {
         long value = (long) ((translated * 100) / (fuzzy + untranslated + translated));
         return value;
      }

   }

   @Override
   protected void onPlaceRequest(PlaceRequest request)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void onUnbind()
   {
   }

   @Override
   public void refreshDisplay()
   {
      loadDocumentList();
   }

   @Override
   public void revealDisplay()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public HandlerRegistration addDocumentSelectionHandler(DocumentSelectionHandler handler)
   {
      return eventBus.addHandler(DocumentSelectionEvent.getType(), handler);
   }

   @Override
   public void fireEvent(GwtEvent<?> event)
   {
      eventBus.fireEvent(event);
   }

   public void setDocumentList(ArrayList<DocumentInfo> documents)
   {
      ArrayList<DocumentInfo> sortedList = new ArrayList<DocumentInfo>(documents);

      Collections.sort(sortedList, new Comparator<DocumentInfo>()
      {
         @Override
         public int compare(DocumentInfo o1, DocumentInfo o2)
         {
            String path1 = o1.getPath();
            if (path1 == null)
               path1 = "";
            String path2 = o2.getPath();
            if (path2 == null)
               path2 = "";
            int pathCompare = path1.compareTo(path2);
            if (pathCompare == 0)
               return o1.getName().compareTo(o2.getName());
            return pathCompare;
         }
      });
      display.setList(sortedList);
   }

   private void loadDocumentList()
   {
      loadDocsStatus();
      // switch doc list to the new project
      dispatcher.execute(new GetDocumentList(workspaceContext.getWorkspaceId().getProjectIterationId()), new AsyncCallback<GetDocumentListResult>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            eventBus.fireEvent(new NotificationEvent(Severity.Error, messages.loadDocFailed()));
         }

         @Override
         public void onSuccess(GetDocumentListResult result)
         {
            final ArrayList<DocumentInfo> documents = result.getDocuments();
            Log.info("Received doc list for " + result.getProjectIterationId() + ": " + documents.size() + " elements");
            setDocumentList(documents);
         }
      });
   }

   private void loadDocsStatus()
   {
      dispatcher.execute(new GetProjectStatusCount(), new AsyncCallback<GetProjectStatusCountResult>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.info("load Doc Status failure " + caught.getMessage());
         }

         @Override
         public void onSuccess(GetProjectStatusCountResult result)
         {
            ArrayList<DocumentStatus> liststatus = result.getStatus();
            Log.info("Received project status for " + liststatus.size() + " elements");
            statuscache.clear();
            for (DocumentStatus doc : liststatus)
            {
               statuscache.put(doc.getDocumentid(), doc);
               // TreeNode<DocName> node =
               // display.getTree().getNodeByKey(doc.getDocumentid());
               // node.setName(node.getObject().getName() + " ("+
               // calPercentage(doc.getUntranslated(), doc.getFuzzy(),
               // doc.getTranslated()) +"%)");
            }
         }
      });
   }

}
