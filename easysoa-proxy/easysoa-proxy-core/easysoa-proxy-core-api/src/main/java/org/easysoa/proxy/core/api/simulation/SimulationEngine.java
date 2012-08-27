/**
 * EasySOA Proxy Core
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

package org.easysoa.proxy.core.api.simulation;

import java.util.List;
import java.util.Map;

import org.easysoa.proxy.core.api.template.TemplateEngine;
import org.easysoa.records.ExchangeRecord;

/**
 * 
 * @author jguillemotte
 *
 */
public interface SimulationEngine {

    /**
     * Get a simulation store from suggestions
     * @return A simulation store
     */
    public SimulationStore getSimulationStoreFromSuggestion(String storeName, List<ExchangeRecord> exchangeRecordList);
    
    /**
     * Get an existing simulation store
     * @return A simulation store
     * @throws Exception 
     */
    public SimulationStore getExistingSimulationStore(String storeName, String recordID) throws Exception;
    
    /**
     * Process a simulation
     * @param inputRecord The input exchange record
     * @param simulationStore The simulation store to use for data matching
     * @param method The simulation method to use
     * @param templateEngine the template engine to use
     * @return A exchange record containing the simulated response
     * @throws Exception If a problem occurs
     */
    public ExchangeRecord simulate(ExchangeRecord inputRecord, SimulationStore simulationStore, SimulationMethod method, TemplateEngine templateEngine, Map<String, List<String>> fieldValues)
            throws Exception;
    
}
