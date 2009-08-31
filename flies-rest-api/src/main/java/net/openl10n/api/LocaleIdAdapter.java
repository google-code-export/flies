package net.openl10n.api;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocaleIdAdapter extends XmlAdapter<String, LocaleId>
{
   public LocaleId unmarshal(String s) throws Exception
   {
      if (s == null) return null;
      return new LocaleId(s);
   }

   public String marshal(LocaleId localeId) throws Exception
   {
      if (localeId == null) return null;
      return localeId.toString();
   }
   
}