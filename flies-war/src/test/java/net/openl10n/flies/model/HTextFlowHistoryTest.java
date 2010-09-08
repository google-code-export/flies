package net.openl10n.flies.model;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import net.openl10n.flies.FliesDbunitJpaTest;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HProjectIteration;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.model.HTextFlowHistory;
import net.openl10n.flies.service.LocaleService;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

public class HTextFlowHistoryTest extends FliesDbunitJpaTest
{
   private LocaleService localeServiceImpl;

   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("META-INF/testdata/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }

   @Test
   public void ensureHistoryIsRecorded()
   {
      Session session = getSession();
      HDocument d = new HDocument("/path/to/document.txt", ContentType.TextPlain, localeServiceImpl.getDefautLanguage());
      d.setProjectIteration((HProjectIteration) session.load(HProjectIteration.class, 1L));
      session.save(d);
      session.flush();

      HTextFlow tf = new HTextFlow(d, "mytf", "hello world");
      d.getTextFlows().add(tf);

      session.flush();

      List<HTextFlowHistory> historyElems = getHistory(tf);

      assertThat(historyElems.size(), is(0));

      d.incrementRevision();
      tf.setContent("hello world again");
      tf.setRevision(d.getRevision());
      session.flush();

      historyElems = getHistory(tf);

      assertThat(historyElems.size(), is(1));

   }

   @SuppressWarnings("unchecked")
   private List<HTextFlowHistory> getHistory(HTextFlow tf)
   {
      return getSession().createCriteria(HTextFlowHistory.class).add(Restrictions.eq("textFlow", tf)).list();

   }
}
