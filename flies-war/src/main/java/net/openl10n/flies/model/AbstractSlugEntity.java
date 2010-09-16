package net.openl10n.flies.model;

import javax.persistence.MappedSuperclass;

import net.openl10n.flies.model.validator.Slug;

import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@MappedSuperclass
public class AbstractSlugEntity extends AbstractFliesEntity
{
   private String slug;

   @NaturalId
   @Length(min = 1, max = 40)
   @Slug
   @NotNull
   @Field
   public String getSlug()
   {
      return slug;
   }

   public void setSlug(String slug)
   {
      this.slug = slug;
   }

   @Override
   public String toString()
   {
      return super.toString() + "[slug=" + slug + "]";
   }

}
