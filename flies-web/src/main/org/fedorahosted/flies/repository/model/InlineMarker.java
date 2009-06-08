package org.fedorahosted.flies.repository.model;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class InlineMarker implements Serializable{

	private static final long serialVersionUID = -1805312081844909930L;

	private Long id;

	private TextFlow textFlow;
	
	@Id
	public Long getId() {
		return id;
	}
	
	protected void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "text_flow_id")
	@NotNull
	public TextFlow getTextFlow() {
		return textFlow;
	}
	
	public void setTextFlow(TextFlow textFlow) {
		this.textFlow = textFlow;
	}
	
}
