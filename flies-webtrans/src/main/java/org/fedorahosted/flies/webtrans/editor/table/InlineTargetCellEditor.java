package org.fedorahosted.flies.webtrans.editor.table;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.gwt.model.TransUnit;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.gen2.table.client.CellEditor;
import com.google.gwt.gen2.table.client.InlineCellEditor.InlineCellEditorImages;
import com.google.gwt.gen2.table.override.client.HTMLTable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class InlineTargetCellEditor implements CellEditor<TransUnit> {

	/**
	 * An {@link ImageBundle} that provides images for {@link InlineTargetCellEditor}.
	 */
	public static interface TargetCellEditorImages extends
			InlineCellEditorImages {

	}

	/**
	 * Default style name.
	 */
	public static final String DEFAULT_STYLENAME = "gwt-TargetCellEditor";

	/**
	 * The click listener used to accept.
	 */
	private ClickHandler cancelHandler = new ClickHandler() {
		public void onClick(ClickEvent event) {
			cancel();
		}
	};

	private final ToggleButton toggleFuzzy;
	
	/**
	 * The click listener used to accept.
	 */
	private ClickHandler acceptHandler = new ClickHandler() {
		public void onClick(ClickEvent event) {
			accept();
		}
	};
	
	/**
	 * The current {@link CellEditor.Callback}.
	 */
	private Callback<TransUnit> curCallback = null;

	/**
	 * The current {@link CellEditor.CellEditInfo}.
	 */
	private CellEditInfo curCellEditInfo = null;

	/**
	 * The main grid used for layout.
	 */
	private FlowPanel layoutTable;

	private Widget cellViewWidget;

	private TransUnit cellValue;
	
	private final TextArea textArea;
	/**
	 * Construct a new {@link InlineTargetCellEditor}.
	 * 
	 * @param content
	 *            the {@link Widget} used to edit
	 */
	public InlineTargetCellEditor() {
		this(GWT.<TargetCellEditorImages> create(TargetCellEditorImages.class));
	}

	/**
	 * Construct a new {@link InlineTargetCellEditor} with the specified images.
	 * 
	 * @param content
	 *            the {@link Widget} used to edit
	 * @param images
	 *            the images to use for the accept/cancel buttons
	 */
	public InlineTargetCellEditor(TargetCellEditorImages images) {

		// Wrap contents in a table
		layoutTable = new FlowPanel();

		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setStyleName("TableEditorContent-Edit");
		textArea.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					accept();
				}
				else if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					cancel();
				}
			}
		});
		layoutTable.add(textArea);
		
		HorizontalPanel operationsPanel = new HorizontalPanel();
		operationsPanel.addStyleName("float-right-div");
		operationsPanel.setSpacing(4);
		layoutTable.add(operationsPanel);
		
		// Add content widget
		toggleFuzzy = new ToggleButton("Approved", "Fuzzy");
		operationsPanel.add(toggleFuzzy);
		

		PushButton cancelButton = new PushButton(images.cellEditorCancel().createImage(),cancelHandler);
		//cancelButton.setText("Cancel");
		operationsPanel.add(cancelButton);

		PushButton acceptButton = new PushButton(images.cellEditorAccept().createImage(),acceptHandler);
		//acceptButton.setText("Accept");
		operationsPanel.add(acceptButton);
		
	}

	int row;
	int col;

	private void restoreView() {
		if(curCellEditInfo != null && cellViewWidget != null) {
			curCellEditInfo.getTable().setWidget(row, col, cellViewWidget);
			cellViewWidget.getParent().setHeight(cellViewWidget.getOffsetHeight()+"px");
		}
	}
	
	private boolean isDirty(){
		if(cellValue == null) return false;
		return !textArea.getText().equals(cellValue.getTarget()) ;
	}
	
	private boolean inEditMode() {
		return cellValue != null;
	}
	
	public void editCell(CellEditInfo cellEditInfo, TransUnit cellValue,
			Callback<TransUnit> callback) {

		// don't allow edits of two cells at once
		if( isDirty() ) {
	    	callback.onCancel(cellEditInfo);
	    	return;
	    }
		
		if( inEditMode() ){
			if(cellEditInfo.getCellIndex() == col && cellEditInfo.getRowIndex() == row){
				return;
			}
			restoreView();
		}
		
		Log.debug("starting edit of cell");
		
		// Save the current values
		curCallback = callback;
		curCellEditInfo = cellEditInfo;

		// Get the info about the cell
		HTMLTable table = curCellEditInfo.getTable();

		row = curCellEditInfo.getRowIndex();
		col = curCellEditInfo.getCellIndex();

		cellViewWidget = table.getWidget(row, col);
		textArea.setHeight( table.getWidget(row, col-1).getOffsetHeight() + "px");
		table.setWidget(row, col, layoutTable);

		textArea.setText(cellValue.getTarget());
		this.cellValue = cellValue;
		textArea.setFocus(true);
		toggleFuzzy.setDown(cellValue.getStatus() == ContentState.NeedReview);
		
	}

	/**
	 * Accept the contents of the cell editor as the new cell value.
	 */
	protected void accept() {
		// Check if we are ready to accept
		if (!onAccept()) {
			return;
		}
		cellValue.setTarget(textArea.getText());
		cellValue.setStatus(toggleFuzzy.isDown() ? ContentState.NeedReview : ContentState.Approved );
		restoreView();
		
		// Send the new cell value to the callback
		curCallback.onComplete(curCellEditInfo, cellValue);
		
		clearSelection();
	}
	
	private void clearSelection() {
		curCallback = null;
		curCellEditInfo = null;
		cellViewWidget = null;
		cellValue = null;
	}

	/**
	 * Cancel the cell edit.
	 */
	protected void cancel() {
		// Fire the event
		if (!onCancel()) {
			return;
		}

		restoreView();
		
		// Call the callback
		if (curCallback != null) {
			curCallback.onCancel(curCellEditInfo);
		}

		clearSelection();
	}

	/**
	 * Called before an accept takes place.
	 * 
	 * @return true to allow the accept, false to prevent it
	 */
	protected boolean onAccept() {
		return true;
	}

	/**
	 * Called before a cancel takes place.
	 * 
	 * @return true to allow the cancel, false to prevent it
	 */
	protected boolean onCancel() {
		return true;
	}

}
