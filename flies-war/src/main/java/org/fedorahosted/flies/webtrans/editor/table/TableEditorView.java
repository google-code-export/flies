package org.fedorahosted.flies.webtrans.editor.table;

import java.util.List;

import org.fedorahosted.flies.gwt.model.TransUnit;
import org.fedorahosted.flies.webtrans.editor.HasPageNavigation;
import org.fedorahosted.flies.webtrans.editor.filter.ContentFilter;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.event.client.HasPageChangeHandlers;
import com.google.gwt.gen2.table.event.client.HasPageCountChangeHandlers;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TableEditorView extends PagingScrollTable<TransUnit> implements
		TableEditorPresenter.Display, HasSelectionHandlers<TransUnit>, HasPageNavigation {

	private final RedirectingCachedTableModel<TransUnit> cachedTableModel;
	private final TableEditorTableDefinition tableDefinition;
	private int cachedPages = 2;
	
	@Inject
	public TableEditorView(NavigationMessages messages) {
		this(messages, new RedirectingTableModel<TransUnit>());
	}
	
	public TableEditorView(NavigationMessages messages, RedirectingTableModel<TransUnit> tableModel) {
		this(new RedirectingCachedTableModel<TransUnit>(tableModel), 
		new TableEditorTableDefinition(messages, new RedirectingCachedTableModel<TransUnit>(tableModel)));
		
	}
	
	public TableEditorView(RedirectingCachedTableModel<TransUnit> tableModel, TableEditorTableDefinition tableDefinition) {
		super(tableModel,tableDefinition);
		this.cachedTableModel = tableModel;
		this.tableDefinition = tableDefinition;
		setStylePrimaryName("TableEditorWrapper");
		setSize("100%", "100%");
		setPageSize(10);
		setEmptyTableWidget(new HTML(
				"There is no data to display"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<TransUnit> bulkRenderer = new FixedWidthGridBulkRenderer<TransUnit>(
				getDataTable(), this);
		setBulkRenderer(bulkRenderer);

		// Setup the formatting
		setCellPadding(3);
		setCellSpacing(0);
		setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);

		getDataTable().setStylePrimaryName("TableEditor");
		getDataTable().setSelectionPolicy(SelectionPolicy.ONE_ROW);
		getDataTable().setCellPadding(3);
		getDataTable().addRowSelectionHandler(new RowSelectionHandler() {
			@Override
			public void onRowSelection(RowSelectionEvent event) {
				if(!event.getSelectedRows().isEmpty()){
					Row row = event.getSelectedRows().iterator().next();
					TransUnit tu = getRowValue(row.getRowIndex());
					SelectionEvent.fire(TableEditorView.this, tu);
				}
			}
		});
	}
	
	@Override
	public List<TransUnit> getRowValues() {
		return super.getRowValues();
	}
	
	@Override
	public InlineTargetCellEditor getTargetCellEditor() {
		return tableDefinition.getTargetCellEditor();
	}

	@Override
	public void setPageSize(int pageSize) {
		super.setPageSize(pageSize);
		cachedTableModel.setPostCachedRowCount(pageSize*cachedPages);
		cachedTableModel.setPreCachedRowCount(pageSize*cachedPages);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void startProcessing() {
		setVisible(false);
	}

	@Override
	public void stopProcessing() {
		setVisible(true);
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<TransUnit> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
	
	@Override
	public HasSelectionHandlers<TransUnit> getSelectionHandlers() {
		return this;
	}
	
	
	@Override
	public HasPageChangeHandlers getPageChangeHandlers() {
		return this;
	}

	@Override
	public HasPageCountChangeHandlers getPageCountChangeHandlers() {
		return this;
	}
	
	public boolean isFirstPage() {
		return getCurrentPage() == 0;
	}
	
	public boolean isLastPage() {
		return getCurrentPage() == getPageCount()-1;
	}
	
	public void setCachedPages(int cachedPages) {
		this.cachedPages = cachedPages;
	}
	
	public int getCachedPages() {
		return cachedPages;
	}

	@Override
	public RedirectingCachedTableModel<TransUnit> getTableModel() {
		return cachedTableModel;
	}
	
	@Override
	public void setTableModelHandler(TableModelHandler<TransUnit> handler) {
		cachedTableModel.getTableModel().setTableModelHandler(handler);
	}

	@Override
	public void clearContentFilter() {
		tableDefinition.clearContentFilter();
		reloadPage();
	}
	
	@Override
	public void setContentFilter(ContentFilter<TransUnit> filter) {
		tableDefinition.setContentFilter(filter);
		reloadPage();
	}

	@Override
	public void gotoRow(int row) {
		editCell(row, TableEditorTableDefinition.TARGET_COL);
	}
	
	@Override
	public int getCurrentPageNumber() {
		return this.getCurrentPage();
	}
	
	@Override
	public TransUnit getTransUnitValue(int row) {
		return this.getRowValue(row);
	}

}
