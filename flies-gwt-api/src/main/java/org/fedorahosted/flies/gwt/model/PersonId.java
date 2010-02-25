package org.fedorahosted.flies.gwt.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//@ExposeEntity 
public final class PersonId implements IsSerializable, Serializable {
	
	private String id;
	
	// for ExposeEntity
	public PersonId() {
	}
	
	public PersonId(String id) {
		if(id == null || id.isEmpty()) {
			throw new IllegalStateException("Invalid Id");
		}
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof PersonId){
			return ((PersonId)obj).id.equals(id);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
