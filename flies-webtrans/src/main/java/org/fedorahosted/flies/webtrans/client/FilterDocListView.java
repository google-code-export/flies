package org.fedorahosted.flies.webtrans.client;

import org.fedorahosted.flies.webtrans.client.FilterDocListPresenter.Display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FilterDocListView extends FlowPanel implements Display {
	private TextBox textBox = new TextBox();
	
	@Inject
	public FilterDocListView(DocumentListPresenter.Display docList) {
		GWT.log("FilterDocListView()", null);
		add(textBox);
		add(docList.asWidget());
	}

	@Override
	public HasValueChangeHandlers<String> getFilterChangeSource() {
		return textBox;
	}
	
	@Override
	public HasKeyUpHandlers getFilterKeyUpSource() {
		return textBox;
	}

	@Override
	public HasText getFilterText() {
		return textBox;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void startProcessing() {
	}

	@Override
	public void stopProcessing() {
	}

}
