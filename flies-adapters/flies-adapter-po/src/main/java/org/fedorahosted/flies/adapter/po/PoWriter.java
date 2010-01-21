package org.fedorahosted.flies.adapter.po;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.resources.OutputSource;
import org.fedorahosted.flies.rest.dto.Document;
import org.fedorahosted.flies.rest.dto.DocumentResource;
import org.fedorahosted.flies.rest.dto.IExtensible;
import org.fedorahosted.flies.rest.dto.SimpleComment;
import org.fedorahosted.flies.rest.dto.TextFlow;
import org.fedorahosted.flies.rest.dto.TextFlowTarget;
import org.fedorahosted.flies.rest.dto.TextFlowTargets;
import org.fedorahosted.flies.rest.dto.po.HeaderEntry;
import org.fedorahosted.flies.rest.dto.po.PoHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeader;
import org.fedorahosted.flies.rest.dto.po.PoTargetHeaders;
import org.fedorahosted.flies.rest.dto.po.PotEntryData;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;

public class PoWriter {

	private final org.fedorahosted.tennera.jgettext.PoWriter poWriter = new org.fedorahosted.tennera.jgettext.PoWriter();
	
	public PoWriter() {
	}
	
	public void write(final Document doc, final File baseDir) throws IOException {
		Set<LocaleId> targetLangs = new HashSet<LocaleId>();
		if (doc.hasResources()) {
			for (DocumentResource resource : doc.getResources()) {
				if (resource instanceof TextFlow) {
					TextFlow textflow = (TextFlow) resource;
					for (TextFlowTarget target : getTargets(textflow)) {
						targetLangs.add(target.getLang());
					}
				} else {
					throw new RuntimeException("unsupported Document element: "+resource.getClass());
				}
			}
		}
		// write the POT file to pot/$name.pot
		{
			File potDir = new File(baseDir, "pot");
			potDir.mkdirs();
			File potFile = new File(potDir, doc.getName()+".pot");
			OutputSource outputSource = new OutputSource(potFile);
			write(doc, outputSource, null);
		}
		// write the PO files to $locale/$name.po
		for (LocaleId locale : targetLangs) {
			File localeDir = new File(baseDir, locale.toString());
			localeDir.mkdirs();
			File poFile = new File(localeDir, doc.getName()+".po");
			OutputSource outputSource = new OutputSource(poFile);
			write(doc, outputSource, locale);
		}
	}
	
	public void write(final Document document, OutputSource outputSource, LocaleId locale) throws IOException{

		final Writer writer;

		if(document == null)
			throw new IllegalArgumentException("document");
		
		// the writer has first priority
		if(outputSource.getWriter() != null)
			writer = outputSource.getWriter();
		else if (outputSource.getOutputStream() != null) { // outputstream has 2nd priority
			if(outputSource.getEncoding() != null){
				writer = new OutputStreamWriter(outputSource.getOutputStream(), Charset.forName(outputSource.getEncoding()));
			}
			else{
				writer = new OutputStreamWriter(outputSource.getOutputStream() );
			}
		}
		else if(outputSource.getFile() != null){ // file has 3rd priority
			try{
				OutputStream os = new BufferedOutputStream(new FileOutputStream(outputSource.getFile()));
				if(outputSource.getEncoding() != null){
					writer = new OutputStreamWriter(os, Charset.forName(outputSource.getEncoding()));
				}
				else{
					writer = new OutputStreamWriter(os);
				}
			}
			catch(FileNotFoundException fnf){
				throw new IllegalArgumentException("localeOutputSourcePair", fnf);
			}
		}
		else
			throw new IllegalArgumentException("localeOutputSourcePair");
		
		
//		if(!document.getTargetLanguages().contains(locale))
//			throw new RuntimeException("could not find target locale");

		PoHeader potHeader = document.getExtension(PoHeader.class);
		
		HeaderFields hf = new HeaderFields();
		for(HeaderEntry e : potHeader.getEntries()){
			hf.setValue(e.getKey(), e.getValue());
		}
		Message message;
		if (locale != null) {
			PoTargetHeaders poHeaders = document.getExtension(PoTargetHeaders.class);
			if (poHeaders != null) {
				PoTargetHeader poHeader = poHeaders.getByLocale(locale);
				for(HeaderEntry e : poHeader.getEntries()){
					hf.setValue(e.getKey(), e.getValue());
				}
				message = hf.unwrap();
				for(String s : poHeader.getComment().getValue().split("\n")){
					message.addComment(s);
				}
				poWriter.write(message, writer);
				writer.write("\n");
			}
		} else {
			message = hf.unwrap();
			poWriter.write(message, writer);
			writer.write("\n");
		}

		// first write header
		if(!document.hasResources())
			return;
		for(DocumentResource resource : document.getResources()){
			TextFlow textFlow = (TextFlow) resource;

			PotEntryData entryData = textFlow.getExtension(PotEntryData.class);
			message = new Message();
			message.setMsgid(textFlow.getContent());
			message.setMsgstr("");
			if (locale != null) {
				TextFlowTargets entryTargets = textFlow.getExtension(TextFlowTargets.class);
				if (entryTargets != null) {
					TextFlowTarget contentData = entryTargets.getByLocale(locale);
					if (contentData != null) {
						if(!entryData.getId().equals(textFlow.getId()) || ! contentData.getId().equals(textFlow.getId())){
							throw new RuntimeException("hey, expected something else here!");
						}
						message.setMsgstr(contentData.getContent());
						SimpleComment poComment = contentData.getExtension(SimpleComment.class);
						if (poComment != null) {
							String [] comments = poComment.getValue().split("\n");
							if(comments.length == 1 && comments[0].isEmpty()){
								
							}
							else{
								for(String comment : comments){
									message.getComments().add(comment);
								}
							}
						}						
						switch(contentData.getState()){
						case Approved:
							message.setFuzzy(false);
							break;
						case NeedReview:
						case New:
							message.setFuzzy(true);
							break;
						}
					}
				}
			}
			
			copyToMessage(entryData, message);
			
			poWriter.write(message, writer);
			writer.write("\n");
		}
	}
	
	private static void copyToMessage(PotEntryData data, Message message){
		String context = data.getContext();
		if(context != null)
			message.setMsgctxt(context);
		String [] comments = StringUtils.splitPreserveAllTokens(data.getExtractedComment().getValue(), "\n");
		if(!(comments.length == 1 && comments[0].isEmpty())){
			for(String comment : comments){
				message.addExtractedComment(comment);
			}
		}
		for(String flag : data.getFlags()){
			message.addFormat(flag);
		}
		for(String ref : data.getReferences()){
			message.addSourceReference(ref);
		}
	}

	private static Set<TextFlowTarget> getTargets(IExtensible resource) {
		TextFlowTargets targets = resource.getExtension(TextFlowTargets.class);
		if (targets != null) {
			return targets.getTargets();
		} else {
			return Collections.EMPTY_SET;
		}
	}
}
