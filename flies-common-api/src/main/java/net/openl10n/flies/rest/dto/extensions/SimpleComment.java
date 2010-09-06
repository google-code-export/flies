package net.openl10n.flies.rest.dto.extensions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import net.openl10n.flies.common.Namespaces;
import net.openl10n.flies.rest.dto.DTOUtil;
import net.openl10n.flies.rest.dto.resource.Extension;

import org.codehaus.jackson.annotate.JsonTypeName;

@XmlType(name = "simpleCommentExtension", namespace = PoHeader.NAMESPACE, propOrder = {})
@XmlRootElement(name = "comment", namespace = PoHeader.NAMESPACE)
@JsonTypeName(value = SimpleComment.ID)
public class SimpleComment extends Extension
{

   public static final String ID = "comment";
   public static final String VERSION = "1.0";
   public static final String NAMESPACE = Namespaces.FLIES;

   private String value;

   public SimpleComment()
   {
      super(ID, VERSION);
   }

   public SimpleComment(String value)
   {
      this.value = value;
   }

   @XmlElement(name = "value", required = true)
   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   @XmlAttribute(name = "space", namespace = Namespaces.XML)
   public String getSpace()
   {
      return "preserve";
   }

   public void setSpace(String space)
   {
   }

}
