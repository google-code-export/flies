package org.zanata.client.commands;

import java.io.File;

public interface PublicanPushOptions extends ConfigurableProjectOptions
{
   public File getSrcDir();
   public File getSrcDirPot();
   public String getSourceLang();
   public boolean getImportPo();
   public boolean getCopyTrans();
   public boolean getValidate();
   public String getMergeType();
}