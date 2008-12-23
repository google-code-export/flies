package org.fedorahosted.flies.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "uname"))
public class Project implements Serializable {

    private Long id;
    private Integer version;
    private String name;
    
    private String uname;
    
    private String shortDescription;
    
    private String longDescription;
    
    
    private List<ProjectSeries> projectSeries;
    private List<ProjectTarget> projectTargets;

    @Id @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Version
    public Integer getVersion() {
        return version;
    }

    private void setVersion(Integer version) {
        this.version = version;
    }

    @Length(max = 20)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 2, max = 40)
    @NotNull
    @Pattern(regex="[a-zA-Z_\\-]*")
    public String getUname() {
		return uname;
	}
    
    public void setUname(String uname) {
		this.uname = uname;
	}
    
    @Length(max = 100)
    public String getShortDescription() {
		return shortDescription;
	}
    
    public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

    
    public String getLongDescription() {
		return longDescription;
	}
    
    public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
    
    @OneToMany(mappedBy="project")
    public List<ProjectSeries> getProjectSeries() {
		return projectSeries;
	}
    
    public void setProjectSeries(List<ProjectSeries> projectSeries) {
		this.projectSeries = projectSeries;
	}
    
    @OneToMany(mappedBy="project")
    public List<ProjectTarget> getProjectTargets() {
		return projectTargets;
	}
    
    public void setProjectTargets(List<ProjectTarget> projectTargets) {
		this.projectTargets = projectTargets;
	}
}
