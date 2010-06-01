package org.fedorahosted.flies.rest.dto.v1;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fedorahosted.flies.common.Namespaces;
import org.hibernate.validator.Email;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@XmlType(name="personType", namespace=Namespaces.FLIES, propOrder={})
@XmlRootElement(name="person", namespace=Namespaces.FLIES)
public class Person {

	private String email;
	private String name;
	
	public Person() {
	}
	
	public Person(String email, String name) {
		this.email = email;
		this.name = name;
	}
	
	@XmlAttribute(name="email", required=true)
	@Email
	@NotNull
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@XmlAttribute(name="name", required=true)
	@NotEmpty
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
