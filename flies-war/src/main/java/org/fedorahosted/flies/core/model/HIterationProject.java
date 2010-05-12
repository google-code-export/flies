package org.fedorahosted.flies.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import org.hibernate.search.annotations.Indexed;

@Entity
@DiscriminatorValue("iteration")
@Indexed
public class HIterationProject extends HProject{

	private List<HProjectIteration> projectIterations = new ArrayList<HProjectIteration>();

	@OneToMany(mappedBy = "project")
	public List<HProjectIteration> getProjectIterations() {
		return projectIterations;
	}

	public void setProjectIterations(List<HProjectIteration> projectIterations) {
		this.projectIterations = projectIterations;
	}
	
}
