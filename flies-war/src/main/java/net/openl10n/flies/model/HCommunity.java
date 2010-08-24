package net.openl10n.flies.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.security.Restrict;

/**
 * A community represents people from different tribes coming together around a
 * common cause, e.g. to translate a set of projects.
 * 
 * A community has goals/targets, priorities
 * 
 * @author asgeirf
 * 
 */
@Entity
@Indexed
@Restrict
public class HCommunity extends AbstractSlugEntity
{

   private String name;

   private String description;
   private String homeContent;

   private HPerson owner;
   private Set<HPerson> officers;
   private Set<HPerson> members;

   @NotEmpty
   @Field(index = Index.TOKENIZED)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Length(max = 100)
   @Field(index = Index.TOKENIZED)
   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   @Type(type = "text")
   public String getHomeContent()
   {
      return homeContent;
   }

   public void setHomeContent(String homeContent)
   {
      this.homeContent = homeContent;
   }

   /*
    * private Set<Community> parentCommunities; private Set<Community>
    * childCommunities;
    * 
    * private Set<Project> associatedProjects;
    */

   @NotNull
   @ManyToOne(optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "ownerId")
   public HPerson getOwner()
   {
      return owner;
   }

   public void setOwner(HPerson owner)
   {
      this.owner = owner;
   }

   @ManyToMany
   @JoinTable(name = "HCommunity_Officer", joinColumns = @JoinColumn(name = "communityId"), inverseJoinColumns = @JoinColumn(name = "personId"))
   public Set<HPerson> getOfficers()
   {
      return officers;
   }

   public void setOfficers(Set<HPerson> officers)
   {
      this.officers = officers;
   }

   @ManyToMany
   @JoinTable(name = "HCommunity_Member", joinColumns = @JoinColumn(name = "communityId"), inverseJoinColumns = @JoinColumn(name = "personId"))
   public Set<HPerson> getMembers()
   {
      return members;
   }

   public void setMembers(Set<HPerson> members)
   {
      this.members = members;
   }

   /*
    * public Set<Community> getParentCommunities() { return parentCommunities; }
    * 
    * public void setParentCommunities(Set<Community> parentCommunities) {
    * this.parentCommunities = parentCommunities; }
    * 
    * public Set<Community> getChildCommunities() { return childCommunities; }
    * 
    * public void setChildCommunities(Set<Community> childCommunities) {
    * this.childCommunities = childCommunities; }
    * 
    * public Set<Project> getAssociatedProjects() { return associatedProjects; }
    * 
    * public void setAssociatedProjects(Set<Project> associatedProjects) {
    * this.associatedProjects = associatedProjects; }
    */

   @Override
   public String toString()
   {
      return super.toString() + "[name=" + name + "]";
   }

}
