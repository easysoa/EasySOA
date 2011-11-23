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

package org.easysoa.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.properties.PropertyNormalizer;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

/**
 * Thin layer to ease the use of the Nuxeo automation API (currently for document queries only).
 * XXX Will eventually be replaced for end-users by the EasySOA API.
 * 
 * @author mkalam-alami
 *
 */
public class AutomationHelper {
    
    private static Log log = LogFactory.getLog(AutomationHelper.class);
    
    private static String QUERY_OPERATION_ID = "Document.Query";
    
    private HttpAutomationClient client = null;
    private Session session = null;
    
    public AutomationHelper(String nuxeoAutomationUrl, String username, String password) throws Exception { 
        client = new HttpAutomationClient(nuxeoAutomationUrl);
        try {
            session = client.getSession(username, password); 
        }
        catch (Exception e) {
            log.warn("Failed to create automation session: " + e.getMessage());
        }
    }

    public Documents findDocumentByUrl(String doctype, String url) throws Exception {
        return query("SELECT * FROM " + doctype + " WHERE " + 
            EasySOADoctype.getSchemaPrefix(doctype) + "url = '" + PropertyNormalizer.normalizeUrl(url) + "'");
    }
    
    public Documents query(String query) throws Exception {
        OperationRequest request = session.newRequest(QUERY_OPERATION_ID);
        request.setHeader("X-NXDocumentProperties", "*");
        request.set("query", query);
        return (Documents) request.execute();
    }
    
}
