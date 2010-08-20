/**
 * 
 */
package net.openl10n.flies.client.commands;

import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sean Flanigan <sflaniga@redhat.com>
 *
 */
public class ArgsUtil
{
   private static final Logger log = LoggerFactory.getLogger(ArgsUtil.class);
   private final AppAbortStrategy abortStrategy;

   public ArgsUtil(AppAbortStrategy strategy)
   {
      this.abortStrategy = strategy;
   }

   public static void processArgs(FliesCommand cmd, String[] args, BasicOptions globals)
   {
      new ArgsUtil(new SystemExitStrategy()).process(cmd, args, globals);
   }

   public void process(FliesCommand cmd, String[] args, BasicOptions globals)
   {
      CmdLineParser parser = new CmdLineParser(cmd);

      if (globals.getDebug())
      {
         cmd.setDebug(true);
      }
      if (globals.getErrors())
      {
         cmd.setErrors(true);
      }
      if (globals.getHelp())
      {
         cmd.setHelp(true);
      }
      if (globals.getQuiet())
      {
         cmd.setQuiet(true);
      }

      try
      {
         parser.setUsageWidth(Integer.parseInt(System.getenv("COLUMNS")));
      }
      catch (Exception e)
      {
         parser.setUsageWidth(120);
      }
      try
      {
         parser.parseArgument(args);
      }
      catch (CmdLineException e)
      {
         if (!cmd.getHelp() && args.length != 0)
         {
            System.err.println(e.getMessage());
            printHelp(cmd, System.err);
            parser.printUsage(System.err);
            abortStrategy.abort();
         }
      }

      if (cmd.getHelp() || args.length == 0)
      {
         printHelp(cmd, System.out);
         parser.printUsage(System.out);
         return;
      }

      try
      {
         // while loading config, we use the global logging options
         setLogLevels(globals);
         cmd.initConfig();
      }
      catch (Exception e)
      {
         handleException(e, globals.getErrors(), abortStrategy);
      }
      try
      {
         // just in case the logging options were changed by a config file:
         setLogLevels(cmd);
         if (cmd.getErrors())
         {
            log.info("Error stacktraces are turned on.");
         }
         cmd.run();
      }
      catch (Exception e)
      {
         handleException(e, cmd.getErrors(), abortStrategy);
      }
   }

   private static void setLogLevels(BasicOptions opts)
   {
      if (opts.getDebug())
      {
         enableDebugLogging();
      }
      else if (opts.getQuiet())
      {
         enableQuietLogging();
      }
   }

   /**
    * Maven's --debug/-X flag sets the Maven LoggerManager to LEVEL_DEBUG. The
    * slf4j framework doesn't provide any way of doing this, so we have to go to
    * the underlying framework (assumed to be log4j).
    */
   private static void enableDebugLogging()
   {
      org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
      root.setLevel(org.apache.log4j.Level.DEBUG);
   }

   /**
    * Maven's --quiet/-q flag sets the Maven LoggerManager to LEVEL_ERROR. The
    * slf4j framework doesn't provide any way of doing this, so we have to go to
    * the underlying framework (assumed to be log4j).
    */
   private static void enableQuietLogging()
   {
      org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
      root.setLevel(org.apache.log4j.Level.ERROR);
   }

   private static void printHelp(FliesCommand cmd, PrintStream output)
   {
      output.println("Usage: " + cmd.getCommandName() + " [options]");
      output.println(cmd.getCommandDescription());
      output.println();
   }

   public static void handleException(Exception e, boolean outputErrors, AppAbortStrategy abortStrategy)
   {
      if (outputErrors)
      {
         log.error("Execution failed: ", e);
      }
      else
      {
         log.error("Execution failed: " + e.getMessage());
         log.error("Use -e/--errors for full stack trace (or when reporting bugs)");
      }
      abortStrategy.abort();
   }

}
