package org.fedorahosted.flies.entity.resources;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.fedorahosted.flies.entity.Project;
import org.fedorahosted.flies.entity.ProjectTarget;
import org.fedorahosted.flies.entity.locale.Locale;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
public class Document implements Serializable{

	private Long id;
    private Integer version;
	
    private String name;
    
    private String contentType;
    
	private List<DocumentTarget> targets;
	private List<TextUnit> entries;

	private Integer revision;
	
	private Project project;
	private ProjectTarget projectTarget;
	
	public Integer getRevision() {
		return revision;
	}
	
	public void setRevision(Integer revision) {
		this.revision = revision;
	}
	
	@Id @GeneratedValue
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) {
		this.id = id;
	}
	
    @Version
    public Integer getVersion() {
        return version;
    }

    private void setVersion(Integer version) {
        this.version = version;
    }

    @NotEmpty
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
    
    @Length(max=20)
    @NotEmpty
    public String getContentType() {
		return contentType;
	}
    
    public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    
    @ManyToOne
    @JoinColumn(name="projectId")
    public Project getProject() {
		return project;
	}
    
    public void setProject(Project project) {
		this.project = project;
	}
    
    @ManyToOne
    @JoinColumn(name="projectTargetId")
    public ProjectTarget getProjectTarget() {
		return projectTarget;
	}
    
    public void setProjectTarget(ProjectTarget projectTarget) {
		this.projectTarget = projectTarget;
	}
    
	@OneToMany(mappedBy="template")
	public List<DocumentTarget> getTargets() {
		return targets;
	}
	
	public void setTargets(List<DocumentTarget> targets) {
		this.targets = targets;
	}
	
	@OneToMany(mappedBy="document")
	public List<TextUnit> getEntries() {
		return entries;
	}
	
	public void setEntries(List<TextUnit> entries) {
		this.entries = entries;
	}
	
	@Transient
	public DocumentTarget createTarget(Locale locale){
		DocumentTarget target = new DocumentTarget();
		target.setTemplate(this);
		target.setLocale(locale);
		return target;
	}
	
	
}
