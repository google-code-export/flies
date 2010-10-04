package net.openl10n.flies.client.ant.po;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.openl10n.flies.client.commands.ArgsUtil;
import net.openl10n.flies.rest.client.ClientUtility;
import net.openl10n.flies.rest.client.FliesClientRequestFactory;
import net.openl10n.flies.rest.client.IProjectIterationResource;
import net.openl10n.flies.rest.dto.ProjectIteration;

import org.jboss.resteasy.client.ClientResponse;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated See PutVersionCommand
 */
public class CreateIterationTask extends FliesTask
{
   private static final Logger log = LoggerFactory.getLogger(CreateIterationTask.class);

   private String user;
   private String apiKey;
   private String fliesURL;
   private String proj;
   private String iter;
   private String name;
   private String desc;

   public static void main(String[] args)
   {
      CreateIterationTask task = new CreateIterationTask();
      ArgsUtil.processArgs(args, task);
   }

   @Override
   public String getCommandName()
   {
      return "createiter";
   }

   @Override
   public String getCommandDescription()
   {
      return "Creates a project iteration in Flies";
   }

   public void run() throws JAXBException, URISyntaxException, IOException
   {
      JAXBContext jc = JAXBContext.newInstance(ProjectIteration.class);
      Marshaller m = jc.createMarshaller();
      // debug
      if (getDebug())
         m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

      ProjectIteration iteration = new ProjectIteration();
      iteration.setId(iter);
      iteration.setName(name);
      iteration.setDescription(desc);

      if (getDebug())
      {
         StringWriter writer = new StringWriter();
         m.marshal(iteration, writer);
         log.debug("{}", writer);
      }

      if (fliesURL == null)
         return;
      URI base = new URI(fliesURL);
      // send iter to rest api
      FliesClientRequestFactory factory = new FliesClientRequestFactory(base, user, apiKey, versionInfo);
      IProjectIterationResource iterResource = factory.getProjectIteration(proj, iter);
      URI uri = factory.getProjectIterationURI(proj, iter);
      ClientResponse<?> response = iterResource.put(iteration);
      ClientUtility.checkResult(response, uri);
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

   @Option(name = "--flies", metaVar = "URL", usage = "Flies base URL, eg http://flies.example.com/flies/", required = true)
   public void setFliesURL(String url)
   {
      this.fliesURL = url;
   }

   @Option(name = "--proj", metaVar = "PROJ", usage = "Flies project ID", required = true)
   public void setProj(String id)
   {
      this.proj = id;
   }

   @Option(name = "--iter", metaVar = "ITER", usage = "Flies project iteration ID", required = true)
   public void setIter(String id)
   {
      this.iter = id;
   }

   @Option(name = "--name", metaVar = "NAME", usage = "Flies project iteration name", required = true)
   public void setName(String name)
   {
      this.name = name;
   }

   @Option(name = "--desc", metaVar = "DESC", usage = "Flies project iteration description", required = true)
   public void setDesc(String desc)
   {
      this.desc = desc;
   }


}
