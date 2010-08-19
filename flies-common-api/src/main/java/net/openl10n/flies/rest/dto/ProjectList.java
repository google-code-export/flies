package net.openl10n.flies.rest.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.openl10n.flies.common.Namespaces;

import org.codehaus.jackson.annotate.JsonValue;

/**
 * 
 * This class is only used for generating the schema.
 * 
 * @author asgeirf
 * 
 */
@XmlType(name = "projectListType", namespace = Namespaces.FLIES, propOrder = { "projects" })
@XmlRootElement(name = "projects", namespace = Namespaces.FLIES)
public class ProjectList implements Serializable, HasSample<ProjectList>
{

   private List<Project> projects;

   @XmlElementRef
   @JsonValue
   public List<Project> getProjects()
   {
      if (projects == null)
      {
         projects = new ArrayList<Project>();
      }
      return projects;
   }

   @Override
   public ProjectList createSample()
   {
      ProjectList entity = new ProjectList();
      entity.getProjects().addAll(new Project().createSamples());
      return entity;
   }

   @Override
   public String toString()
   {
      return DTOUtil.toXML(this);
   }

}
