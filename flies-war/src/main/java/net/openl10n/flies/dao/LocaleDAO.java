package net.openl10n.flies.dao;

import java.util.List;

import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.model.HLocale;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("localeDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class LocaleDAO extends AbstractDAOImpl<HLocale, Long>
{

   public LocaleDAO()
   {
      super(HLocale.class);
   }

   public LocaleDAO(Session session)
   {
      super(HLocale.class, session);
   }

   public HLocale findByLocaleId(LocaleId locale)
   {
      return (HLocale) getSession().createCriteria(HLocale.class).add(Restrictions.naturalId().set("localeId", locale)).setCacheable(true).uniqueResult();
   }

   public List<HLocale> findAllActive()
   {
      return findByCriteria(Restrictions.eq("active", true));
   }
   
   @SuppressWarnings("unchecked")
   public List<HLocale> findDocumentLocale(HDocument document)
   {
	  return getSession().createQuery("select DISTINCT t.locale from HTextFlowTarget t where t.textFlow.document.docId =:document group by t.locale").setParameter("document", document.getDocId()).list();
   }

}
