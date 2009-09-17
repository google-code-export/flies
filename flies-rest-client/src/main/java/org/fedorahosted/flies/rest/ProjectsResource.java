package org.fedorahosted.flies.rest;

import java.net.URI;

import org.fedorahosted.flies.rest.client.IProjectResource;
import org.fedorahosted.flies.rest.client.IProjectsResource;
import org.fedorahosted.flies.rest.dto.ProjectInlineList;
import org.jboss.resteasy.client.ClientResponse;

public class ProjectsResource implements IProjectsResource{

	private final IProjectsResource projectsResource;
	private final FliesClientRequestFactory requestFactory;
	private final URI uri;
	
	public ProjectsResource(FliesClientRequestFactory requestFactory, IProjectsResource projectsResource, URI uri) {
		this.projectsResource = projectsResource;
		this.requestFactory = requestFactory;
		this.uri = uri;
	}
	
	public ProjectResource getProjectResource(String projectSlug) {
		URI uri = this.uri.resolve("projects/p/"+projectSlug);
		IProjectResource projectResource = requestFactory.getProjectResource(uri);
		return new ProjectResource(requestFactory, projectResource, uri);
	}

	@Override
	public ClientResponse<ProjectInlineList> get() {
		return projectsResource.get();
	}
	

}
