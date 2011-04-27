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
package net.openl10n.flies.action.validator;

import java.io.Serializable;

import org.hibernate.validator.Validator;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import net.openl10n.flies.dao.PersonDAO;

public class DuplicateEmailValidator implements Validator<NotDuplicateEmail>, Serializable
{
   private static final long serialVersionUID = 1L;

   @Override
   public boolean isValid(Object value)
   {
      if (value == null)
         return true;
      if (!(value instanceof String))
         return false;
      String string = (String) value;
      if (string.length() == 0)
         return true;
      PersonDAO personDAO = (PersonDAO) Component.getInstance(PersonDAO.class, ScopeType.STATELESS);
      return personDAO.findByEmail(string) == null;
   }


   @Override
   public void initialize(NotDuplicateEmail parameters)
   {
   }

}
