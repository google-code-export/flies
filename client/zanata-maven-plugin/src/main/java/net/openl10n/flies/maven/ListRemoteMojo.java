package net.openl10n.flies.maven;

import net.openl10n.flies.client.commands.ListRemoteCommand;

/**
 * Lists all remote documents in the configured Zanata project version.
 * 
 * @goal listremote
 * @requiresProject false
 * @author Sean Flanigan <sflaniga@redhat.com>
 */
public class ListRemoteMojo extends ConfigurableProjectMojo
{

   public ListRemoteMojo() throws Exception
   {
      super();
   }

   public ListRemoteCommand initCommand()
   {
      return new ListRemoteCommand(this);
   }

}
