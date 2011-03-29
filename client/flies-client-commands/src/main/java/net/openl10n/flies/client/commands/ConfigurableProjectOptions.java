package net.openl10n.flies.client.commands;

import net.openl10n.flies.client.config.LocaleList;

import org.kohsuke.args4j.Option;

/**
 * Base options for commands which support configuration by the user's
 * zanata.ini and by a project's zanata.xml
 * 
 * @author Sean Flanigan <sflaniga@redhat.com>
 * 
 */
public interface ConfigurableProjectOptions extends ConfigurableOptions
{

   public String getProj();

   @Option(name = "--project", metaVar = "PROJ", usage = "Project ID.  This value is required unless specified in zanata.xml.")
   public void setProj(String projectSlug);

   @Option(name = "--project-config", metaVar = "FILENAME", usage = "Project configuration file, eg zanata.xml", required = false)
   public void setProjectConfig(String projectConfig);

   public String getProjectVersion();

   @Option(name = "--project-version", metaVar = "VER", usage = "Project version ID  This value is required unless specified in zanata.xml.")
   public void setProjectVersion(String versionSlug);

   public String getProjectConfig();

   public LocaleList getLocales();

   public void setLocales(LocaleList locales);

}