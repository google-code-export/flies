package org.fedorahosted.flies.webtrans.client;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppView extends DockPanel implements AppPresenter.Display {

	public AppView() {
		setSpacing(3);
//		setBorderWidth(1);
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
	public void addMain(Widget main) {
		add(main, DockPanel.CENTER );
		setCellWidth(main, "100%");
	}
	
	@Override
	public void addWest(Widget west) {
		add(west, DockPanel.WEST );
//		setCellWidth(west, "220px");
	}
	
	@Override
	public void addNorth(Widget north) {
		add(north, DockPanel.NORTH );
		// just for Chrome's benefit:
		setCellHeight(north, "20px");
	}

	@Override
	public void addSouth(Widget south) {
		add(south, DockPanel.SOUTH );
		// just for Chrome's benefit:
		setCellHeight(south, "20px");
	}
	
}
