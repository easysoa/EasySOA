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

import java.util.List;
import java.util.Map;

/**
 * Main EasySOA API interface.
 * Allows to fetch documents and make notifications to the SOA model.
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOAApiSession {

    /**
     * Creates or updates an Appli Impl. given a set of properties
     * (see {@code org.easysoa.doctypes.AppliImpl} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    EasySOADocument notifyAppliImpl(Map<String, String> properties) throws Exception; // TODO More specific exceptions

    /**
     * Creates or updates a Service API given a set of properties
     * (see {@code org.easysoa.doctypes.ServiceAPI} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    EasySOADocument notifyServiceApi(Map<String, String> properties) throws Exception;

    /**
     * Creates or updates a Service given a set of properties
     * (see {@code org.easysoa.doctypes.Service} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    EasySOADocument notifyService(Map<String, String> properties) throws Exception;

    /**
     * Creates or updates a Service Reference given a set of properties
     * (see {@code org.easysoa.doctypes.ServiceReference} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    EasySOADocument notifyServiceReference(Map<String, String> properties) throws Exception;
    
    /**
     * Retrieves a list of documents
     * @param query A valid NXQL query
     * @return
     */
    List<EasySOADocument> queryDocuments(String query) throws Exception;
    
    /**
     * Retrieves a document given its ID
     * @param id
     * @return
     */
    EasySOADocument getDocumentById(String id) throws Exception;
    
}
