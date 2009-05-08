package org.fedorahosted.flies.core.action;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import org.fedorahosted.tennera.jgettext.*;
import org.fedorahosted.tennera.jgettext.catalog.parse.ParseException;

import org.fedorahosted.flies.core.model.Pofile;

@Name("downloadfile")
public class DownloadFile {
	
	@Logger
	private Log log;
	
	@In
	private EntityManager entityManager;
	
	@In(value="#{facesContext.externalContext}")
	private ExternalContext extCtx;
	
	@In(value="#{facesContext}")
	FacesContext facesContext;
	
	@RequestParameter
	private Long pofileId;

        private String ParseFile(File file) throws ParseException, IOException{
		PoParser poParser = new PoParser();
		PoWriter poWriter = new PoWriter();
		Catalog fileCatalog = poParser.parseCatalog(file);
		StringWriter outputWriter = new StringWriter();
		poWriter.write(fileCatalog, outputWriter);
		outputWriter.flush();
		return outputWriter.toString();
	}
	
	public String download() {
		Pofile pofile = entityManager.find(Pofile.class, pofileId);
		HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();
		response.setContentType(pofile.getContentType());
                response.addHeader("Content-disposition", "attachment; filename=\"" + pofile.getName() +"\"");
		try {
			ServletOutputStream os = response.getOutputStream();
                        //Create a temp file for parsing in JGettext
                        File temp = new File(pofile.getName());
            		FileOutputStream f = new FileOutputStream(temp);
            		f.write(pofile.getData());
                        String result = ParseFile(temp);
			os.print(result);
			os.flush();
			os.close();
			facesContext.responseComplete();
		} catch(Exception e) {
			log.error("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}
}
