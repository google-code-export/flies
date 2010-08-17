package org.fedorahosted.flies.action;

import org.fedorahosted.flies.model.HProject;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("projectList")
public class ProjectList extends EntityQuery<HProject>
{
   public ProjectList()
   {
      setEjbql("select project from HProject project");
   }
}
