package net.openl10n.flies.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.util.List;

import net.openl10n.flies.FliesDbunitJpaTest;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HDocumentHistory;
import net.openl10n.flies.model.HProjectIteration;
import net.openl10n.flies.service.LocaleService;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

public class HDocumentHistoryTest extends FliesDbunitJpaTest
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

      Date lastChanged = d.getLastChanged();

      d.incrementRevision();
      d.setContentType(ContentType.PO);
      session.update(d);
      session.flush();

      List<HDocumentHistory> historyElems = loadHistory(d);

      assertThat(historyElems.size(), is(1));
      HDocumentHistory history = historyElems.get(0);
      assertThat(history.getDocId(), is(d.getDocId()));
      assertThat(history.getContentType(), is(ContentType.TextPlain));
      assertThat(history.getLastChanged(), is(lastChanged));
      assertThat(history.getLastModifiedBy(), nullValue());
      assertThat(history.getLocale().getLocaleId(), is(LocaleId.EN));
      assertThat(history.getName(), is(d.getName()));
      assertThat(history.getPath(), is(d.getPath()));
      assertThat(history.getRevision(), is(d.getRevision() - 1));

      d.incrementRevision();
      d.setName("name2");
      session.update(d);
      session.flush();

      historyElems = loadHistory(d);
      assertThat(historyElems.size(), is(2));

   }

   @SuppressWarnings("unchecked")
   private List<HDocumentHistory> loadHistory(HDocument d)
   {
      return getSession().createCriteria(HDocumentHistory.class).add(Restrictions.eq("document", d)).list();
   }

}
