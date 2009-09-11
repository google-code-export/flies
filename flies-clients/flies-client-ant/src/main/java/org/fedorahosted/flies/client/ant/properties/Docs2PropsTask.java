package org.fedorahosted.flies.client.ant.properties;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.fedorahosted.flies.adapter.properties.PropWriter;
import org.fedorahosted.flies.rest.FliesClient;
import org.fedorahosted.flies.rest.client.DocumentResource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.Documents;
import org.jboss.resteasy.client.ClientResponse;

public class Docs2PropsTask extends MatchingTask {

    private String apiKey;
    private boolean debug;
    private File dstDir;
    private String src;
    private String url;
    
    @Override
    public void execute() throws BuildException {
	try {
	    Unmarshaller m = null;
	    if (debug) {
		JAXBContext jc = Context.newJAXBContext();
		m = jc.createUnmarshaller();
	    }
	    
	    URL srcURL = Utility.createURL(src, getProject());

	    List<Document> docList;
	    if("file".equals(srcURL.getProtocol())) {
		Documents docs = (Documents) m.unmarshal(new File(srcURL.getFile()));
		docList = docs.getDocuments();
	    } else {
		// use rest api to fetch Documents
		FliesClient client = new FliesClient(url, apiKey);
		DocumentResource documentResource = client.getDocumentResource("FIXME", "METOO");
		ClientResponse<Documents> response  = documentResource.getAllDocuments();
		
		if (response.getStatus() >= 399) {
		    throw new BuildException(response.toString());
		} else {
		    docList = response.getEntity().getDocuments();
		}
	    }
	    
	    for (Document doc : docList) {
		PropWriter.write(doc, dstDir);
	    }
	} catch (Exception e) {
	    throw new BuildException(e);
	}
    }
    
    @Override
    public void log(String msg) {
        super.log(msg+"\n\n");
    }
    
//    private void logVerbose(String msg) {
//	super.log(msg, org.apache.tools.ant.Project.MSG_VERBOSE);
//    }
    
    public void setApiKey(String apiKey) {
	this.apiKey = apiKey;
    }
    
    public void setDebug(boolean debug) {
	this.debug = debug;
    }
    
    public void setDstDir(File dstDir) {
	this.dstDir = dstDir;
    }

    public void setSrc(String src) {
	this.src = src;
    }
    
    public void setUrl(String url) {
	this.url = url;
    }

}