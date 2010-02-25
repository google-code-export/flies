package org.fedorahosted.flies.webtrans.client;

import java.util.ArrayList;

import net.customware.gwt.presenter.client.EventBus;

import org.fedorahosted.flies.gwt.model.Person;
import org.fedorahosted.flies.gwt.model.PersonId;
import org.fedorahosted.flies.gwt.model.TransMemory;
import org.fedorahosted.flies.webtrans.client.ui.TreeNodeImpl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.weborient.codemirror.client.HighlightingLabel;
import com.weborient.codemirror.client.ParserSyntax;

public class TransMemoryView extends FlowPanel implements TransMemoryPresenter.Display {

	private static final int CELL_PADDING = 5;
	private static final int HEADER_ROW = 0;
	private static final int SOURCE_COL = 0;
	private static final int TARGET_COL = 1;
	private static final int ACTION_COL = 2;
	
	private final TextBox tmTextBox = new TextBox();
	private final CheckBox phraseButton = new CheckBox("Exact");
	private final Button searchButton = new Button("Search");
	private final Button clearButton = new Button("Clear");

	final DecoratedPopupPanel resultSuppPanel = new DecoratedPopupPanel(true);
	
	private final FlexTable resultTable = new FlexTable();
	@Inject
	private EventBus eventBus;
	
	public TransMemoryView() {
		tmTextBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					searchButton.click();
				}
			}
		});
		
		clearButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				tmTextBox.setText("");
				clearResults();
			}
		});
		
		add(tmTextBox);
		add(phraseButton);
		add(searchButton);
		add(clearButton);
		add(resultTable);
	}
	
	@Override
	public HasValue<Boolean> getExactButton() {
		return phraseButton;
	}
	
	@Override
	public Button getSearchButton() {
		return searchButton;
	}
	
	public TextBox getTmTextBox() {
		return tmTextBox;
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
	public void createTable(ArrayList<TransMemory> memories) {
		clearResults();
		addColumn("Source", SOURCE_COL);
		addColumn("Target", TARGET_COL);
		addColumn("Action", ACTION_COL);

		int row = HEADER_ROW;
		for(final TransMemory memory: memories) {
			++row;
			final String sourceMessage = memory.getSource();
			final String targetMessage = memory.getMemory();
			final String sourceComment = memory.getSourceComment();
			final String targetComment = memory.getTargetComment();
			final String docID = memory.getDocID();
			resultTable.setWidget(row, SOURCE_COL, new HighlightingLabel(sourceMessage, ParserSyntax.MIXED));
			resultTable.setWidget(row, TARGET_COL, new HighlightingLabel(targetMessage, ParserSyntax.MIXED));

			final Anchor copyLink = new Anchor("Copy To Target");
			copyLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new TransMemoryCopyEvent(sourceMessage, targetMessage));
					Log.info("TransMemoryCopyEvent event is sent. (" + targetMessage + ")");
				}
			});
			resultTable.setWidget(row, ACTION_COL, copyLink);
			
			// The MouseOverHandler is supposed to be added to the whole
			// row of resultTable. However, resultTable has to be 
			// modified to achieve that.
			copyLink.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
						VerticalPanel contentPanel = new VerticalPanel();
						contentPanel.add(new Label("Source Comment: " + sourceComment));
						contentPanel.add(new Label("Target Comment: " + targetComment));
						contentPanel.add(new Label("Document: " + docID));
						
						// setPopupPosition has to be done after the pop
						// -up panel is shown as to get panel height. The
						// position is 5 px above, center aligned of the
						// anchor.
						resultSuppPanel.setWidget(contentPanel);
						resultSuppPanel.show();
						resultSuppPanel.setPopupPosition(
								(copyLink.getAbsoluteLeft() + copyLink.getOffsetWidth()) / 2, 
								copyLink.getAbsoluteTop() - resultSuppPanel.getOffsetHeight() - 5);
				}
			});
		}
		resultTable.setCellPadding(CELL_PADDING);
	}
	
	private void addColumn(String columnHeading, int pos) {
	    Label widget = new Label(columnHeading);
	    widget.setWidth("100%");
	    widget.addStyleName("TransMemoryTableColumnHeader");
	    resultTable.setWidget(HEADER_ROW, pos, widget);
	  }
	
	@Override
	public void clearResults() {
		resultTable.removeAllRows();
	}
}

