package org.fedorahosted.flies.entity.resources;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.fedorahosted.flies.entity.locale.Locale;

@Entity
@Table(	uniqueConstraints = {@UniqueConstraint(columnNames={"id", "document_id"})})
public class DocumentTarget implements Serializable{

	private Long id;
	private Document template;
	private Locale locale;
	
    private Integer version;
    
	private List<TextUnitTarget> entries;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="document_id")
	//@NaturalId
	public Document getTemplate() {
		return template;
	}
	
	public void setTemplate(Document template) {
		this.template = template;
	}
	
	@ManyToOne
	@JoinColumn(name="locale_id")
	//@NaturalId
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
    @Version
    public Integer getVersion() {
        return version;
    }

    private void setVersion(Integer version) {
        this.version = version;
    }
	
	@OneToMany
	@JoinColumns({
		@JoinColumn(name="document_id", referencedColumnName="document_id", insertable=false, updatable=false),
		@JoinColumn(name="locale_id", referencedColumnName="locale_id", insertable=false, updatable=false)
	})
	public List<TextUnitTarget> getEntries() {
		return entries;
	}
	
	public void setEntries(List<TextUnitTarget> entries) {
		this.entries = entries;
	}
	
}
