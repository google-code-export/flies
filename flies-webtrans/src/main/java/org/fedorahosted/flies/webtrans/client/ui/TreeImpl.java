package org.fedorahosted.flies.webtrans.client.ui;

import org.fedorahosted.flies.gwt.model.DocName;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeImpl<T> extends Tree implements HasTreeNodes<T> {
	
	public TreeImpl() {
	}

	public TreeImpl(TreeImages images) {
		super(images);
	}

	@Override
	public TreeNodeImpl<T> addItem(String itemText) {
		TreeNodeImpl<T> treeNodeImpl = new TreeNodeImpl<T>(itemText);
		addItem(treeNodeImpl);
		return treeNodeImpl;
	}
	
	@Override
	public TreeNodeImpl<T> getSelectedNode() {
		TreeItem item = super.getSelectedItem();
		if (item instanceof TreeNodeImpl<?>)
			return (TreeNodeImpl<T>) item;
		else
			return null;
	}
	
	@Override
	public void setSelectedNode(TreeNode<DocName> node) {
		setSelectedItem((TreeItem) node, false);
	}

}
