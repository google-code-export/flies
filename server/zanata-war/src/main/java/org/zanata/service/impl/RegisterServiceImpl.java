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
package org.zanata.service.impl;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityStore;
import org.zanata.dao.AccountActivationKeyDAO;
import org.zanata.dao.AccountDAO;
import org.zanata.dao.PersonDAO;
import org.zanata.model.HAccount;
import org.zanata.model.HAccountActivationKey;
import org.zanata.model.HPerson;
import org.zanata.service.RegisterService;
import org.zanata.util.HashUtil;

@Name("registerServiceImpl")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class RegisterServiceImpl implements RegisterService
{
   @In
   IdentityStore identityStore;
   
   @In
   AccountDAO accountDAO;
   
   @In
   PersonDAO personDAO;
   
   @In
   AccountActivationKeyDAO accountActivationKeyDAO;

   public String register(final String username, final String password, String name, String email)
   {
      new RunAsOperation()
      {
         public void execute()
         {
            identityStore.createUser(username, password);
            identityStore.disableUser(username);
         }
      }.addRole("admin").run();

      HAccount account = accountDAO.getByUsername(username);
      HPerson person = new HPerson();
      person.setAccount(account);
      person.setEmail(email);
      person.setName(name);
      personDAO.makePersistent(person);

      HAccountActivationKey key = new HAccountActivationKey();
      key.setAccount(account);
      key.setKeyHash(HashUtil.generateHash(username + password + email + name + System.currentTimeMillis()));
      accountActivationKeyDAO.makePersistent(key);
      accountActivationKeyDAO.flush();
      return key.getKeyHash();
   }
}
