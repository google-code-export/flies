package net.openl10n.flies.webtrans.client.rpc;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.TransUnitCount;
import net.openl10n.flies.common.TransUnitWords;
import net.openl10n.flies.common.TranslationStats;
import net.openl10n.flies.webtrans.shared.rpc.GetStatusCount;
import net.openl10n.flies.webtrans.shared.rpc.GetStatusCountResult;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DummyGetStatusCountCommand implements Command
{

   private final GetStatusCount action;
   private final AsyncCallback<GetStatusCountResult> callback;

   public DummyGetStatusCountCommand(GetStatusCount action, AsyncCallback<GetStatusCountResult> callback)
   {
      this.action = action;
      this.callback = callback;
   }

   @Override
   public void execute()
   {
      TransUnitCount count = new TransUnitCount();
      count.set(ContentState.Approved, 34);
      count.set(ContentState.NeedReview, 23);
      count.set(ContentState.New, 43);
      TransUnitWords words = new TransUnitWords();
      words.set(ContentState.Approved, 70);
      words.set(ContentState.NeedReview, 40);
      words.set(ContentState.New, 90);
      TranslationStats stats = new TranslationStats(count, words);
      callback.onSuccess(new GetStatusCountResult(action.getDocumentId(), stats));
   }

}
