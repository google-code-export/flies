package org.fedorahosted.flies.webtrans.shared.rpc;

import org.fedorahosted.flies.webtrans.shared.common.WorkspaceId;

public class ActivateWorkspaceAction implements DispatchAction<ActivateWorkspaceResult> {
	
	private static final long serialVersionUID = 1L;

	private WorkspaceId workspaceId;
	
	@SuppressWarnings("unused")
	private ActivateWorkspaceAction() {
	}

	public ActivateWorkspaceAction(WorkspaceId workspaceId) {
		this.workspaceId = workspaceId;
	}

	public WorkspaceId getWorkspaceId() {
		return workspaceId;
	}
}
