package org.fedorahosted.flies.rest.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fedorahosted.flies.LocaleId;


@XmlType(name="containerType", namespace=Namespaces.FLIES, propOrder={"links", "content", "extensions"})
public class Container extends AbstractBaseResource implements Resource {

	private String id;
	private LocaleId lang;
	private long version = 1;
	
	private List<Object> extensions;
	private List<Resource> content;
	
	public Container() {
	}
	
	public Container(String id) {
		this.id = id;
	}
	
	public Container(String id, LocaleId lang) {
		this(id);
		this.lang = lang;
	}

	@XmlAttribute(name="id", required=true)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name="version", required=true)
	public long getVersion() {
		return version;
	}
	
	public void setVersion(long version) {
		this.version = version;
	}

	@XmlAnyElement(lax=true)
	public List<Object> getExtensions() {
		if(extensions == null)
			extensions = new ArrayList<Object>();
		return extensions;
	}
	
	@Override
	public <T> T getExtension(Class<T> clz){
		if(extensions == null)
			return null;
		for(Object o : extensions){
			if(clz.isInstance(o))
				return clz.cast(o);
		}
		return null;
	}
	
	@Override
	public <T> T getOrAddExtension(Class<T> clz) {
		T ext = getExtension(clz);
		if(ext == null){
			try {
				ext = clz.newInstance();
			} catch (Throwable e) {
				throw new RuntimeException("unable to create instance", e);
			}
		}
		return ext;
	}	
	
	
	@XmlJavaTypeAdapter(type=LocaleId.class, value=LocaleIdAdapter.class)
	@XmlAttribute(name="lang", namespace=Namespaces.XML, required=true)
	public LocaleId getLang() {
		return lang;
	}
	
	public void setLang(LocaleId lang) {
		this.lang = lang;
	}
	
	@XmlElementWrapper(name="content", namespace=Namespaces.FLIES, required=true)
	@XmlElements({
		@XmlElement(name="text-flow", type=TextFlow.class, namespace=Namespaces.FLIES),
		@XmlElement(name="container", type=Container.class, namespace=Namespaces.FLIES),
		@XmlElement(name="reference", type=Reference.class, namespace=Namespaces.FLIES),
		@XmlElement(name="data-hook", type=DataHook.class, namespace=Namespaces.FLIES)
		})
	public List<Resource> getContent() {
		if(content == null)
			content = new ArrayList<Resource>();
		return content;
	}
	
	@Override
	public String toString() {
		return Utility.toXML(this);
	}
	
}
