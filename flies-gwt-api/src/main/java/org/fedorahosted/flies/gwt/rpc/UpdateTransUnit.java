package org.fedorahosted.flies.gwt.rpc;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.gwt.common.WorkspaceId;
import org.fedorahosted.flies.gwt.model.TransUnitId;

import com.google.gwt.user.client.rpc.IsSerializable;

import net.customware.gwt.dispatch.shared.Action;

public class UpdateTransUnit implements WorkspaceAction<UpdateTransUnitResult> {
	
	private static final long serialVersionUID = 1L;

	private TransUnitId transUnitId;
	private String content;
	private ContentState contentState;
	private WorkspaceId workspaceId;
	
	@SuppressWarnings("unused")
	private UpdateTransUnit() {
	}
	
	public UpdateTransUnit(WorkspaceId workspaceId, TransUnitId transUnitId, String content, ContentState contentState) {
		this.workspaceId = workspaceId;
		this.transUnitId = transUnitId;
		this.content = content;
		this.contentState = contentState;
	}
	
	public String getContent() {
		return content;
	}
	
	public TransUnitId getTransUnitId() {
		return transUnitId;
	}

	@Override
	public WorkspaceId getWorkspaceId() {
		return workspaceId;
	}

	public ContentState getContentState() {
		return contentState;
	}
}
