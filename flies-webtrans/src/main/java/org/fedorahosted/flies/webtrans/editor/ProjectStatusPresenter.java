package org.fedorahosted.flies.webtrans.editor;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;

import org.fedorahosted.flies.gwt.rpc.GetProjectStatusCount;
import org.fedorahosted.flies.gwt.rpc.GetProjectStatusCountResult;
import org.fedorahosted.flies.webtrans.client.WorkspaceContext;
import org.fedorahosted.flies.webtrans.client.events.TransUnitUpdatedEvent;
import org.fedorahosted.flies.webtrans.client.events.TransUnitUpdatedEventHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ProjectStatusPresenter extends TranslationStatsBarPresenter{

	private final DispatchAsync dispatcher;
	private final WorkspaceContext workspaceContext;
	private int latestStatusCountOffset = -1;

	@Inject
	public ProjectStatusPresenter (Display display, EventBus eventBus,
			DispatchAsync dispatcher,
			WorkspaceContext workspaceContext) {
		super(display, eventBus);
		this.dispatcher = dispatcher;
        this.workspaceContext = workspaceContext;
	}

	@Override
	public Place getPlace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onBind() {
		requestStatusCount();
		
		registerHandler(eventBus.addHandler(TransUnitUpdatedEvent.getType(), new TransUnitUpdatedEventHandler() {
			@Override
			public void onTransUnitUpdated(TransUnitUpdatedEvent event) {
				
				if( event.getOffset() < latestStatusCountOffset){
					return;
				}
				
				int fuzzyCount = getDisplay().getFuzzy();
				int translatedCount = getDisplay().getTranslated();
				int untranslatedCount = getDisplay().getUntranslated();
				
				switch (event.getData().getPreviousStatus() ) {
				case Approved:
					translatedCount--;
					break;
				case NeedReview:
					fuzzyCount--;
					break;
				case New:
					untranslatedCount--;
					break;
				}
				
				switch (event.getData().getNewStatus() ) {
				case Approved:
					translatedCount++;
					break;
				case NeedReview:
					fuzzyCount++;
					break;
				case New:
					untranslatedCount++;
					break;
				}
				
				getDisplay().setStatus(fuzzyCount, translatedCount, untranslatedCount);
				
			}


		}));
		
		
		
	}
	
	@Override
	protected void onPlaceRequest(PlaceRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revealDisplay() {
		// TODO Auto-generated method stub
		
	}
	
	private void requestStatusCount() {
		dispatcher.execute(new GetProjectStatusCount(workspaceContext.getProjectContainerId(), workspaceContext.getLocaleId()), new AsyncCallback<GetProjectStatusCountResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(GetProjectStatusCountResult result) {
				Log.info("Project Status:"+(int)result.getUntranslated());
				getDisplay().setStatus((int) result.getFuzzy(), (int)result.getTranslated(), (int)result.getUntranslated());
				latestStatusCountOffset = result.getOffset();
			}
	});
	}	

}
