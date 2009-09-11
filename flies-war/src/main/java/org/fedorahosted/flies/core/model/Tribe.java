package org.fedorahosted.flies.core.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * A tribe represents people with a common language/locale
 * 
 * @author asgeirf
 *
 */
@Entity
public class Tribe extends AbstractFliesEntity{

	private HFliesLocale locale;
	
	private HPerson chief;
	private Set<HPerson> tribalLeaders;
	private Set<HPerson> members;

	
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "localeId")
	public HFliesLocale getLocale() {
		return locale;
	}
	
	public void setLocale(HFliesLocale locale) {
		this.locale = locale;
	}
	
	@ManyToOne
	@JoinColumn(name = "chiefId")
	public HPerson getChief() {
		return chief;
	}

	public void setChief(HPerson chief) {
		this.chief = chief;
	}
	
	@ManyToMany
	@JoinTable(name = "Tribe_Leader", joinColumns = @JoinColumn(name = "tribeId"), inverseJoinColumns = @JoinColumn(name = "personId"))
	public Set<HPerson> getTribalLeaders() {
		return tribalLeaders;
	}
	
	public void setTribalLeaders(Set<HPerson> tribalLeaders) {
		this.tribalLeaders = tribalLeaders;
	}
	
	@ManyToMany
	@JoinTable(name = "Tribe_Member", joinColumns = @JoinColumn(name = "tribeId"), inverseJoinColumns = @JoinColumn(name = "personId"))
	public Set<HPerson> getMembers() {
		return members;
	}
	
	public void setMembers(Set<HPerson> members) {
		this.members = members;
	}
	
}
