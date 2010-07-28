package org.fedorahosted.flies.client.ant.po;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.fedorahosted.flies.adapter.po.PoReader;
import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.rest.JaxbUtil;
import org.fedorahosted.flies.rest.client.ClientUtility;
import org.fedorahosted.flies.rest.client.FliesClientRequestFactory;
import org.fedorahosted.flies.rest.client.IDocumentsResource;
import org.fedorahosted.flies.rest.dto.deprecated.Document;
import org.fedorahosted.flies.rest.dto.deprecated.Documents;
import org.jboss.resteasy.client.ClientResponse;
import org.kohsuke.args4j.Option;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UploadPoTask extends Task implements Subcommand
{

   private String user;

   private String apiKey;

   private String dst;

   private File srcDir;

   private String sourceLang = "en-US";

   private boolean debug;

   private boolean help;

   private boolean errors;

   private boolean importPo;

   private boolean validate;

   public static void main(String[] args) throws Exception
   {
      UploadPoTask task = new UploadPoTask();
      ArgsUtil.processArgs(task, args, GlobalOptions.EMPTY);
   }

   @Override
   public String getCommandName()
   {
      return "uploadpo";
   }

   @Override
   public String getCommandDescription()
   {
      return "Uploads a Publican project's PO/POT files to Flies for translation";
   }

   @Override
   public void execute() throws BuildException
   {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // make sure RESTEasy classes will be found:
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
         process();
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

   public void process() throws JAXBException, SAXException, URISyntaxException, IOException
   {
      PoReader poReader = new PoReader();
      // scan the directory for pot files
      File potDir = new File(srcDir, "pot");
      File[] potFiles = potDir.listFiles(new FileFilter()
      {
         @Override
         public boolean accept(File pathname)
         {
            return pathname.isFile() && pathname.getName().endsWith(".pot");
         }
      });

      if (potFiles == null)
      {
         throw new IOException("Unable to read directory \"pot\" - have you run \"publican update_pot\"?");
      }

      // debug: print scanned files
      if (debug)
      {
         System.out.println("Here are scanned files: ");
         for (File potFile : potFiles)
            System.out.println("  " + potFile);
      }

      JAXBContext jc = JAXBContext.newInstance(Documents.class);
      Marshaller m = jc.createMarshaller();

      // debug
      if (debug)
         m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

      Documents docs = new Documents();
      List<Document> docList = docs.getDocuments();

      File[] localeDirs = new File[0];
      if (importPo)
      {
         localeDirs = srcDir.listFiles(new FileFilter()
         {

            @Override
            public boolean accept(File f)
            {
               return f.isDirectory() && !f.getName().equals("pot");
            }
         });
      }

      // for each of the base pot files under srcdir/pot:
      for (File potFile : potFiles)
      {
         //				progress.update(i++, files.length);
         String basename = StringUtil.removeFileExtension(potFile.getName(), ".pot");
         Document doc = new Document(basename, ContentType.TextPlain);
         InputSource potInputSource = new InputSource(potFile.toURI().toString());
         //				System.out.println(potFile.toURI().toString());
         potInputSource.setEncoding("utf8");
         poReader.extractTemplate(doc, potInputSource, new LocaleId(sourceLang));
         docList.add(doc);

         String poName = basename + ".po";

         // for each of the corresponding po files in the locale subdirs:
         // (The locale list should actually be empty unless importPo is enabled)
         if (importPo)
         {
            for (int i = 0; i < localeDirs.length; i++)
            {
               File localeDir = localeDirs[i];
               File poFile = new File(localeDir, poName);
               if (poFile.exists())
               {
                  //					progress.update(i++, files.length);
                  InputSource inputSource = new InputSource(poFile.toURI().toString());
                  //							System.out.println(poFile.toURI().toString());
                  inputSource.setEncoding("utf8");
                  poReader.extractTarget(doc, inputSource, new LocaleId(localeDir.getName()));
               }
            }
         }
      }
      //			progress.finished();

      if (debug)
      {
         m.marshal(docs, System.out);
      }

      if (validate)
         JaxbUtil.validateXml(docs, jc);

      if (dst == null)
         return;

      // check if local or remote: write to file if local, put to server if remote
      URL dstURL = Utility.createURL(dst, Utility.getBaseDir(getProject()));
      if ("file".equals(dstURL.getProtocol()))
      {
         m.marshal(docs, new File(dstURL.getFile()));
      }
      else
      {
         // send project to rest api
         FliesClientRequestFactory factory = new FliesClientRequestFactory(user, apiKey);
         IDocumentsResource documentsResource = factory.getDocumentsResource(dstURL.toURI());
         ClientResponse response = documentsResource.put(docs);
         ClientUtility.checkResult(response, dstURL);
      }
   }

   @Override
   public void log(String msg)
   {
      super.log(msg + "\n\n");
   }

   @Option(name = "--user", metaVar = "USER", usage = "Flies user name", required = true)
   public void setUser(String user)
   {
      this.user = user;
   }

   @Option(name = "--key", metaVar = "KEY", usage = "Flies API key (from Flies Profile page)", required = true)
   public void setApiKey(String apiKey)
   {
      this.apiKey = apiKey;
   }

   // TODO make --dst optional, and provide --flies, --proj, --iter options

   @Option(aliases = { "-d" }, name = "--dst", metaVar = "URL", required = true, usage = "Destination URL for upload, eg http://flies.example.com/seam/resource/restv1/projects/p/myProject/iterations/i/myIter/documents")
   public void setDst(String dst)
   {
      this.dst = dst;
   }

   @Option(aliases = { "-s" }, name = "--src", metaVar = "DIR", required = true, usage = "Base directory for publican files (with subdirectory \"pot\" and optional locale directories)")
   public void setSrcDir(File srcDir)
   {
      this.srcDir = srcDir;
   }

   @Option(aliases = { "-l" }, name = "--srclang", usage = "Language of source (defaults to en-US)")
   public void setSourceLang(String sourceLang)
   {
      this.sourceLang = sourceLang;
   }

   @Option(name = "--importpo", usage = "Import translations from local PO files to Flies (DANGER!)")
   public void setImportPo(boolean importPo)
   {
      this.importPo = importPo;
   }

   @Option(name = "--debug", aliases = { "-x" }, usage = "Enable debug mode")
   public void setDebug(boolean debug)
   {
      this.debug = debug;
   }

   @Override
   public boolean getHelp()
   {
      return this.help;
   }

   @Option(name = "--help", aliases = { "-h", "-help" }, usage = "Display this help and exit")
   public void setHelp(boolean help)
   {
      this.help = help;
   }

   @Override
   public boolean getErrors()
   {
      return this.errors;
   }

   @Option(name = "--errors", aliases = { "-e" }, usage = "Output full execution error messages")
   public void setErrors(boolean errors)
   {
      this.errors = errors;
   }


   @Option(name = "validate", usage = "Validate XML before sending request to server")
   public void setValidate(boolean validate)
   {
      this.validate = validate;
   }

}
