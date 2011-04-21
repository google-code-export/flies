package org.zanata.client.commands;

import java.io.File;

import org.kohsuke.args4j.Option;

/**
 * @author Sean Flanigan <a
 *         href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 * 
 */
public interface PublicanPullOptions extends ConfigurableProjectOptions
{

   @Option(aliases = { "-d" }, name = "--dst", metaVar = "DIR", required = true, usage = "Base directory for publican files (with subdirectory \"pot\" and locale directories)")
   public void setDstDir(File dstDir);

   public void setDstDirPot(File dstDirPot);

   public File getDstDir();

   public File getDstDirPot();

   boolean getExportPot();

   void setExportPot(boolean exportPot);

}