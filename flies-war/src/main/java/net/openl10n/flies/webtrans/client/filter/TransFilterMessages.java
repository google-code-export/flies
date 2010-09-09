package net.openl10n.flies.webtrans.client.filter;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface TransFilterMessages extends Messages
{
   @DefaultMessage("Find Messages")
   String transUnitSearchesHeading();

   @DefaultMessage("Source or Target content")
   String findSourceOrTargetString();
}
