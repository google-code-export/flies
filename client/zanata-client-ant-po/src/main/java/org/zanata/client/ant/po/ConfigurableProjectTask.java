package org.zanata.client.ant.po;


import org.kohsuke.args4j.Option;
import org.zanata.client.config.LocaleList;

public abstract class ConfigurableProjectTask extends ConfigurableTask
{
   private String projectConfig = "zanata.xml";

   private String project;
   private String projectVersion;
   private LocaleList locales;

   public String getProj()
   {
      return project;
   }

   @Option(name = "--project", metaVar = "PROJ", usage = "Project ID.  This value is required unless specified in zanata.xml.")
   public void setProj(String projectSlug)
   {
      this.project = projectSlug;
   }

   @Option(name = "--project-config", metaVar = "FILENAME", usage = "Project configuration, eg zanata.xml", required = false)
   public void setProjectConfig(String projectConfig)
   {
      this.projectConfig = projectConfig;
   }

   public String getProjectVersion()
   {
      return projectVersion;
   }

   @Option(name = "--project-version", metaVar = "VER", usage = "Project version ID  This value is required unless specified in zanata.xml.")
   public void setProjectVersion(String versionSlug)
   {
      this.projectVersion = versionSlug;
   }

   public String getProjectConfig()
   {
      return projectConfig;
   }

   public LocaleList getLocales()
   {
      return locales;
   }

   public void setLocales(LocaleList locales)
   {
      this.locales = locales;
   }
}
