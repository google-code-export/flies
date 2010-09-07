package net.openl10n.flies.dao;

import java.util.List;

import net.openl10n.flies.common.ContentState;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HLocale;
import net.openl10n.flies.model.HTextFlow;
import net.openl10n.flies.model.HTextFlowTarget;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("textFlowTargetDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class TextFlowTargetDAO extends AbstractDAOImpl<HTextFlowTarget, Long>
{

   public TextFlowTargetDAO()
   {
      super(HTextFlowTarget.class);
   }

   public TextFlowTargetDAO(Session session)
   {
      super(HTextFlowTarget.class, session);
   }

   /**
    * @param textFlow
    * @param localeId
    * @return
    */
   public HTextFlowTarget getByNaturalId(HTextFlow textFlow, HLocale locale)
   {
      return (HTextFlowTarget) getSession().createCriteria(HTextFlowTarget.class).add(Restrictions.naturalId().set("textFlow", textFlow).set("locale", locale)).setCacheable(true).setComment("TextFlowTargetDAO.getByNaturalId").uniqueResult();
   }

   @SuppressWarnings("unchecked")
   public List<HTextFlowTarget> findAllTranslations(HDocument document, LocaleId locale)
   {
      return getSession().createQuery("select t from HTextFlowTarget t where " + "t.textFlow.document =:document and t.locale.localeId =:locale " + "and t.state !=:state " + "order by t.textFlow.pos").setParameter("document", document).setParameter("locale", locale).setParameter("state", ContentState.New).list();

   }

}
