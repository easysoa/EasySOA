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

package org.easysoa.proxy.core.api.records.replay;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.records.assertions.AssertionEngine;
import org.easysoa.proxy.core.api.simulation.SimulationEngine;
import org.easysoa.proxy.core.api.template.TemplateEngine;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;

public interface ReplayEngine {

    /**
     * Return an ExchangeReplayController
     * @return
     */
    //public ExchangeReplayController getExchangeReplayController();
    
    /**
     * Returns the template engine SCA reference
     * @return
     */
    public TemplateEngine getTemplateEngine();
    
    /**
     * Returns the template engine SCA reference
     * @return
     */
    public AssertionEngine getAssertionEngine();    
    
    /**
     * 
     * @param runName
     * @param environmentName
     * @throws InvalidParameterException
     * @throws IOException 
     */
    public void replayRunRecords(String runName, String environmentName) throws InvalidParameterException, IOException;
    
    /**
     * 
     * @return 
     */
    public String[] getAllRunNamesArray();
    
    /**
     * Starts a replay session
     * @param replaySessionName
     * @throws Exception If a problem occurs
     */
    public void startReplaySession(String replaySessionName) throws Exception;
    
    /**
     * Stop a replay session
     * @throws Exception If a problem occurs
     */
    public void stopReplaySession() throws Exception;
    
    /**
     * Get the exchange record corresponding to the ID and stored in the specified store 
     * @param exchangeRecordStoreName The store where the record is stored
     * @param exchangeID The exchange ID to get
     * @return An <code>ExchangeRecord</code>
     * @throws Exception If a problem occurs
     */
    public ExchangeRecord getExchangeRecord(String exchangeRecordStoreName, String exchangeID) throws Exception;    
    
    /**
     * Get the exchange records for a store
     * @param exchangeRecordStoreName The store name
     * @return A <code>RecordCollection</code> containing all the exchange record stored in the store
     * @throws Exception If a problem occurs 
     */
    public RecordCollection getExchangeRecordlist(String exchangeRecordStoreName) throws Exception;    

    /**
     * Get the list of exchange record stores
     * @return A <code>StoreCollection</code>
     * @throws Exception If a problem occurs
     */
    public StoreCollection getExchangeRecordStorelist() throws Exception;    

    /**
     * Get the template field suggestions for a complete store
     * @param storeName The store name
     * @param templateFieldSuggestionsName 
     * @return The suggestions in a <code>TemplateFieldSuggestions</code> 
     * @throws Exception If a problem occurs
     */
    public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String templateFieldSuggestionsName) throws Exception;    
    
    /**
     * Replay an exchange record without any modifications
     * @param exchangeRecordStoreName The store name where the exchange record is stored
     * @param exchangeRecordId The ID of the exchange record to replay
     * @return The <code>Map</code> containing the record id as key and the associated <code>OutMessage</code> as response
     * @throws Exception If a problem occurs 
     */
    public OutMessage replay(String exchangeRecordStoreName, String exchangeRecordId) throws Exception;

    /**
     * Render and replay a templatized exchange record
     * @param formData Custom data to use 
     * @param exchangeStoreName 
     * @param exchangeRecordID 
     * @return 
     * @throws Exception If a problem occurs
     */
    public OutMessage replayWithTemplate(Map<String, List<String>> formData, String exchangeStoreName, String exchangeRecordID) throws Exception;

    /**
     * Returns the simulation engine
     * @return The simulation engine
     */
    public SimulationEngine getSimulationEngine();

    /**
     * Replay a customized exchange record
     * @param formData Custom data to use with the template to obtain the customized exchange record
     * @param exchangeStoreName  
     * @param exchangeRecordID 
     * @param templateName Name of template to use
     * @return
     * @throws Exception If a problem occurs
     */
    //public String replayWithTemplate(MultivaluedMap<String, String> formData, String exchangeStoreName, String exchangeRecordID, String templateName) throws Exception;
    
    /**
     * 
     * @param runName
     * @throws Exception 
     */
    public String generateTemplateDefinition(String runName) throws Exception;
    
}