package org.fedorahosted.flies.webtrans.server.rpc;

import org.fedorahosted.flies.webtrans.server.ActionHandlerFor;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

public abstract class AbstractActionHandler<A extends Action<R>, R extends Result> implements ActionHandler<A,R>{

	@SuppressWarnings("unchecked")
	@Override
	public final Class<A> getActionType() {
		return (Class<A>) this.getClass().getAnnotation(ActionHandlerFor.class).getClass();
	}
	
}
