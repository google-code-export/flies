package org.fedorahosted.flies.maven;

import org.fedorahosted.flies.client.commands.RetrieveCommand;

/**
 * Retrieves translated text from a Flies project version.
 * 
 * @goal retrieve
 * @requiresProject false
 * @author Sean Flanigan <sflaniga@redhat.com>
 */
public class RetrieveMojo extends ConfigurableProjectMojo<RetrieveCommand>
{

   public RetrieveMojo() throws Exception
   {
      super(new RetrieveCommand());
   }

}
