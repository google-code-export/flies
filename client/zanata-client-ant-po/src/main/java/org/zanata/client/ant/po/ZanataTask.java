package org.zanata.client.ant.po;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.kohsuke.args4j.Option;
import org.zanata.client.commands.BasicOptions;
import org.zanata.rest.dto.VersionInfo;

public abstract class ZanataTask extends Task implements BasicOptions
{

   private boolean debug;
   private boolean errors;
   private boolean help;
   private boolean quiet;
   private boolean interactiveMode = true;
   // TODO shouldn't this use VersionUtility.getVersionInfo() ?
   protected VersionInfo versionInfo = new VersionInfo("SNAPSHOT", "");

   @Override
   public boolean getDebug()
   {
      return debug;
   }

   @Option(name = "--debug", aliases = { "-X" }, usage = "Enable debug logging")
   @Override
   public void setDebug(boolean debug)
   {
      this.debug = debug;
      if (debug)
      {
         setErrors(true);
      }
   }

   @Override
   public boolean getHelp()
   {
      return this.help;
   }

   @Option(name = "--help", aliases = { "-h", "-help" }, usage = "Display this help and exit")
   @Override
   public void setHelp(boolean help)
   {
      this.help = help;
   }

   @Override
   public boolean getErrors()
   {
      return this.errors;
   }

   @Option(name = "--errors", aliases = { "-e" }, usage = "Output full execution error messages (stacktraces)")
   @Override
   public void setErrors(boolean errors)
   {
      this.errors = errors;
   }

   @Override
   public boolean getQuiet()
   {
      return quiet;
   }

   @Option(name = "--quiet", aliases = { "-q" }, usage = "Quiet mode - error messages only")
   @Override
   public void setQuiet(boolean quiet)
   {
      this.quiet = quiet;
   }

   @Override
   public boolean isInteractiveMode()
   {
      return interactiveMode;
   }

   @Override
   public void setInteractiveMode(boolean interactiveMode)
   {
      this.interactiveMode = interactiveMode;
   }

   @Override
   public void execute() throws BuildException
   {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // make sure RESTEasy classes will be found:
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
         // OptionsUtil.applyConfigFiles(this);
         this.initCommand().run();
      }
      catch (Exception e)
      {
         throw new BuildException(e);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(oldLoader);
      }
   }

   @Override
   public void log(String msg)
   {
      super.log(msg + "\n\n");
   }

   @Override
   public boolean isDebugSet()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isErrorsSet()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isQuietSet()
   {
      // TODO Auto-generated method stub
      return false;
   }

}
