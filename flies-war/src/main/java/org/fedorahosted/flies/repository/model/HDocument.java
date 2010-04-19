package org.fedorahosted.flies.repository.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.fedorahosted.flies.common.ContentType;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.core.model.AbstractFliesEntity;
import org.fedorahosted.flies.hibernate.type.ContentTypeType;
import org.fedorahosted.flies.hibernate.type.LocaleIdType;
import org.fedorahosted.flies.repository.model.po.HPoHeader;
import org.fedorahosted.flies.repository.model.po.HPoTargetHeader;
import org.fedorahosted.flies.repository.model.po.PoUtility;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.TextFlow;
import org.fedorahosted.flies.rest.dto.po.HeaderEntry;
import org.fedorahosted.flies.rest.dto.po.PoHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeaders;
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

	private Map<String, HTextFlow> allResources;
	private Map<String, HTextFlow> obsoletes;
	private List<HTextFlow> resources;
	private boolean obsolete = false;
	private HPoHeader poHeader;
	private Map<LocaleId, HPoTargetHeader> poTargetHeaders;
	
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

	public HTextFlow create(TextFlow res, int nextDocRev){
		HTextFlow tf = new HTextFlow(res, nextDocRev);
		// don't add to this.resources, we could be in a subcontainer
		getAllResources().put(tf.getResId(), tf);
		tf.setDocument(this);
		return tf;
	}
	
	// TODO make this case sensitive
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
	public Map<String,HTextFlow> getAllResources() {
		if(allResources == null)
			allResources = new HashMap<String, HTextFlow>();
		return allResources;
	}
	
	public void setAllResources(Map<String, HTextFlow> resources) {
		this.allResources = resources;
	}
	
	@OneToMany(cascade=CascadeType.ALL/*, mappedBy="document"*/)
	@Where(clause="obsolete=0")
	@IndexColumn(name="pos", base=0, nullable=true)// see http://opensource.atlassian.com/projects/hibernate/browse/HHH-4390?focusedCommentId=30964#action_30964
	@JoinColumn(name="document_id",nullable=true)
	/**
	 * Returns the <b>top-level</b> resources contained in the document.  Some 
	 * of these resources may be containers containing other resources.
	 */
	public List<HTextFlow> getResources() {
		if(resources == null)
			resources = new ArrayList<HTextFlow>();
		return resources;
	}
	
	public void setResources(List<HTextFlow> resources) {
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
	public Map<String, HTextFlow> getObsoletes() {
		if(obsoletes == null)
			obsoletes = new HashMap<String, HTextFlow>();
		return obsoletes;
	}
	
	public void setObsoletes(Map<String, HTextFlow> obsoletes) {
		this.obsoletes = obsoletes;
	}
	public Document toDocument(boolean deep) {
		if (deep)
			return toDocument(Integer.MAX_VALUE);
		else
			return toDocument(0);
	}
	
	public void setPoHeader(HPoHeader poHeader) {
		this.poHeader = poHeader;
	}
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=true)
	public HPoHeader getPoHeader() {
		return poHeader;
	}
	
	public void setPoTargetHeaders(Map<LocaleId, HPoTargetHeader> poTargetHeaders) {
		this.poTargetHeaders = poTargetHeaders;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="document")
	@MapKey(name="targetLanguage")
	public Map<LocaleId, HPoTargetHeader> getPoTargetHeaders() {
		if (poTargetHeaders == null)
			poTargetHeaders = new HashMap<LocaleId, HPoTargetHeader>();
		return poTargetHeaders;
	}
	
	public Document toDocument(int levels) {
	    Document doc = new Document(docId, name, path, contentType, revision, locale);
	    if (levels != 0) {
		    List<TextFlow> docResources = doc.getResources(true);
		    for (HTextFlow hRes : this.getResources()) {
				docResources.add(hRes.toResource(levels));
			}
		    HPoHeader fromPoHeader = this.getPoHeader();
		    if (fromPoHeader != null) {
		    	PoHeader toPoHeader = doc.getOrAddExtension(PoHeader.class);
		    	String fromComment = fromPoHeader.getComment() != null ? fromPoHeader.getComment().getComment() : null;
		    	toPoHeader.setComment(fromComment);
		    	List<HeaderEntry> toEntries = toPoHeader.getEntries();
		    	toEntries.addAll(PoUtility.headerToList(fromPoHeader.getEntries()));
		    }
		    Collection<HPoTargetHeader> fromTargetHeaders = this.getPoTargetHeaders().values();
		    if (!fromTargetHeaders.isEmpty()) {
			    PoTargetHeaders toTargetHeaders = doc.getOrAddExtension(PoTargetHeaders.class);
			    for (HPoTargetHeader fromHeader : fromTargetHeaders) {
			    	PoTargetHeader toHeader = new PoTargetHeader();
			    	String fromComment = fromHeader.getComment() != null ? fromHeader.getComment().getComment() : null;
			    	toHeader.setComment(fromComment);
			    	List<HeaderEntry> toEntries = toHeader.getEntries();
			    	toEntries.addAll(PoUtility.headerToList(fromHeader.getEntries()));
			    	toHeader.setTargetLanguage(fromHeader.getTargetLanguage());
			    	toTargetHeaders.getHeaders().add(toHeader);
				}
		    }		    
	    }
		return doc;
	}
}
