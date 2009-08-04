package org.fedorahosted.flies.webtrans.action;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import net.openl10n.packaging.jpa.document.HTextFlowTarget;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("flies.tftDataModel")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class TextFlowTargetDataModel extends
		BaseModifiableHibernateDataModel<HTextFlowTarget> {

	@In(value = "#{entityManager.delegate}")
	private Session hibernateSession;

	private Converter rowKeyConverter = new Converter() {

		@Override
		public String getAsString(FacesContext context, UIComponent component,
				Object value) {
			if (value == null) {
				return null;
			}
			if (value instanceof String) {
				return (String) value;
			}
			return String.valueOf(value);
		}

		@Override
		public Object getAsObject(FacesContext context, UIComponent component,
				String value) {
			if (value == null) {
				return null;
			}
			return hibernateSession.get(HTextFlowTarget.class, Long.valueOf(value));
		}
	};

	public TextFlowTargetDataModel() {
		super(HTextFlowTarget.class);
	}

	@Override
	protected Session getSession() {
		return hibernateSession;
	}

	public Converter getRowKeyConverter() {
		return rowKeyConverter;
	}

}
