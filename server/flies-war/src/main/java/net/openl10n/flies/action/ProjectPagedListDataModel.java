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
package net.openl10n.flies.action;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import net.openl10n.flies.dao.ProjectDAO;
import net.openl10n.flies.model.HProject;

public class ProjectPagedListDataModel extends PagedListDataModel<HProject>
{
   @Override
   public DataPage<HProject> fetchPage(int startRow, int pageSize)
   {
      ProjectDAO projectDAO = (ProjectDAO) Component.getInstance(ProjectDAO.class, ScopeType.STATELESS);
      List<HProject> proj = projectDAO.getOffsetListByCreateDate(startRow, pageSize);
      return new DataPage<HProject>(projectDAO.getProjectSize(), startRow, proj);
   }

}
