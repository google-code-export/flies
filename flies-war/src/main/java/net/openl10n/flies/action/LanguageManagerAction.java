package net.openl10n.flies.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.service.LocaleService;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ibm.icu.util.ULocale;

@Name("languageManagerAction")
@Scope(ScopeType.EVENT)
@Restrict("#{s:hasRole('admin')}")
public class LanguageManagerAction implements Serializable
{
   private static final long serialVersionUID = 1L;
   @In
   LocaleService localeServiceImpl;
   private String language;
   private ULocale uLocale;
   private List<SelectItem> localeStringList;
   @Logger
   private Log log;

   @Create
   public void onCreate()
   {
      fectchLocaleFromJava();
   }

   public String getLanguage()
   {
      return language;
   }

   public ULocale getuLocale()
   {
      return uLocale;
   }

   public void setuLocale(ULocale uLocale)
   {
      this.uLocale = uLocale;
   }

   public void setLanguage(String language)
   {
      this.language = language;
   }

   public void updateLanguage(String lan)
   {
      this.uLocale = new ULocale(lan);
   }

   public String save()
   {
      LocaleId locale = new LocaleId(language);
      localeServiceImpl.save(locale);
      return "success";
   }

   public void edit()
   {

   }
   public void delete(LocaleId selectedLanguage)
   {
      log.debug("delete selected language");
      localeServiceImpl.delete(selectedLanguage);
   }

   public void createSupportedLanguage()
   {
   }

   public void fectchLocaleFromJava()
   {
      List<LocaleId> locale = localeServiceImpl.getAllJavaLanguages();
      List<SelectItem> localeList = new ArrayList<SelectItem>();
      for (LocaleId var : locale)
      {
         SelectItem op = new SelectItem(var.getId(), var.getId());
         localeList.add(op);
      }
      localeStringList = localeList;
   }

   public List<SelectItem> getLocaleStringList()
   {
      return localeStringList;
   }

}
