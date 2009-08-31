package org.fedorahosted.flies.webtrans.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;


import org.fedorahosted.flies.LocaleId;
import org.fedorahosted.flies.core.model.Account;
import org.fedorahosted.flies.core.model.FliesLocale;
import org.fedorahosted.flies.core.model.Person;
import org.fedorahosted.flies.core.model.ProjectIteration;
//import org.fedorahosted.flies.repository.model.TextUnitTarget;
import org.fedorahosted.flies.repository.model.document.HDocumentTarget;
import org.fedorahosted.flies.repository.model.document.HTextFlowTarget;
import org.fedorahosted.flies.repository.model.project.HProject;
import org.fedorahosted.flies.webtrans.NoSuchWorkspaceException;
import org.fedorahosted.flies.webtrans.TranslationWorkspace;
import org.fedorahosted.flies.webtrans.TranslationWorkspaceManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.richfaces.model.selection.SimpleSelection;

@Name("translateAction")
@Scope(ScopeType.CONVERSATION)
@Restrict("#{identity.loggedIn}")
public class TranslateAction {

	@RequestParameter("wid")
	private String workspaceId;
	
	@In(required=true)
	private TranslationWorkspaceManager translationWorkspaceManager;
	
	@In
	private EntityManager entityManager;

	@Logger
	private Log log;

	@In(required=false, value=JpaIdentityStore.AUTHENTICATED_USER) 
	Account authenticatedAccount;

	private LocaleId locale;
	private HProject project;

	public String getWorkspaceId() {
		return workspaceId;
	}
	
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}
	
	public HProject getProject() {
		return project;
	}
	
	public void setProject(HProject project) {
		this.project = project;
	}
	
	public LocaleId getLocale() {
		return locale;
	}

	@DataModel
	private List<HDocumentTarget> documentTargets;

	@DataModelSelection(value="documentTargets")
	@Out(required=false)
	private HDocumentTarget selectedDocumentTarget;

	@In(value="flies.tftDataModel")
	@MyDataModel
	private TextFlowTargetDataModel textFlowTargets;
	
	private HTextFlowTarget selectedTextFlowTarget;
	
	public HTextFlowTarget getSelectedTextFlowTarget() {
		return selectedTextFlowTarget;
	}
	
	private SimpleSelection selectedRow;
	
	public SimpleSelection getSelectedRow() {
		return selectedRow;
	}
	
	public void setSelectedRow(SimpleSelection selectedRow) {
		this.selectedRow = selectedRow;
		setSelectedTextFlowTarget((HTextFlowTarget) selectedRow.getKeys().next() );
	}
	
	public void setSelectedTextFlowTarget(HTextFlowTarget selectedTextFlowTarget) {
		this.selectedTextFlowTarget = selectedTextFlowTarget;
		log.info("set selected {0}", selectedTextFlowTarget.getId() );
	}
	
	public void selectDocumentTarget(){
		log.info("selected {0}", selectedDocumentTarget.getTemplate().getName());
		//loadTextFlowTargets();
	}
	
	@Factory("documentTargets")
	public void loadDocumentTargets(){
		documentTargets = entityManager.createQuery("select d from HDocumentTarget d " +
								"where d.locale = :locale and d.template.project = :project")
					.setParameter("locale", locale)
					.setParameter("project", project).getResultList();
	}

	//@Factory("textFlowTargets")
	public void loadTextFlowTargets(){
		log.info("retrieving textFlowTargets...");
		textFlowTargets = new TextFlowTargetDataModel();
		/*
		if(selectedDocumentTarget == null) {
			log.info("none available...");
			textFlowTargets =  Collections.EMPTY_LIST;
		}
		else {
			log.info("retrieving entries. count: {0} ", selectedDocumentTarget.getTargets().size());
			textFlowTargets =  new ArrayList<HTextFlowTarget>(selectedDocumentTarget.getTargets() );
		}
		*/
	}
	
	public void selectTextFlowTarget(){
		log.info("selected {0}", selectedTextFlowTarget.getId() );
	}

	
	public boolean isConversationActive(){
		return project != null && locale != null; 
	}
	
	public void initialize() {
		if(isConversationActive()) return;
		
		log.info("Initializing workspace for '{0}'", workspaceId);
		if(workspaceId == null){
			throw new NoSuchWorkspaceException();
		}
		else{
			String [] ws = workspaceId.split("/");
			if(ws.length != 2){
				throw new NoSuchWorkspaceException(workspaceId);
			}
			try{
				Long projectIterationId = Long.parseLong(ws[0]);
				String localeId = ws[1];
				project = entityManager.find(HProject.class, projectIterationId);
				locale = new LocaleId(localeId);
				Person translator = entityManager.find(Person.class, authenticatedAccount.getPerson().getId());
				getWorkspace().registerTranslator(translator);
			}
			catch(Exception e){
				throw new NoSuchWorkspaceException(workspaceId);
			}
		}
	}

	public TranslationWorkspace getWorkspace(){
		return translationWorkspaceManager.getOrRegisterWorkspace(project.getId(), locale);
	}
	
	@End
	@Destroy
	public void destroy() {
	}
	
	public boolean ping(){
		Person translator = entityManager.find(Person.class, authenticatedAccount.getPerson().getId());
		log.info("ping {3} - {0} - {2}", 
				this.project.getProjectId(), 
				this.locale,
				translator.getAccount().getUsername());
		getWorkspace().registerTranslator(translator);
		return true;
	}
	public void persistChanges(){
		if(selectedTextFlowTarget != null){
			entityManager.flush();
		}
	}
}
