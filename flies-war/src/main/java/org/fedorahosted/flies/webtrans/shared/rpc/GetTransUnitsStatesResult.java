package org.fedorahosted.flies.webtrans.shared.rpc;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import org.fedorahosted.flies.webtrans.shared.model.DocumentId;
import org.fedorahosted.flies.webtrans.shared.model.TransUnitId;

public class GetTransUnitsStatesResult implements Result {

	private static final long serialVersionUID = 1L;

	private DocumentId documentId;
	private List<Long> units;

	@SuppressWarnings("unused")
	private GetTransUnitsStatesResult()	{
	}
	
	public GetTransUnitsStatesResult(DocumentId documentId, List<Long> units) {
		this.documentId = documentId;
		this.units = units;
	}
	
	public List<Long> getUnits() {
		return units;
	}

	public DocumentId getDocumentId() {
		return documentId;
	}
}
