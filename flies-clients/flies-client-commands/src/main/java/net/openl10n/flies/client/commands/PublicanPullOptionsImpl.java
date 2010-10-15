package net.openl10n.flies.client.commands;

import java.io.File;

import org.kohsuke.args4j.Option;

/**
 * @author Sean Flanigan <a
 *         href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 * 
 */
public class PublicanPullOptionsImpl extends ConfigurableProjectOptionsImpl implements PublicanPullOptions
{
   private File dstDir;
   private boolean exportPot;

   @Override
   public FliesCommand initCommand()
   {
      return new PublicanPullCommand(this);
   }

   @Override
   public String getCommandName()
   {
      return "publican-pull";
   }

   @Override
   public String getCommandDescription()
   {
      return "Pulls translated text from Flies.";
   }

   @Option(aliases = { "-d" }, name = "--dst", metaVar = "DIR", required = true, usage = "Base directory for publican files (with subdirectory \"pot\" and locale directories)")
   @Override
   public void setDstDir(File dstDir)
   {
      this.dstDir = dstDir;
   }

   @Override
   public File getDstDir()
   {
      return dstDir;
   }

   @Override
   public boolean getExportPot()
   {
      return exportPot;
   }

   @Override
   @Option(name = "--export-pot", usage = "Export source text from Flies to local POT files")
   public void setExportPot(boolean exportPot)
   {
      this.exportPot = exportPot;
   }

}
