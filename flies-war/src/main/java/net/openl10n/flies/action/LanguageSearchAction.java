package net.openl10n.flies.action;

import java.io.Serializable;
import java.util.List;

import net.openl10n.flies.model.FliesLocalePair;
import net.openl10n.flies.service.LocaleService;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;


@Name("languageSearchAction")
@Scope(ScopeType.PAGE)
@Restrict("#{s:hasRole('admin')}")
public class LanguageSearchAction implements Serializable
{
   private static final long serialVersionUID = 1L;
   @In
   LocaleService localeServiceImpl;
   @DataModel
   List<FliesLocalePair> supportedLanguages;
   @DataModelSelection
   FliesLocalePair selectedLanguage;

   public void loadSupportedLanguage()
   {
      supportedLanguages = localeServiceImpl.getAllLocales();
   }

   public FliesLocalePair getSelectedLanguage()
   {
      return selectedLanguage;
   }

}
