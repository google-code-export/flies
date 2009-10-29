package org.fedorahosted.flies.repository.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.fedorahosted.flies.ContentType;
import org.fedorahosted.flies.LocaleId;
import org.fedorahosted.flies.core.model.AbstractFliesEntity;
import org.fedorahosted.flies.hibernate.type.ContentTypeType;
import org.fedorahosted.flies.hibernate.type.LocaleIdType;
import org.fedorahosted.flies.rest.dto.Container;
import org.fedorahosted.flies.rest.dto.DataHook;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.Reference;
import org.fedorahosted.flies.rest.dto.DocumentResource;
import org.fedorahosted.flies.rest.dto.TextFlow;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.Where;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@TypeDefs({
	@TypeDef(name="localeId", typeClass=LocaleIdType.class),
	@TypeDef(name = "contentType", typeClass = ContentTypeType.class)
})
public class HDocument extends AbstractFliesEntity{

	private String docId;
	private String name;
	private String path;
	private ContentType contentType;
	private Integer revision = 1;
	private LocaleId locale;
	
	private HProjectContainer project;

	private Map<String, HDocumentResource> allResources;
	private Map<String, HDocumentResource> obsoletes;
	private List<HDocumentResource> resources;
	private boolean obsolete = false;
	
	public HDocument(String fullPath, ContentType contentType) {
		this(fullPath, contentType, LocaleId.EN_US);
	}
	
	public HDocument(String fullPath, ContentType contentType, LocaleId locale) {
		int lastSepChar =  fullPath.lastIndexOf('/');
		switch(lastSepChar){
		case -1:
			this.path = "";
			this.docId = this.name = fullPath;
			break;
		case 0:
			this.path = "/";
			this.docId = fullPath;
			this.name = fullPath.substring(1);
			break;
		default:
			this.path = fullPath.substring(0,lastSepChar+1);
			this.docId = fullPath;
			this.name = fullPath.substring(lastSepChar+1);
		}
		this.contentType = contentType;
		this.locale = locale;
	}
	
	public HDocument(String docId, String name, String path, ContentType contentType) {
		this(docId, name, path, contentType, LocaleId.EN_US, 1);
	}
	
	public HDocument(String docId, String name, String path, ContentType contentType, LocaleId locale) {
		this(docId, name, path, contentType, locale, 1);
	}
	
	public HDocument(String docId, String name, String path, ContentType contentType, LocaleId locale, int revision) {
		this.docId = docId;
		this.name = name;
		this.path = path;
		this.contentType = contentType;
		this.locale = locale;
	}
	
	public HDocument() {
	}
	
	public HDocument(Document docInfo) {
		this.docId = docInfo.getId();
		this.name = docInfo.getName();
		this.path = docInfo.getPath();
		this.contentType = docInfo.getContentType();
		this.locale = docInfo.getLang();
		this.revision = docInfo.getRevision();
	}

	public static HDocumentResource create(DocumentResource res){
		if(res instanceof TextFlow){
			TextFlow tf = (TextFlow) res;
			return new HTextFlow( tf );
		}
		else if (res instanceof Container){
			Container cont = (Container) res;
			return new HContainer(cont);
		}
		else if (res instanceof DataHook){
			DataHook hook = (DataHook) res;
			return new HDataHook(hook);
		}
		else if (res instanceof Reference){
			Reference ref = (Reference) res;
			return new HReference(ref);
		}
		throw new IllegalStateException("could not find subclass of Resource: " + res.getClass().toString());
	}
	
	// seems to be obsolete
	public void copy(List<DocumentResource> content){
		for(DocumentResource res :content){
			HDocumentResource hRes = create(res);
			hRes.setDocument(this);
			getResources().add(hRes);
		}
	}
	
	@NaturalId
	@Length(max=255)
	@NotEmpty
	public String getDocId() {
		return docId;
	}
	
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	@NotEmpty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@NotNull
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@NotNull
	@Type(type="localeId")
	public LocaleId getLocale() {
		return locale;
	}
	
	public void setLocale(LocaleId locale) {
		this.locale = locale;
	}
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="project_id", nullable=false)
	@NaturalId
	public HProjectContainer getProject() {
		return project;
	}
	
	public void setProject(HProjectContainer project) {
		this.project = project;
	}

	@NotNull
	public Integer getRevision() {
		return revision;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	@Transient
	public void incrementRevision() {
		revision++;
	}

	@Type(type="contentType")
	@NotNull
	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	@OneToMany(mappedBy="document", cascade=CascadeType.ALL)
	@MapKey(name="resId")
	// all resources, obsolete or not, inside containers or not
	public Map<String,HDocumentResource> getAllResources() {
		if(allResources == null)
			allResources = new HashMap<String, HDocumentResource>();
		return allResources;
	}
	
	public void setAllResources(Map<String, HDocumentResource> resources) {
		this.allResources = resources;
	}
	
	@OneToMany(cascade=CascadeType.ALL/*, mappedBy="document"*/)
	@Where(clause="parent_id is null and obsolete=0")
	@IndexColumn(name="pos", base=0, nullable=true)// see http://opensource.atlassian.com/projects/hibernate/browse/HHH-4390?focusedCommentId=30964&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_30964
	@JoinColumn(name="document_id",nullable=true)
	/**
	 * Returns the <b>top-level</b> resources contained in the document.  Some 
	 * of these resources may be containers containing other resources.
	 */
	public List<HDocumentResource> getResources() {
		if(resources == null)
			resources = new ArrayList<HDocumentResource>();
		return resources;
	}
	
	public void setResources(List<HDocumentResource> resources) {
		this.resources = resources;
	}

	public boolean isObsolete() {
		return obsolete;
	}
	
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}
	
	@OneToMany(cascade=CascadeType.ALL)
	@Where(clause="obsolete=1")
	@MapKey(name="resId")
	public Map<String, HDocumentResource> getObsoletes() {
		if(obsoletes == null)
			obsoletes = new HashMap<String, HDocumentResource>();
		return obsoletes;
	}
	
	public void setObsoletes(Map<String, HDocumentResource> obsoletes) {
		this.obsoletes = obsoletes;
	}
	public Document toDocument(boolean deep) {
		if (deep)
			return toDocument(Integer.MAX_VALUE);
		else
			return toDocument(0);
	}
	
	public Document toDocument(int levels) {
	    Document doc = new Document(docId, name, path, contentType, revision, locale);
	    if (levels != 0) {
		    List<DocumentResource> docResources = doc.getResources(true);
		    for (HDocumentResource hRes : this.getResources()) {
				docResources.add(hRes.toResource(levels));
			}
		    // TODO handle extensions
	//	    List<Object> docExtensions = doc.getExtensions();
	    }
		return doc;
	}
}
