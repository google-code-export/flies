package net.openl10n.flies.webtrans.shared.rpc;

import net.openl10n.flies.webtrans.shared.model.DocumentId;

public class GetStatusCount extends AbstractWorkspaceAction<GetStatusCountResult>
{

   private DocumentId documentId;

   @SuppressWarnings("unused")
   private GetStatusCount()
   {
   }

   public GetStatusCount(DocumentId id)
   {
      this.documentId = id;
   }

   public DocumentId getDocumentId()
   {
      return documentId;
   }

}
