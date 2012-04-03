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

package org.easysoa.simulation;

import java.util.List;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.correlation.CorrelationEngine;
import org.easysoa.records.correlation.FieldExtractor;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.simulation.SimulationStore.OrderingMethod;
import org.easysoa.template.TemplateFieldSuggestions;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Simulation engine
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class SimulationEngineImpl implements SimulationEngine {

    @Reference
    CorrelationEngine correlationEngine;
    
    //@Reference
    ProxyFileStore fileStore;
    
    /**
     * 
     */
    public SimulationEngineImpl() {
        fileStore = new ProxyFileStore();
    }
    
    @Override
    public SimulationStore getSimulationStoreFromSuggestion(List<ExchangeRecord> exchangeRecordList) {
        SimulationStore store = new SimulationStore();
        FieldExtractor extractor = new FieldExtractor();
        for(ExchangeRecord exchangeRecord : exchangeRecordList){
            //TODO : How to deal with template suggestions to have output templates
            TemplateFieldSuggestions suggestions = correlationEngine.correlateWithSubpath(exchangeRecord, 
                    extractor.getInputPathParams(exchangeRecord.getInMessage()), 
                    extractor.getInputQueryParams(exchangeRecord.getInMessage()),
                    extractor.getInputContentParam(exchangeRecord.getInMessage()),
                    extractor.getOutputFields(exchangeRecord.getOutMessage()));
        }
        return store;
    }

    @Override
    public SimulationStore getExistingSimulationStore(String storeName, String recordID) {
        //fileStore.getTemplateFieldSuggestions(storeName, recordID);
        return null;
    }

    /**
     * Match
     */
    private boolean match(){
        // check for each suggestion if match
        /*for(){
            
        }*/
        return false;
    }

    @Override
    public void simulate(SimulationStore simulationStore/*, SimulationMethod method*/) {
        // Sorting store by decreasing correlation level
        simulationStore.orderBy(OrderingMethod.DECREASING_CORRELATION_LEVEL);
        // 
        
        
    }

}
