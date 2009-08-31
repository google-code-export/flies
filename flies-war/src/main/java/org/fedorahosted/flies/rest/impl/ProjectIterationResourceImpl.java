package org.fedorahosted.flies.rest.impl;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.fedorahosted.flies.ContentType;
import org.fedorahosted.flies.core.model.ProjectIteration;
import org.fedorahosted.flies.rest.DocumentResource;
import org.fedorahosted.flies.rest.ProjectIterationResource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.DocumentRef;
import org.fedorahosted.flies.rest.dto.Project;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;

@Name("projectIterationResource")
public class ProjectIterationResourceImpl implements ProjectIterationResource{

	private ProjectIteration projectIteration;
	
	public void setProjectIteration(ProjectIteration projectIteration) {
		this.projectIteration = projectIteration;
	}

	@Override
	public Project get(String ext) {
		Project p = load();
		//Set<String> extensions = ImmutableSet.of( StringUtils.split(ext, ',') );
		//if(extensions != null && extensions.contains("docs")){
			Document d = new Document("123", "name", "/full/path", ContentType.TextPlain, 1);
			p.getDocuments().add( new DocumentRef(d) );
		//}
		return p;
	}
	
	private Project load(){
		Project p = new Project();
		p.setId( projectIteration.getProject().getSlug() + '/' + projectIteration.getSlug());
		p.setName(projectIteration.getProject().getName() + " - " + projectIteration.getName());
		p.setSummary( projectIteration.getDescription() );
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
