package org.fedorahosted.flies.webtrans.client.rpc;

import java.util.ArrayList;

import org.fedorahosted.flies.webtrans.shared.model.TranslationMemoryItem;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemory;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemoryResult;
import org.fedorahosted.flies.webtrans.shared.rpc.GetTranslationMemory.SearchType;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DummyGetTranslationMemoryCommand implements Command {

	private final GetTranslationMemory action;
	private final AsyncCallback<GetTranslationMemoryResult> callback;

	public DummyGetTranslationMemoryCommand(GetTranslationMemory action,
			AsyncCallback<GetTranslationMemoryResult> callback) {
		this.action = action;
		this.callback = callback;
	}

	@Override
	public void execute() {
		String query = action.getQuery();
		SearchType type = action.getSearchType();
		ArrayList<TranslationMemoryItem> matches = new ArrayList<TranslationMemoryItem>();
		matches.add(new TranslationMemoryItem(type+"1", "target1", "sourceComment", "targetComment", new Long(1), 1, 100));
		matches.add(new TranslationMemoryItem(query, "target2", "sourceComment", "targetComment", new Long(2), 1, 90));
		matches.add(new TranslationMemoryItem("source3", "target3", "sourceComment", "targetComment", new Long(3), 1, 85));
		matches.add(new TranslationMemoryItem("<source4/>", "<target4/>", "sourceComment", "targetComment", new Long(4), 1, 60));
		callback.onSuccess(new GetTranslationMemoryResult(matches));
	}

}
