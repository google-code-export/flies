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
package net.openl10n.flies.webtrans.client.editor.table;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface NavigationMessages extends Messages
{

   @DefaultMessage("Next Entry")
   String nextEntry();

   @DefaultMessage("Alt+Down")
   String nextEntryShortcut();

   @DefaultMessage("Prev Entry")
   String prevEntry();

   @DefaultMessage("Alt+Up")
   String prevEntryShortcut();

   @DefaultMessage("Next Fuzzy")
   String nextFuzzy();

   @DefaultMessage("Next Fuzzy or Untranslated")
   String nextFuzzyOrUntranslated();

   @DefaultMessage("Alt+PageDown")
   String nextFuzzyOrUntranslatedShortcut();

   @DefaultMessage("Prev Fuzzy or Untranslated")
   String prevFuzzyOrUntranslated();

   @DefaultMessage("Alt+PageUp")
   String prevFuzzyOrUntranslatedShortcut();

   @DefaultMessage("Save")
   String editSave();

   @DefaultMessage("Ctrl+Enter")
   String editSaveShortcut();

   @DefaultMessage("Cancel")
   String editCancel();

   @DefaultMessage("Esc")
   String editCancelShortcut();

   @DefaultMessage("Clone")
   String editClone();

   @DefaultMessage("Ctrl+Home")
   String editCloneShortcut();

   @DefaultMessage("Clone & Save")
   String editCloneAndSave();

   @DefaultMessage("Ctrl+End")
   String editCloneAndSaveShortcut();

   @DefaultMessage("{0} ({1})")
   String actionToolTip(String actionName, String shortcut);

   @DefaultMessage("Fuzzy")
   String fuzzy();

   @DefaultMessage("Source comment: ")
   String sourceCommentLabel();
}
