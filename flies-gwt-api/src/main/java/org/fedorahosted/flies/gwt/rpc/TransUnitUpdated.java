package org.fedorahosted.flies.gwt.rpc;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.gwt.model.DocumentId;
import org.fedorahosted.flies.gwt.model.TransUnitId;

public class TransUnitUpdated implements SessionEventData, HasTransUnitUpdatedData {

	private static final long serialVersionUID = 1L;

	private TransUnitId transUnitId;
	private DocumentId documentId;
	private ContentState previousStatus;
	private ContentState newStatus;
	
	@SuppressWarnings("unused")
	private TransUnitUpdated() {
	}
	
	public TransUnitUpdated(DocumentId documentId, TransUnitId transUnitId, ContentState previousStatus, ContentState newStatus) {
		this.documentId = documentId;
		this.transUnitId = transUnitId;
		this.previousStatus = previousStatus;
		this.newStatus = newStatus;
	}

	@Override
	public DocumentId getDocumentId() {
		return documentId;
	}
	
	@Override
	public ContentState getNewStatus() {
		return newStatus;
	}
	
	@Override
	public ContentState getPreviousStatus() {
		return previousStatus;
	}
	
	@Override
	public TransUnitId getTransUnitId() {
		return transUnitId;
	}
	
}
