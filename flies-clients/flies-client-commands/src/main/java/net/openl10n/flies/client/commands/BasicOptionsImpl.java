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
package net.openl10n.flies.client.commands;

import org.kohsuke.args4j.Option;

/**
 * Base class for Flies commands which supports configuration by the user's
 * flies.ini
 * 
 * @author Sean Flanigan <sflaniga@redhat.com>
 * 
 */
public abstract class BasicOptionsImpl implements BasicOptions
{

   /**
    * Whether to enable debug mode. Defaults to the value in flies.ini. This
    * value is used by command line clients, but not by Maven (which uses its
    * own --debug/-X flag).
    */
   private boolean debug;
   private boolean debugSet;

   /**
    * Whether to display full information about errors (ie exception stack
    * traces). Defaults to the value in flies.ini. This value is used by command
    * line clients, but not by Maven (which uses its own --errors/-e flag).
    */
   private boolean errors;
   private boolean errorsSet;

   /**
    * Whether to display the command's usage help. Maven uses the auto-generated
    * HelpMojo instead.
    */
   private boolean help;

   /**
    * Enable quiet mode - error messages only
    */
   private boolean quiet;
   private boolean quietSet;

   public BasicOptionsImpl()
   {
   }

   @Override
   public boolean getDebug()
   {
      return debug;
   }

   @Override
   @Option(name = "--debug", aliases = { "-X" }, usage = "Enable debug logging")
   public void setDebug(boolean debug)
   {
      debugSet = true;
      this.debug = debug;
      if (debug)
      {
         setErrors(true);
      }
   }

   @Override
   public boolean getErrors()
   {
      return errors;
   }

   @Override
   @Option(name = "--errors", aliases = { "-e" }, usage = "Output full execution error messages (stacktraces)")
   public void setErrors(boolean errors)
   {
      errorsSet = true;
      this.errors = errors;
   }

   @Override
   public boolean getHelp()
   {
      return this.help;
   }

   @Override
   @Option(name = "--help", aliases = { "-h", "-help" }, usage = "Display this help and exit")
   public void setHelp(boolean help)
   {
      this.help = help;
   }

   @Override
   public boolean getQuiet()
   {
      return quiet;
   }

   @Override
   @Option(name = "--quiet", aliases = { "-q" }, usage = "Quiet mode - error messages only")
   public void setQuiet(boolean quiet)
   {
      quietSet = true;
      this.quiet = quiet;
   }

   @Override
   public boolean isDebugSet()
   {
      return debugSet;
   }

   @Override
   public boolean isErrorsSet()
   {
      return errorsSet;
   }

   @Override
   public boolean isQuietSet()
   {
      return quietSet;
   }
}
