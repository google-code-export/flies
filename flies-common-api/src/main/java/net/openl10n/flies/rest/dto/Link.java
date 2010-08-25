package net.openl10n.flies.rest.dto;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import net.openl10n.flies.common.Namespaces;

@XmlType(name = "linkType", namespace = Namespaces.FLIES)
@XmlRootElement(name = "link", namespace = Namespaces.FLIES)
public class Link
{

   private URI href;
   private String rel;
   private String type;

   protected Link()
   {
   }

   public Link(URI href)
   {
      this.href = href;
   }

   public Link(URI href, String rel)
   {
      this.href = href;
      this.rel = rel;
   }

   public Link(URI href, String rel, String type)
   {
      this.href = href;
      this.rel = rel;
      this.type = type;
   }

   @XmlAttribute(name = "href", required = true)
   public URI getHref()
   {
      return href;
   }

   public void setHref(URI href)
   {
      this.href = href;
   }

   @XmlAttribute(name = "rel", required = false)
   public String getRel()
   {
      return rel;
   }

   public void setRel(String rel)
   {
      this.rel = rel;
   }

   @XmlAttribute(name = "type", required = true)
   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public String toString()
   {
      return DTOUtil.toXML(this);
   }

}
