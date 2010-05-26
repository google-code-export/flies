package org.fedorahosted.flies.webtrans.client.editor.table;

import org.fedorahosted.flies.webtrans.shared.model.DocumentId;

public interface HasDocumentId {
	public void setDocumentId(DocumentId documentId);
	public DocumentId getDocumentId();
}
