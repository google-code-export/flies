package org.fedorahosted.flies.webtrans.shared.rpc;

import org.fedorahosted.flies.webtrans.shared.model.PersonId;

public class ExitWorkspaceAction extends AbstractWorkspaceAction<ExitWorkspaceResult>
{

   private static final long serialVersionUID = 1L;

   private PersonId personId;

   @SuppressWarnings("unused")
   private ExitWorkspaceAction()
   {
   }

   public ExitWorkspaceAction(PersonId personId)
   {
      this.personId = personId;
   }

   public PersonId getPersonId()
   {
      return personId;
   }

}
