/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.test;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

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

    private static final int INDENT_STEP = 2; // in spaces

    private static Log log = LogFactory.getLog(RepositoryLogger.class);
    
    private CoreSession session;
    private String title;
    private RepositoryLoggerMatcher matcher = new RepositoryLoggerMatcher() {
        public boolean matches(DocumentModel model) {
            return false;
        }
    };

    public RepositoryLogger(CoreSession session) {
        this(session, "Repository contents");
    }
    
    public RepositoryLogger(CoreSession session, String title) {
        this.session = session;
        this.title = title;
    }
    
    /**
     * Allows to define a matcher to set which documents
     * need to be logged in details.
     */
    public RepositoryLogger enableDetailedLoggingFor(RepositoryLoggerMatcher matcher) {
        this.matcher = matcher;
        return this;
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
            log.debug(separator);
            log.debug(title);
            log.debug(separator);
            
            // Contents
            logDocumentAndChilds(model, 0);
        } catch (ClientException e) {
            log.error("Failed to log document or a document child", e);
        }
    }

    private void logDocumentAndChilds(DocumentModel model, int indent) throws ClientException {
        
        // Log document
        if (matcher.matches(model)) {
            logDetailed(indent, model);
        }
        else {
            logBasic(indent, model);
        }
        
        // Recursive calls
        DocumentModelList list = session.getChildren(model.getRef());
        for (DocumentModel childModel : list) {
            logDocumentAndChilds(childModel, indent+INDENT_STEP);
        }
    }

    private void logBasic(int indent, DocumentModel model) {
        String line = getSpaces(indent) + "* ["+model.getType()+"] ";
        try {
            line += model.getTitle();
        } catch (ClientException e) {
            line += "!!(title unknown)!!";
        }
        log.debug(line);
    }

    private void logDetailed(int indent, DocumentModel model) {
        logBasic(indent, model);
        String spaces = getSpaces(indent);
        try {
            for (String schema : model.getDocumentType().getSchemaNames()) {
                StringBuffer line = new StringBuffer(spaces + "    | " + schema + "> ");
                Map<String, Object> schemaProperties = model.getProperties(schema);
                for (Entry<String, Object> entry : schemaProperties.entrySet()) {
                    Serializable value = (Serializable) schemaProperties.get(entry.getValue());
                    line.append(entry.getKey() + "=" + value + " ");
                }
                log.debug(line.toString());
            }
        }
        catch(Exception e) {
            log.debug(spaces + "(Failed to get more information: " + e.getMessage() + ")");
        }
    }
    
    private String getDashes(int length) {
        return getCharSuite('-', length);
    }
    
    private String getSpaces(int length) {
        return getCharSuite(' ', length);
    }
    
    private String getCharSuite(char c, int length) {
        StringBuffer line = new StringBuffer();
        for (int i = 0; i< length; i++) {
            line.append(c);
        }
        return line.toString();
    }
    
}
