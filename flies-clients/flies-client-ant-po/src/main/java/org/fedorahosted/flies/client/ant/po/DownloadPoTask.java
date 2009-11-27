package org.fedorahosted.flies.client.ant.po;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.fedorahosted.flies.adapter.po.PoReader;
import org.fedorahosted.flies.client.ant.po.BasePoSelector;
import org.fedorahosted.flies.client.ant.po.Utility;
import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.rest.ClientUtility;
import org.fedorahosted.flies.rest.FliesClientRequestFactory;
import org.fedorahosted.flies.rest.client.IDocumentsResource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.Documents;
import org.jboss.resteasy.client.ClientResponse;
import org.xml.sax.InputSource;

public class DownloadPoTask extends MatchingTask {

	private String user;
	private String apiKey;
	private String dst;
	private File srcDir;
	private String[] locales;
	private String sourceLang;
	private boolean debug;
	private ContentState contentState = ContentState.Approved;
	
	@Override
	public void execute() throws BuildException {
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		try {
			// make sure RESTEasy classes will be found:
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			DirectoryScanner ds = getDirectoryScanner(srcDir);
			// use default includes if unset:
			if (!getImplicitFileSet().hasPatterns()) {
				ds.setIncludes(new String[] { "**/*.po" }); //$NON-NLS-1$
			}
			ds.setSelectors(getSelectors());
			ds.scan();
			String[] files = ds.getIncludedFiles();

			Marshaller m = null;
			JAXBContext jc = JAXBContext.newInstance(Documents.class);
			m = jc.createMarshaller();
			if (debug) {
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			}
			
			InputSource inputSource = new InputSource(
					//new File("/home/asgeirf/projects/gitsvn/Deployment_Guide/pt-BR/SELinux_Background.po").toURI().toString()
					"http://svn.fedorahosted.org/svn/Deployment_Guide/community/fc10/de-DE/Apache.po"
			);
			inputSource.setEncoding("utf8");

			Documents docs = new Documents();
			List<Document> docList = docs.getDocuments();
			PoReader poReader = new PoReader();
			// for each of the base Pos files under srcdir:
			int i = 0;
			for (String filename : files) {
//				progress.update(i++, files.length);
				Document doc = new Document(filename, ContentType.TextPlain);
				doc.setLang(LocaleId.fromJavaName(sourceLang));
				File f = new File(srcDir, filename);
				
				for (String locale : locales){
					inputSource = new InputSource(
							//new File("/home/asgeirf/projects/gitsvn/Deployment_Guide/" + locale + "/SELinux_Background.po").toURI().toString()
							"http://svn.fedorahosted.org/svn/Deployment_Guide/community/fc10/fr-FR/Apache.po"
					);
					inputSource.setEncoding("utf8");
					System.out.println("extracting target: " + locale);
					poReader.extractTarget(doc, inputSource, new LocaleId(locale));
				}
				
				docList.add(doc);
			}
//			progress.finished();
			if (debug) {
				m.marshal(docs, System.out);
			}

			if(dst == null)
				return;

			URL dstURL = Utility.createURL(dst, getProject());
			if("file".equals(dstURL.getProtocol())) {
				m.marshal(docs, new File(dstURL.getFile()));
			} else {
				// send project to rest api
				FliesClientRequestFactory factory = new FliesClientRequestFactory(user, apiKey);
				IDocumentsResource documentsResource = factory.getDocumentsResource(dstURL.toURI());
				ClientResponse response = documentsResource.put(docs);
				ClientUtility.checkResult(response, dstURL);
			}

		} catch (Exception e) {
			throw new BuildException(e);
		} finally {
			Thread.currentThread().setContextClassLoader(oldLoader);
		}
	}

	FileSelector[] getSelectors() {
		if (locales != null)
			return new FileSelector[] { new BasePoSelector(locales) };
		else
			return new FileSelector[0];
	}
	
	@Override
	public void log(String msg) {
		super.log(msg+"\n\n");
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public void setSrcDir(File srcDir) {
		this.srcDir = srcDir;
	}

	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	public void setLocales(String[] locales) {
		this.locales = locales;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
