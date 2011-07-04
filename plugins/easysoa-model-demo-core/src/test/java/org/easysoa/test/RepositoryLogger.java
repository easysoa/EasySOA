package org.easysoa.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;


/**
 * Debugging tool that traces document structures
 * @author mkalam-alami
 *
 */
public class RepositoryLogger {

    private static final Log log = LogFactory.getLog(RepositoryLogger.class);
	private static final int INDENT_STEP = 2; // in spaces

	private CoreSession session;
	private String title;

	public RepositoryLogger(CoreSession session) {
		this(session, "Repository contents");
	}
	
	public RepositoryLogger(CoreSession session, String title) {
		this.session = session;
		this.title = title;
	}

	public void logAllRepository() {
		try {
			logDocumentAndChilds(session.getRootDocument());
		} catch (ClientException e) {
			log.error("Failed to log a document", e);
		}
	}
	
	public void logDocumentAndChilds(DocumentModel model) {
		try {
			// Header
			String separator = getDashes(title.length());
			log.info(separator);
			log.info(title);
			log.info(separator);
			
			// Contents
			logDocumentAndChilds(model, 0);
		} catch (ClientException e) {
			log.error("Failed to log document or a document child", e);
		}
	}

	private void logDocumentAndChilds(DocumentModel model, int indent) throws ClientException {
		
		// Log document
		String line = getSpaces(indent) + "* ["+model.getType()+"] "+model.getTitle();
		log.info(line);
		
		// Recursive calls
		DocumentModelList list = session.getChildren(model.getRef());
		for (DocumentModel childModel : list) {
			logDocumentAndChilds(childModel, indent+INDENT_STEP);
		}
	}
	
	private String getDashes(int length) {
		return getCharSuite('-', length);
	}
	
	private String getSpaces(int length) {
		return getCharSuite(' ', length);
	}
	
	private String getCharSuite(char c, int length) {
		String line = "";
		for (int i = 0; i< length; i++) {
			line += c;
		}
		return line;
	}
	
}
