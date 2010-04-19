package org.fedorahosted.flies.webtrans.client;

import java.util.ArrayList;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.fedorahosted.flies.gwt.common.WorkspaceContext;
import org.fedorahosted.flies.gwt.model.TransMemory;
import org.fedorahosted.flies.gwt.model.TransUnit;
import org.fedorahosted.flies.gwt.rpc.GetTranslationMemory;
import org.fedorahosted.flies.gwt.rpc.GetTranslationMemoryResult;
import org.fedorahosted.flies.gwt.rpc.GetTranslationMemory.SearchType;
import org.fedorahosted.flies.webtrans.client.events.TransUnitSelectionEvent;
import org.fedorahosted.flies.webtrans.client.events.TransUnitSelectionHandler;
import org.fedorahosted.flies.webtrans.client.rpc.CachingDispatchAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class TransMemoryPresenter extends WidgetPresenter<TransMemoryPresenter.Display> {
	private final WorkspaceContext workspaceContext;
	private final CachingDispatchAsync dispatcher;
	
	public interface Display extends WidgetDisplay {
		HasValue<Boolean> getExactButton();
		HasClickHandlers getSearchButton();
		HasText getTmTextBox();
		void createTable(ArrayList<TransMemory> memories);
	}

	@Inject
	public TransMemoryPresenter(Display display, EventBus eventBus, CachingDispatchAsync dispatcher, WorkspaceContext workspaceContext) {
		super(display, eventBus);
		this.dispatcher = dispatcher;
		this.workspaceContext = workspaceContext;
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	protected void onBind() {
		display.getSearchButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				display.startProcessing();
				final String query = display.getTmTextBox().getText();
				GetTranslationMemory.SearchType searchType = 
					display.getExactButton().getValue() ? 
						SearchType.EXACT : SearchType.RAW;
				GetTranslationMemory action = new GetTranslationMemory(
						query, workspaceContext.getWorkspaceId().getLocaleId(), searchType);
				dispatcher.execute(action, new AsyncCallback<GetTranslationMemoryResult>() {
					@Override
					public void onFailure(Throwable caught) {
					}
					@Override
					public void onSuccess(GetTranslationMemoryResult result) {
						ArrayList<TransMemory> memories = result.getMemories();
						display.createTable(memories);
					}
				});
			}
		});
		
		registerHandler(eventBus.addHandler(TransUnitSelectionEvent.getType(), new TransUnitSelectionHandler() {
			@Override
			public void onTransUnitSelected(TransUnitSelectionEvent event) {
				showResultsFor(event.getSelection());
			}
		}));
	}

	public void showResultsFor(TransUnit transUnit) {
		display.getTmTextBox().setText("");
		display.startProcessing();
		//Start automatically fuzzy search
		final String query = transUnit.getSource();
		final GetTranslationMemory action = new GetTranslationMemory(
				query, 
				workspaceContext.getWorkspaceId().getLocaleId(), 
				GetTranslationMemory.SearchType.FUZZY);
		dispatcher.execute(action, new AsyncCallback<GetTranslationMemoryResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error(caught.getMessage(), caught);
			}
			@Override
			public void onSuccess(GetTranslationMemoryResult result) {
				ArrayList<TransMemory> memories = result.getMemories();
				display.createTable(memories);
			}
		});
	}
	
	@Override
	protected void onPlaceRequest(PlaceRequest request) {
	}

	@Override
	protected void onUnbind() {
	}

	@Override
	public void refreshDisplay() {
	}

	@Override
	public void revealDisplay() {
	}
}
