package org.fedorahosted.flies;

import java.io.File;
import java.io.FileNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.fedorahosted.flies.entity.Project;
import org.fedorahosted.flies.entity.ProjectSeries;
import org.fedorahosted.flies.entity.ProjectTarget;
import org.fedorahosted.flies.entity.resources.Document;
import org.fedorahosted.flies.entity.resources.TextUnitTarget;
import org.fedorahosted.flies.projects.publican.PublicanProjectAdapter;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.ExtendedCatalogParser;
import org.fedorahosted.tennera.jgettext.catalog.write.MessageProcessor;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;

import antlr.RecognitionException;
import antlr.TokenStreamException;

@Name("debugDataInitialization")
public class DebugDataInitialization {


   @In
   EntityManager entityManager;

   @Logger
   Log log;
   
   //@Observer("org.jboss.seam.postInitialization")
   @Transactional
   public void initializeDebugData() {
	   log.info("*************************** start observing!");
	   try{
		   Project project = (Project) entityManager.createQuery("Select p from Project p where p.uname = :uname")
		   				.setParameter("uname", "deploymentguide").getSingleResult();
		   log.info("Data already exists");
		   return;
	   }
	   catch(NoResultException e){
		   // continue
	   }
	   
	   Project project = new Project();
	   project.setName("RHEL Deployment Guide");
	   project.setUname("deploymentguide");
	   project.setShortDescription("A comprehensive manual for Red Hat Enterprise Linux");
	   entityManager.persist(project);

	   ProjectSeries series = new ProjectSeries();
	   series.setName("default");
	   series.setProject(project);
	   entityManager.persist(series);
	   
	   ProjectTarget target = new ProjectTarget();
	   target.setName("5.3");
	   target.setProject(project);
	   target.setProjectSeries(series);
	   entityManager.persist(target);
	   
	   File basePath = new File("/home/asgeirf/projects/Deployment_Guide");
	   PublicanProjectAdapter adapter = new PublicanProjectAdapter(basePath);
	   log.info(adapter.getBrandName());
	   log.info("*************************** end observing!");
      // Do your initialization here

	   for(String resource : adapter.getResources()){
		   final Document template = new Document();
		   template.setRevision(1);
		   template.setName(resource);
		   template.setProject(project);
		   template.setProjectTarget(target);
		   template.setContentType("pot");
		   entityManager.persist(template);
		   
		   File poFile = new File(new File(basePath, adapter.getResourceBasePath()), resource);
		   
		   log.info("Importing resource {0}", resource);
		   try{
			   ExtendedCatalogParser parser = new ExtendedCatalogParser(poFile);
			   parser.catalog();
			   Catalog catalog = parser.getCatalog();
			   catalog.processMessages(new Catalog.MessageProcessor(){
					public void processMessage(Message message) {
						if(!message.isHeader()){
							// create Template...
							TextUnitTarget tf = new TextUnitTarget();
							tf.setDocumentTemplate(template);
							tf.setContent(message.getMsgid());
							tf.setDocumentRevision(template.getRevision());
							entityManager.persist(tf);
						}
			    		log.info(message.getMsgid());
					}
			   });
		   }
		   catch(FileNotFoundException e){
			   log.error(e);
		   } catch (RecognitionException e) {
			   log.error(e);
		} catch (TokenStreamException e) {
			   log.error(e);
		}
		   
	   }
	   
	   entityManager.flush();
   }
   
}