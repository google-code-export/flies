package org.fedorahosted.flies.webtrans.client;

import org.fedorahosted.flies.gwt.model.DocName;
import org.fedorahosted.flies.gwt.model.DocumentId;
import org.fedorahosted.flies.webtrans.client.ui.FilterTree;
import org.fedorahosted.flies.webtrans.client.ui.HasFilter;
import org.fedorahosted.flies.webtrans.client.ui.HasTreeNodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DocumentListView extends Composite 
	implements DocumentListPresenter.Display {

	public interface Images extends ImageBundle, TreeImages {

		@Resource("org/fedorahosted/flies/webtrans/images/silk/folder.png")
		AbstractImagePrototype treeOpen();

		@Resource("org/fedorahosted/flies/webtrans/images/silk/folder_page_white.png")
		AbstractImagePrototype treeClosed();

		@Resource("org/fedorahosted/flies/webtrans/images/silk/page_white_text.png")
		AbstractImagePrototype treeLeaf();

	}

	private static Images images = (Images) GWT.create(Images.class);
	private FilterTree<DocumentId, DocName> tree;
	private FlowPanel mainpanel;
	private FlowPanel bottompanel;
	
	public DocumentListView() {
	    tree = new FilterTree<DocumentId, DocName>(new FlatFolderDocNameMapper(), images);
	    mainpanel = new FlowPanel();
	    mainpanel.setWidth("100%");
	    mainpanel.add(tree);
	    tree.setWidth("100%");
	    bottompanel = new FlowPanel();
	    bottompanel.setWidth("100%");
	    
	    mainpanel.add(bottompanel);

		RoundedContainerWithHeader container = new RoundedContainerWithHeader(new Label("Documents"), mainpanel);
		initWidget(container);
		getElement().setId("DocumentListView");
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
	public HasTreeNodes<DocumentId, DocName> getTree() {
		return tree;
	}

	@Override
	public HasFilter<DocName> getFilter() {
		return tree;
	}

	@Override
	public void setProjectStatusBar(Widget widget) {
		bottompanel.add(widget);		
		widget.addStyleName("CenterDiv");
	}

}
