package org.zanata.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;


import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.ZanataDbunitJpaTest;
import org.zanata.common.ContentType;
import org.zanata.common.LocaleId;
import org.zanata.dao.LocaleDAO;
import org.zanata.model.HDocument;
import org.zanata.model.HLocale;
import org.zanata.model.HProjectIteration;
import org.zanata.model.HTextFlow;
import org.zanata.model.HTextFlowHistory;

public class HTextFlowHistoryTest extends ZanataDbunitJpaTest
{
   private LocaleDAO localeDAO;
   HLocale en_US;

   @BeforeMethod(firstTimeOnly = true)
   public void beforeMethod()
   {
      localeDAO = new LocaleDAO((Session) em.getDelegate());
      en_US = localeDAO.findByLocaleId(LocaleId.EN_US);
   }

   @Override
   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("META-INF/testdata/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation("META-INF/testdata/LocalesData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }

   // FIXME this test only works if resources-dev is on the classpath
   @Test
   public void ensureHistoryIsRecorded()
   {
      Session session = getSession();
      HDocument d = new HDocument("/path/to/document.txt", ContentType.TextPlain, en_US);
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
