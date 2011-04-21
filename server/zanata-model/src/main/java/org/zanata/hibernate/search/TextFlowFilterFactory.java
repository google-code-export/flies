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
package org.zanata.hibernate.search;

import java.util.List;

import org.apache.lucene.search.Filter;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class TextFlowFilterFactory
{

   @Factory
   public Filter getFilter()
   {
      TextFlowIdFilter filter = new TextFlowIdFilter();
      filter.setTextFlowIds(this.translatedIds);
      return filter;
   }

   private List<Long> translatedIds;

   public List<Long> getTranslatedIds()
   {
      return translatedIds;
   }

   public void setTranslatedIds(List<Long> var)
   {
      this.translatedIds = var;
   }

   @Key
   public FilterKey getKey()
   {
      StandardFilterKey key = new StandardFilterKey();
      key.addParameter(translatedIds);
      return key;
   }

}
