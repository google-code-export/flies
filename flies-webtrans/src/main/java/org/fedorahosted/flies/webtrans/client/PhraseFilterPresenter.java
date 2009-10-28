package org.fedorahosted.flies.webtrans.client;

import org.fedorahosted.flies.gwt.model.Person;
import org.fedorahosted.flies.webtrans.client.ui.HasChildTreeNodes;
import org.fedorahosted.flies.webtrans.client.ui.HasFilter;
import org.fedorahosted.flies.webtrans.client.ui.HasNodeMouseOutHandlers;
import org.fedorahosted.flies.webtrans.client.ui.HasNodeMouseOverHandlers;
import org.fedorahosted.flies.webtrans.editor.filter.PhraseFilter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public class PhraseFilterPresenter extends WidgetPresenter<PhraseFilterPresenter.Display> {
	
	private PhraseFilter filter;
	
	@Inject
	public PhraseFilterPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
	}

	public interface Display extends WidgetDisplay{
		HasValue<String> getFilterText();
	}

	public void bind(PhraseFilter filter) {
		this.filter = filter;
		bind();
	}
	
	@Override
	public Place getPlace() {
		return null;
	}

	public PhraseFilter getFilter() {
		return filter;
	}
	
	@Override
	protected void onBind() {
		display.getFilterText().addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				filter.setPhrase(event.getValue());
			}
		});
		refreshDisplay();
	}

	@Override
	protected void onPlaceRequest(PlaceRequest request) {
	}

	@Override
	protected void onUnbind() {
	}

	@Override
	public void refreshDisplay() {
		display.getFilterText().setValue(filter.getPhrase());
	}

	@Override
	public void revealDisplay() {
	}
}
