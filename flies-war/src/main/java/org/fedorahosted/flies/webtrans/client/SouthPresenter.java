package org.fedorahosted.flies.webtrans.client;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class SouthPresenter extends WidgetPresenter<SouthPresenter.Display> {
	private final TransMemoryPresenter transMemorypresenter;
	
	public interface Display extends WidgetDisplay {
		HasWidgets getWidgets();
		HasText getGlossary();
		HasText getRelated();
		HasSelectionHandlers<Integer> getSelectionHandler();
		int getTransPanelIndex();
		boolean isDisclosurePanelOpen();
	}
	
	@Inject
	public SouthPresenter(Display display, EventBus eventBus, TransMemoryPresenter transMemorypresenter) {
		super(display, eventBus);
		this.transMemorypresenter = transMemorypresenter;
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	protected void onBind() {
		transMemorypresenter.bind();
		display.getWidgets().add(transMemorypresenter.getDisplay().asWidget());
		refreshDisplay();
		display.getSelectionHandler().addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(display.isDisclosurePanelOpen()) {
					if (event.getSelectedItem() == display.getTransPanelIndex()) {
						//Translation Memory Tab is visible, Send TranslationMemoryVisibleEvent to TableEditorPresenter
						eventBus.fireEvent(new TranslationMemoryVisibleEvent(true));
					}
					else {
						eventBus.fireEvent(new TranslationMemoryVisibleEvent(false));
					}
				}
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
