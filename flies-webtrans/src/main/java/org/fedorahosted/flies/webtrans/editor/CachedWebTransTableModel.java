package org.fedorahosted.flies.webtrans.editor;

import org.fedorahosted.flies.webtrans.model.TransUnit;

import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.inject.Inject;

public class CachedWebTransTableModel extends CachedTableModel<TransUnit>{
	
	@Inject
	public CachedWebTransTableModel(WebTransTableModel tableModel) {
		super(tableModel);
		setPreCachedRowCount(200);
		setPostCachedRowCount(200);
		setRowCount(1000);
	}

}
