package org.fedorahosted.flies.webtrans.client.rpc;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import org.fedorahosted.flies.gwt.auth.AuthorizationError;
import org.fedorahosted.flies.gwt.auth.Identity;
import org.fedorahosted.flies.gwt.common.DispatchService;
import org.fedorahosted.flies.gwt.common.DispatchServiceAsync;
import org.fedorahosted.flies.gwt.common.WorkspaceContext;
import org.fedorahosted.flies.gwt.rpc.AbstractWorkspaceAction;
import org.fedorahosted.flies.webtrans.client.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;

public class SeamDispatchAsync implements CachingDispatchAsync {

	private static final DispatchServiceAsync realService;
	
	private WorkspaceContext workspaceContext;
	private Identity identity;
	
	static {
		realService = GWT.create(DispatchService.class);
		final String endpointURL = Application.FLIES_BASE_PATH + "seam/resource/gwt";

		((ServiceDefTarget) realService).setServiceEntryPoint(endpointURL);
	}
	
	@Inject
	public SeamDispatchAsync() {
	}
	
	public <A extends Action<R>, R extends Result> void execute(final A action,
			final AsyncCallback<R> callback) {

		if( action instanceof AbstractWorkspaceAction<?> ) {
			AbstractWorkspaceAction<?> wsAction = (AbstractWorkspaceAction<?>) action;
			if(workspaceContext == null || identity == null) {
				callback.onFailure( new AuthorizationError() );
				return;
			}
			wsAction.setSessionId(identity.getSessionId());
			wsAction.setWorkspaceId(workspaceContext.getWorkspaceId());
		}
		
		realService.execute(action, new AsyncCallback<Result>() {

			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@SuppressWarnings("unchecked")
			public void onSuccess(final Result result) {
				callback.onSuccess((R) result);
			}
		});
	}
	
	
	
}
