package net.openl10n.flies.webtrans.client.events;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.webtrans.shared.model.DocumentId;
import net.openl10n.flies.webtrans.shared.model.TransUnitId;
import net.openl10n.flies.webtrans.shared.rpc.HasTransUnitUpdatedData;

import com.google.gwt.event.shared.GwtEvent;

public class TransUnitUpdatedEvent extends GwtEvent<TransUnitUpdatedEventHandler> implements HasTransUnitUpdatedData
{

   private final TransUnitId transUnitId;
   private final DocumentId documentId;
   private final ContentState previousStatus;
   private final ContentState newStatus;
   private int wordCount;

   /**
    * Handler type.
    */
   private static Type<TransUnitUpdatedEventHandler> TYPE;

   /**
    * Gets the type associated with this event.
    * 
    * @return returns the handler type
    */
   public static Type<TransUnitUpdatedEventHandler> getType()
   {
      if (TYPE == null)
      {
         TYPE = new Type<TransUnitUpdatedEventHandler>();
      }
      return TYPE;
   }

   public TransUnitUpdatedEvent(HasTransUnitUpdatedData data)
   {
      this.documentId = data.getDocumentId();
      this.newStatus = data.getNewStatus();
      this.previousStatus = data.getPreviousStatus();
      this.transUnitId = data.getTransUnitId();
      this.wordCount = data.getWordCount();
   }

   @Override
   protected void dispatch(TransUnitUpdatedEventHandler handler)
   {
      handler.onTransUnitUpdated(this);
   }

   @Override
   public Type<TransUnitUpdatedEventHandler> getAssociatedType()
   {
      return getType();
   }

   @Override
   public DocumentId getDocumentId()
   {
      return documentId;
   }

   @Override
   public ContentState getNewStatus()
   {
      return newStatus;
   };

   @Override
   public ContentState getPreviousStatus()
   {
      return previousStatus;
   }

   @Override
   public TransUnitId getTransUnitId()
   {
      return transUnitId;
   }

   @Override
   public int getWordCount()
   {
      return wordCount;
   }

}
