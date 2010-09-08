package net.openl10n.flies.webtrans.client.rpc;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface RpcMessages extends Messages
{

   @DefaultMessage("Dispatcher not set up to delegate WorkspaceContext and Identity.")
   String dispatcherSetupFailed();

}
