package org.fedorahosted.flies.rest.dto.v1;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fedorahosted.flies.common.ContentState;
import org.fedorahosted.flies.common.Namespaces;

@XmlType(name="textFlowTargetType", namespace=Namespaces.FLIES, propOrder={"translator", "content", "extensions"})
@XmlRootElement(name="target", namespace=Namespaces.FLIES)
public class TextFlowTarget {

	private ContentState state = ContentState.New;
	private Person translator;
	private String content;
	private ExtensionSet extensions;
	
	
	@XmlElement(name="translator", namespace=Namespaces.FLIES, required=true)
	public Person getTranslator() {
		return translator;
	}
	
	public void setTranslator(Person translator) {
		this.translator = translator;
	}
	
	@XmlAttribute(name="state", required=true)
	public ContentState getState() {
		return state;
	}
	
	public void setState(ContentState state) {
		this.state = state;
	}

	@XmlElement(name="content",namespace=Namespaces.FLIES, required=true)
	public String getContent() {
		if(content == null)
			return "";
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@XmlElementWrapper(name="extensions", namespace=Namespaces.FLIES, required=false, nillable=false)
	@XmlAnyElement(lax=true)
	public ExtensionSet getExtensions() {
		if(extensions == null)
			extensions = new ExtensionSet();
		return extensions;
	}
	
	
}
