package org.zanata.client.commands;

import java.io.File;

import org.kohsuke.args4j.Option;

/**
 * @author Sean Flanigan <a
 *         href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 * 
 */
public class PublicanPushOptionsImpl extends ConfigurableProjectOptionsImpl implements PublicanPushOptions
{
   private File srcDir;
   private File srcDirPot;

   private String sourceLang = "en-US";

   private boolean importPo;
   private boolean copyTrans = true;
   private boolean validate;
   private String mergeType = "auto";

   public PublicanPushOptionsImpl()
   {
      super();
   }

   @Override
   public String getCommandName()
   {
      return "publican-push";
   }

   @Override
   public String getCommandDescription()
   {
      return "Publishes publican source text to Zanata so that it can be translated.";
   }

   @Override
   public PublicanPushCommand initCommand()
   {
      return new PublicanPushCommand(this);
   }

   @Option(aliases = { "-s" }, name = "--src", metaVar = "DIR", required = true, usage = "Base directory for publican files (with subdirectory \"pot\" and optional locale directories)")
   public void setSrcDir(File srcDir)
   {
      this.srcDir = srcDir;
      if (srcDirPot == null)
         srcDirPot = new File(srcDir, "pot");
   }

   @Option(name = "--src-pot", metaVar = "DIR", required = false, usage = "Override base directory for publican POT files (defaults to \"pot\" under --src directory)")
   public void setSrcDirPot(File srcDirPot)
   {
      this.srcDirPot = srcDirPot;
   }

   @Option(aliases = { "-l" }, name = "--src-lang", usage = "Language of source (defaults to en-US)")
   public void setSourceLang(String sourceLang)
   {
      this.sourceLang = sourceLang;
   }

   @Option(name = "--import-po", usage = "Import translations from local PO files to Zanata, overwriting or erasing existing translations (DANGER!)")
   public void setImportPo(boolean importPo)
   {
      this.importPo = importPo;
   }

   @Option(name = "--validate", usage = "Validate XML before sending request to server")
   public void setValidate(boolean validate)
   {
      this.validate = validate;
   }


   @Override
   public boolean getValidate()
   {
      return validate;
   }

   @Override
   public boolean getImportPo()
   {
      return importPo;
   }
   
   @Override
   public boolean getCopyTrans()
   {
      return copyTrans;
   }
   
   @Option(name = "--no-copy-trans", usage = "Don't copy latest translation from equivalent documents from other versions of the same project")
   public void setNoCopyTrans(boolean noCopyTrans)
   {
      this.copyTrans = !noCopyTrans;
   }

   @Override
   public File getSrcDir()
   {
      return srcDir;
   }

   @Override
   public File getSrcDirPot()
   {
      return srcDirPot;
   }

   @Override
   public String getSourceLang()
   {
      return sourceLang;
   }
   
   @Override
   public String getMergeType()
   {
      return mergeType;
   }

}
