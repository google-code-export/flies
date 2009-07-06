package net.openl10n.packaging.jpa.document;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.openl10n.adapters.LocaleId;
import net.openl10n.packaging.document.ContentTarget;
import net.openl10n.packaging.document.TextFlow;
import net.openl10n.packaging.document.TextFlowTarget;
import net.openl10n.packaging.document.TextFlowTarget.ContentState;
import net.openl10n.packaging.jpa.LocaleIdType;
import net.openl10n.packaging.jpa.comments.HSimpleComment;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.NotNull;

/**
 * Represents a flow of text that should be processed as a
 * stand-alone structural unit. 
 *
 * @author Asgeir Frimannsson <asgeirf@redhat.com>
 *
 */
@Entity
@TypeDef(name="localeId", typeClass=LocaleIdType.class)
public class HTextFlowTarget implements Serializable{

	private static final long serialVersionUID = 302308010797605435L;

	private Long id;
	
	private HTextFlow textFlow;
	private LocaleId locale;
	
	private String content;
	private ContentState state = ContentState.New;
	private Long revision = 1l;
	
	private HSimpleComment comment;
	
	private HDocumentTarget documentTarget; 
	
	public HTextFlowTarget() {
	}
	
	public HTextFlowTarget(HDocumentTarget docTarget, HTextFlow textFlow) {
		if(docTarget.getTemplate() != textFlow.getDocument()){
			throw new IllegalStateException("docTarget and textFlow must reference same Document");
		}
		this.documentTarget = docTarget;
		this.locale = docTarget.getLocale();
		this.textFlow = textFlow;
		this.revision = textFlow.getRevision();
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	protected void setId(Long id) {
		this.id = id;
	}
	
	public void copy(TextFlowTarget tfTarget){
		this.content = tfTarget.getContent();
		this.state = tfTarget.getState();
		this.revision = tfTarget.getVersion();
	}
	
	@NaturalId
	@Type(type="localeId")
	@NotNull
	public LocaleId getLocale() {
		return locale;
	}
	
	public void setLocale(LocaleId locale) {
		this.locale = locale;
	}
	
	@NotNull
	public ContentState getState() {
		return state;
	}
	
	public void setState(ContentState state) {
		this.state = state;
	}

	@NotNull
	public Long getRevision() {
		return revision;
	}
	
	public void setRevision(Long revision) {
		this.revision = revision;
	}
	
	@NaturalId
	@OneToOne
	@JoinColumn(name="resource_id")
	public HTextFlow getTextFlow() {
		return textFlow;
	}
	
	public void setTextFlow(HTextFlow textFlow) {
		this.textFlow = textFlow;
	}

	@ManyToOne
	@JoinColumn(name="document_target_id", nullable=false)
	public HDocumentTarget getDocumentTarget() {
		return documentTarget;
	}

	public void setDocumentTarget(HDocumentTarget documentTarget) {
		this.documentTarget = documentTarget;
	}
	
	@NotNull
	@Type(type = "text")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@OneToOne(mappedBy="target", optional=true, cascade=CascadeType.ALL)
	public HSimpleComment getComment() {
		return comment;
	}
	
	public void setComment(HSimpleComment comment) {
		this.comment = comment;
	}

	
}
