package org.fedorahosted.flies.webtrans.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CaptionPanel extends DecoratorPanel {

	private VerticalPanel mainPanel;
	private HorizontalPanel headPanel;
	private VerticalPanel bodyPanel;
	
	Label titleWidget;
	private Button collapseButton;
	private boolean collapseButtonVisible;
	
	public CaptionPanel() {
		addStyleName("gwt-CaptionPanel");
		
		// init panel
		mainPanel = new VerticalPanel();
		mainPanel.setBorderWidth(0);
		mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		mainPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		mainPanel.setSpacing(0);
		mainPanel.setWidth("100%");
		setWidth("100%");
		
		headPanel = new HorizontalPanel();
		bodyPanel = new VerticalPanel();
		
		titleWidget = new Label();
		collapseButtonVisible = true;
		collapseButton = new Button("-");
		collapseButton.setStylePrimaryName("gwt-CaptionPanel-Collapse");
		collapseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				collapseButtonVisible = !collapseButtonVisible;
				String collapseButtonIcon = collapseButtonVisible ? "-" : "+";
				collapseButton.setText(collapseButtonIcon);
				mainPanel.getWidget(1).setVisible(collapseButtonVisible);
			}
		});
		
		headPanel.addStyleName("gwt-CaptionPanel-Heading");
		headPanel.setWidth("100%");
		
		headPanel.add(titleWidget);
		headPanel.add(collapseButton);
		
		headPanel.setCellVerticalAlignment(titleWidget, HorizontalPanel.ALIGN_MIDDLE);
		headPanel.setCellHorizontalAlignment(collapseButton, HorizontalPanel.ALIGN_RIGHT);
		
		mainPanel.add(headPanel);
		mainPanel.add(bodyPanel);
		add(mainPanel);
	}
	
	public void addBody(Widget widget) {
		bodyPanel.add(widget);
	}
	
	public void setTitle(String title) {
		titleWidget.setText(title);
	}
}
