package org.fedorahosted.flies.rest.dto.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.common.Namespaces;
import org.fedorahosted.flies.common.ResourceType;
import org.fedorahosted.flies.rest.dto.ContentTypeAdapter;
import org.fedorahosted.flies.rest.dto.HasCollectionSample;
import org.fedorahosted.flies.rest.dto.LocaleIdAdapter;
import org.fedorahosted.flies.rest.dto.extensions.PoHeader;

@XmlType(name = "abstractResourceMetaType", namespace = Namespaces.FLIES, propOrder = { "name", "extensions" })
public abstract class AbstractResourceMeta implements Serializable
{

   private String name;

   private ContentType contentType = ContentType.TextPlain;

   private ResourceType type = ResourceType.FILE;

   private LocaleId lang = LocaleId.EN_US;

   private ExtensionSet extensions;

   public AbstractResourceMeta()
   {
   }

   public AbstractResourceMeta(String name)
   {
      this.name = name;
   }

   @XmlElementWrapper(name = "extensions", namespace = Namespaces.FLIES, required = false)
   @XmlAnyElement(lax = true)
   public ExtensionSet getExtensions()
   {
      return extensions;
   }

   public void setExtensions(ExtensionSet extensions)
   {
      this.extensions = extensions;
   }

   @JsonIgnore
   public ExtensionSet getExtensions(boolean createIfNull)
   {
      if (createIfNull && extensions == null)
         extensions = new ExtensionSet();
      return extensions;
   }

   @XmlAttribute(name = "type", required = true)
   public ResourceType getType()
   {
      return type;
   }

   public void setType(ResourceType type)
   {
      this.type = type;
   }

   @XmlJavaTypeAdapter(type = LocaleId.class, value = LocaleIdAdapter.class)
   @XmlAttribute(name = "lang", namespace = Namespaces.XML, required = true)
   public LocaleId getLang()
   {
      return lang;
   }

   public void setLang(LocaleId lang)
   {
      this.lang = lang;
   }

   @XmlJavaTypeAdapter(type = ContentType.class, value = ContentTypeAdapter.class)
   @XmlAttribute(name = "content-type", required = true)
   public ContentType getContentType()
   {
      return contentType;
   }

   public void setContentType(ContentType contentType)
   {
      this.contentType = contentType;
   }

   @XmlElement(name = "name", namespace = Namespaces.FLIES, required = true)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

}
