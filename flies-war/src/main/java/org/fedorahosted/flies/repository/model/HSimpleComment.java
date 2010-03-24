package org.fedorahosted.flies.repository.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.fedorahosted.flies.rest.dto.SimpleComment;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

@Entity
public class HSimpleComment {
	
	private Long id;
	
	private String comment;
	
	public HSimpleComment() {
	}
	
	public HSimpleComment(String comment) {
		this.comment = comment;
	}
	
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	protected void setId(Long id) {
		this.id = id;
	}
	
	@NotNull
	@Type(type = "text")
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public static SimpleComment toSimpleComment(HSimpleComment comment) {
		String s = toString(comment);
		return s != null ? new SimpleComment(s) : null;
	}
	
	public static String toString(HSimpleComment comment) {
		return comment != null ? comment.getComment() : null;
	}
}
