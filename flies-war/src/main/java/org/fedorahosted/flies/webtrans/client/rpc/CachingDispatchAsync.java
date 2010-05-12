package org.fedorahosted.flies.webtrans.client.rpc;

import org.fedorahosted.flies.webtrans.shared.auth.Identity;
import org.fedorahosted.flies.webtrans.shared.common.WorkspaceContext;

import net.customware.gwt.dispatch.client.DispatchAsync;

public interface CachingDispatchAsync extends DispatchAsync{

	void setWorkspaceContext(WorkspaceContext workspaceContext);

	void setIdentity(Identity identity);
	
}
