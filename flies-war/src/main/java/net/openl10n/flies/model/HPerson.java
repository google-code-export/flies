/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package net.openl10n.flies.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.openl10n.flies.rest.dto.Person;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

/**
 * @see Person
 * 
 */
@NamedQueries({ @NamedQuery(name = "getLanguageMemberships", query = "select p.tribeMemberships from HPerson as p where p.account.username = :username"), @NamedQuery(name = "getMaintainerProjects", query = "select p.maintainerProjects from HPerson as p where p.account.username = :username") })
@Entity
public class HPerson extends AbstractFliesEntity implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String name;
   private HAccount account;

   private String email;

   private List<HProject> maintainerProjects;

   private Set<HLocale> tribeMemberships;


   @NotEmpty
   @Length(min = 2, max = 80)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @OneToOne(optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "accountId")
   public HAccount getAccount()
   {
      return account;
   }

   public void setAccount(HAccount account)
   {
      this.account = account;
   }

   @Transient
   public boolean hasAccount()
   {
      return account != null;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   @Email
   @NotEmpty
   @NaturalId(mutable = true)
   public String getEmail()
   {
      return email;
   }

   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(name = "HProject_Maintainer", joinColumns = @JoinColumn(name = "personId"), inverseJoinColumns = @JoinColumn(name = "projectId"))
   public List<HProject> getMaintainerProjects()
   {
      return maintainerProjects;
   }

   public void setMaintainerProjects(List<HProject> maintainerProjects)
   {
      this.maintainerProjects = maintainerProjects;
   }

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "HLocale_Member", joinColumns = @JoinColumn(name = "personId"), inverseJoinColumns = @JoinColumn(name = "supportedLanguageId"))
   public Set<HLocale> getTribeMemberships()
   {
      if (tribeMemberships == null)
      {
         tribeMemberships = new HashSet<HLocale>();
         setTribeMemberships(tribeMemberships);
      }
      return tribeMemberships;
   }

   public void setTribeMemberships(Set<HLocale> tribeMemberships)
   {
      this.tribeMemberships = tribeMemberships;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((email == null) ? 0 : email.hashCode());
      result = prime * result + ((maintainerProjects == null) ? 0 : maintainerProjects.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      HPerson other = (HPerson) obj;
      if (account == null)
      {
         if (other.account != null)
            return false;
      }
      else if (!account.equals(other.account))
         return false;
      if (email == null)
      {
         if (other.email != null)
            return false;
      }
      else if (!email.equals(other.email))
         return false;
      if (maintainerProjects == null)
      {
         if (other.maintainerProjects != null)
            return false;
      }
      else if (!maintainerProjects.equals(other.maintainerProjects))
         return false;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return super.toString() + "[name=" + name + "]";
   }

   @Transient
   public boolean isMaintainer(HProject proj)
   {
      // TODO consider implementing business key equality and using
      // getMaintainerProjects().contains(proj)
      if (getMaintainerProjects() != null)
      {
         for (HProject maintProj : getMaintainerProjects())
         {
            if (maintProj.getId().equals(proj.getId()))
               return true;
         }
      }
      return false;
   }

}
