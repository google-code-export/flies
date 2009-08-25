package org.fedorahosted.flies.core.rest;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import net.openl10n.api.ContentType;
import net.openl10n.api.rest.document.Document;
import net.openl10n.api.rest.project.Project;
import net.openl10n.packaging.jpa.project.HProject;

import org.apache.commons.lang.StringUtils;
import org.fedorahosted.flies.core.model.ProjectIteration;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;

import com.google.common.collect.ImmutableSet;

@Name("projectIterationResource")
public class ProjectIterationResourceImpl implements ProjectIterationResource{

	private ProjectIteration projectIteration;
	
	public void setProjectIteration(ProjectIteration projectIteration) {
		this.projectIteration = projectIteration;
	}

	@Override
	public Project get(String ext) {
		Project p = new Project("id", "name", "summary");
		Set<String> extensions = ImmutableSet.of( StringUtils.split(ext, ',') );
		if(extensions != null && extensions.contains("docs")){
			p.getDocuments().add( new Document("/path/to/doc.txt", ContentType.TextPlain ) );
		}
		return p;
	}
	
	@Override
	public Response post(Project project) {
		return Response.created( URI.create("http://example.com/project") ).build();
	}
	
	@Override
	public Response put(Project project) {
		return Response.created( URI.create("http://example.com/project") ).build();
	}
	
	
	@Override
	public DocumentResource getDocument(String documentId) {
		DocumentResourceImpl docRes = (DocumentResourceImpl) Component.getInstance(DocumentResourceImpl.class, true);
		//docRes.setProject();
		return DocumentResourceImpl.getProxyWrapper(docRes);
	}
	
	
	// hack to allow sub-resource in resteasy
	public static ProjectIterationResource getProxyWrapper(final ProjectIterationResource instance){
		return new ProjectIterationResource(){

			@Override
			public Project get(String extensions) {
				return instance.get(extensions);
			}
			
			@Override
			public Response post(Project project) {
				return instance.post(project);
			}
			
			@Override
			public Response put(Project project) {
				return instance.put(project);
			}
			
			@Override
			public DocumentResource getDocument(String documentId) {
				return instance.getDocument(documentId);
			}
			
		};
	}	
	
}
