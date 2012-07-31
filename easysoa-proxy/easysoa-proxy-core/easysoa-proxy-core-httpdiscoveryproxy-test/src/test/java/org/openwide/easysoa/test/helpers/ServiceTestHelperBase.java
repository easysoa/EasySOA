/**
 * EasySOA Proxy
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

package org.openwide.easysoa.test.helpers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.easysoa.nuxeo.registration.NuxeoRegistrationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Put here everything that has to be impl'd differently according to mock setup
 * (ex. FullMock, PartiallyMocked...), in addition to global test helper methods
 * 
 * @author mdutoo
 *
 */
public abstract class ServiceTestHelperBase {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ServiceTestHelperBase.class.getName());
	
    /**
     * Wait x ms for services (ex. nuxeo registry) to have done their work (ex. service registration)
     * To be impl'd accordingly (ex. does nothing when full mock)
     * @param i
     */
	protected abstract void waitForServices(int i) throws InterruptedException;
	
	/**
	 * Cleans registry from nodes having a dc:title, api:url or serv:url like the given one
     * To be impl'd accordingly (ex. does nothing when full mock)
	 * @param urlPattern
	 * @throws JSONException
	 */
	protected abstract String cleanNuxeoRegistry(String urlPattern) throws Exception;
	 
	/**
	 * Cleans registry from nodes having a dc:title, api:url or serv:url like the given one
	 * @throws Exception 
	 */
	public static String cleanRemoteNuxeoRegistry(String urlPattern) throws Exception  {
		// Not possible NXQL to select only one field, only select * is available ..
		StringBuffer nuxeoQueryBuf = new StringBuffer("SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted'");
		nuxeoQueryBuf.append(" AND (ecm:primaryType = 'Service' OR ecm:primaryType = 'ServiceAPI' OR ecm:primaryType = 'Workspace')");
		if (urlPattern != null && urlPattern.trim().length() != 0) {
			// dont' clean everything, only what's required
			nuxeoQueryBuf.append("AND (dc:title LIKE '"); // title rather than app:url, else "Default application" could be deleted
			nuxeoQueryBuf.append(urlPattern);
			nuxeoQueryBuf.append("' OR api:url LIKE '");
			nuxeoQueryBuf.append(urlPattern);
			nuxeoQueryBuf.append("' OR serv:url LIKE '");
			nuxeoQueryBuf.append(urlPattern);
			nuxeoQueryBuf.append("')");
		}
		String nuxeoQuery = nuxeoQueryBuf.toString();
		
        try {
            NuxeoRegistrationService nrs = new NuxeoRegistrationService();

            String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
            // For each documents returned by the query, call the delete method
            // Need to parse the complete JSON response to find all the document uid to delete
            String entries = new JSONObject(nuxeoResponse).getString("entries");
            JSONArray entriesArray = new JSONArray(entries);
            for(int i =0; i < entriesArray.length(); i++){
                JSONObject entry = entriesArray.getJSONObject(i);
                String uid = entry.getString("uid");
                logger.info("Deleting document with uid = " + uid);
                if(nrs.deleteQuery(uid)){
                    logger.info("Document doc:" + uid + " deleted successfully");
                } else {
                    logger.info("Unable to delete document doc:" + uid);
                }
            }
            
            // check that docs are well deleted
            return nuxeoResponse = nrs.sendQuery(nuxeoQuery);
            
        } catch (IOException e) {
            String errMsg = "Failed to delete documents";
            logger.error(errMsg, e);
            return errMsg;
        }
		
	}
	
}
