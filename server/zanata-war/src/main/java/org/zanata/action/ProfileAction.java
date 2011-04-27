/*
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.action;

import java.io.Serializable;


import org.hibernate.validator.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.zanata.ApplicationConfiguration;
import org.zanata.action.validator.NotDuplicateEmail;
import org.zanata.dao.PersonDAO;
import org.zanata.model.HAccount;
import org.zanata.model.HPerson;
import org.zanata.security.ZanataIdentity;
import org.zanata.security.ZanataJpaIdentityStore;
import org.zanata.service.RegisterService;
import org.zanata.service.impl.EmailChangeActivationService;

@Name("profileAction")
@Scope(ScopeType.PAGE)
public class ProfileAction implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private String name;
   private String email;
   private String username;
   private String activationKey;

   public String getActivationKey()
   {
      return activationKey;
   }

   public void setActivationKey(String keyHash)
   {
      this.activationKey = keyHash;
   }

   @In
   ApplicationConfiguration applicationConfiguration;
   @Logger
   Log log;

   @In
   ZanataIdentity identity;

   @In
   ZanataJpaIdentityStore identityStore;

   @In(create = true)
   private Renderer renderer;

   @In(required = false, value = JpaIdentityStore.AUTHENTICATED_USER)
   HAccount authenticatedAccount;

   @In
   PersonDAO personDAO;

   @In
   RegisterService registerServiceImpl;


   @Create
   public void onCreate()
   {
      username = identity.getCredentials().getUsername();
      if (identityStore.isNewUser(username))
      {
         name = identity.getCredentials().getUsername();
         String domain = applicationConfiguration.getDomainName();
         email = identity.getCredentials().getUsername() + "@" + domain;
         identity.unAuthenticate();
      }else{
         HPerson person = personDAO.findById(authenticatedAccount.getPerson().getId(), false);
         name = person.getName();
         email = person.getEmail();
         authenticatedAccount.getPerson().setName(this.name);
         authenticatedAccount.getPerson().setEmail(this.email);
      }
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Email
   @NotDuplicateEmail
   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   @Transactional
   public String edit()
   {
      if (personDAO.findByEmail(email) != null)
      {
         return null;
      }

      if (!identityStore.isNewUser(username))
      {
         HPerson person = personDAO.findById(authenticatedAccount.getPerson().getId(), true);
         person.setName(this.name);
         personDAO.makePersistent(person);
         personDAO.flush();
         authenticatedAccount.getPerson().setName(this.name);
         log.debug("updated successfully");
         if (!authenticatedAccount.getPerson().getEmail().equals(this.email))
         {
            activationKey = EmailChangeActivationService.generateActivationKey(authenticatedAccount.getPerson().getId().toString(), this.email);
            renderer.render("/WEB-INF/facelets/email/email_validation.xhtml");
            FacesMessages.instance().add("You will soon receive an email with a link to activate your email account change.");
         }

         return "updated";
      }
      else
      {
         final String user = this.username;
         String key = registerServiceImpl.register(user, "", this.name, this.email);
         setActivationKey(key);
         renderer.render("/WEB-INF/facelets/email/email_activation.xhtml");
         FacesMessages.instance().add("You will soon receive an email with a link to activate your account.");

         return "home";
      }
   }

   public String cancel(){
      if (identityStore.isNewUser(username))
      {
         return "home";
      }
      return "view";
   }


}
