package org.fedorahosted.flies.webtrans.client;

import org.fedorahosted.flies.gwt.model.TransUnit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TransUnitDetailsView extends Composite implements TransUnitDetailsPresenter.Display {

	private static TransUnitDetailsViewUiBinder uiBinder = GWT
			.create(TransUnitDetailsViewUiBinder.class);

	interface TransUnitDetailsViewUiBinder extends
			UiBinder<Widget, TransUnitDetailsView> {
	}
	
	@UiField
	Label noDetailsLabel, sourceCommentsLabel;
	
	@UiField
	LayoutPanel rootPanel;

	public TransUnitDetailsView() {
		initWidget(uiBinder.createAndBindUi(this));
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

	@Override
	public void showDetails(TransUnit transUnit) {
		rootPanel.setWidgetTopHeight(noDetailsLabel, 0, Unit.PX, 0, Unit.PX);
		rootPanel.setWidgetTopBottom(sourceCommentsLabel, 0, Unit.PX, 0, Unit.PX);
		sourceCommentsLabel.setText(transUnit.getSourceComment());
	}
	
}