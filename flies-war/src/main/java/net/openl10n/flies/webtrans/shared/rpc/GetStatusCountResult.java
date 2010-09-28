package net.openl10n.flies.webtrans.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;
import net.openl10n.flies.common.TransUnitCount;
import net.openl10n.flies.webtrans.shared.model.DocumentId;


public class GetStatusCountResult implements Result
{

   private static final long serialVersionUID = 1L;

   private DocumentId documentId;
   private TransUnitCount count;

   @SuppressWarnings("unused")
   private GetStatusCountResult()
   {
   }

   public GetStatusCountResult(DocumentId documentId, TransUnitCount count)
   {
      this.documentId = documentId;
      this.count = count;
   }

   public DocumentId getDocumentId()
   {
      return documentId;
   }

   public TransUnitCount getCount()
   {
      return count;
   }

}
