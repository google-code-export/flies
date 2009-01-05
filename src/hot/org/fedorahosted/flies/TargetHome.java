package org.fedorahosted.flies;

import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import org.fedorahosted.flies.entity.ProjectTarget;

@Name("targetHome")
public class TargetHome extends EntityHome<ProjectTarget>
{
    @RequestParameter
    String uname;

    @RequestParameter
    Long targetId;
    
    @Override
    public Object getId()
    {
        if (targetId == null)
        {
            return super.getId();
        }
        
        return targetId;
    }
    
    @Override
    protected ProjectTarget loadInstance() 
    {
       return getEntityManager().find(getEntityClass(), getId());
    }    
    
}
