package org.fedorahosted.flies;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.fedorahosted.flies.entity.Project;

@Name("projectList")
public class ProjectList extends EntityQuery<Project>
{
    public ProjectList()
    {
        setEjbql("select project from Project project");
    }
}
