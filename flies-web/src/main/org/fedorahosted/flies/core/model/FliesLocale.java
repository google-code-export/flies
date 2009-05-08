package org.fedorahosted.flies.core.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.fedorahosted.flies.type.ULocaleType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.ibm.icu.util.ULocale;

@Entity
@TypeDef(name = "ulocale", typeClass = ULocaleType.class)
public class FliesLocale implements Serializable {

	private String id;
	private ULocale locale;

	private FliesLocale parent;
	private List<FliesLocale> children;

	private Tribe tribe;
	
	private List<FliesLocale> friends; // e.g. nn, nb.

	public FliesLocale() {
	}

	public FliesLocale(ULocale locale) {
		setLocale(locale);
	}

	@OneToOne(mappedBy="locale")
	public Tribe getTribe() {
		return tribe;
	}
	
	public void setTribe(Tribe tribe) {
		this.tribe = tribe;
	}
	
	public static String getFliesId(ULocale locale) {
		StringBuilder builder = new StringBuilder();
		builder.append(locale.getLanguage());
		if (!locale.getCountry().isEmpty()) {
			builder.append('-');
			builder.append(locale.getCountry());
		}
		if (!locale.getScript().isEmpty()) {
			builder.append('-');
			builder.append(locale.getScript());
		}
		if (!locale.getVariant().isEmpty()) {
			builder.append('-');
			builder.append(locale.getVariant());
		}

		return builder.toString();
	}

	@Id
	@Length(max = 80, min = 1)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "icu_locale_id")
	@Type(type = "ulocale")
	public ULocale getLocale() {
		return locale;
	}

	public void setLocale(ULocale locale) {
		this.locale = locale;
		setId(getFliesId(locale));
	}

	@ManyToMany
	@JoinTable(name = "FliesLocale_Friends", joinColumns = @JoinColumn(name = "locale_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
	public List<FliesLocale> getFriends() {
		return friends;
	}

	public void setFriends(List<FliesLocale> friends) {
		this.friends = friends;
	}

	@OneToMany(mappedBy = "parent")
	public List<FliesLocale> getChildren() {
		return children;
	}

	public void setChildren(List<FliesLocale> children) {
		this.children = children;
	}

	@ManyToOne
	@JoinColumn(name = "parent_id")
	public FliesLocale getParent() {
		return parent;
	}

	public void setParent(FliesLocale parent) {
		this.parent = parent;
	}

	private static int findLocId(ULocale fallback, ULocale[] locales) {
		for (int i = 0; i < locales.length; i++) {
			if (locales[i].equals(fallback))
				return i;
		}
		return -1;
	}

	@Transient
	public String getNativeName() {
		return locale.getDisplayName(locale);
	}
}
