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

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.records.correlation.CorrelationEngine;
import org.easysoa.proxy.core.api.records.correlation.FieldExtractor;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.proxy.core.api.simulation.SimulationEngine;
import org.easysoa.proxy.core.api.simulation.SimulationMethod;
import org.easysoa.proxy.core.api.simulation.SimulationStore;
import org.easysoa.proxy.core.api.template.TemplateEngine;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.records.ExchangeRecord;
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
    
    // Logger
    private static Logger logger = Logger.getLogger(SimulationEngineImpl.class.getName());    
    
    @Reference
    CorrelationEngine correlationEngine;
    
    //@Reference
    //TemplateEngine templateEngine;
    
    //@Reference
    ProxyFileStore fileStore;

    List<SimulationMethod> simulationMethodList;
    
    /**
     * 
     */
    public SimulationEngineImpl() {
        fileStore = new ProxyFileStore();
    }
    
    @Override
    public SimulationStore getSimulationStoreFromSuggestion(String storeName, List<ExchangeRecord> exchangeRecordList) {
        SimulationStore store = new SimulationStore(storeName);
        FieldExtractor extractor = new FieldExtractor();
        for(ExchangeRecord exchangeRecord : exchangeRecordList){
            logger.debug("Extracting fields for Exchange record " + exchangeRecord.getExchange().getExchangeID());
            TemplateFieldSuggestions suggestions = correlationEngine.correlateWithSubpath(exchangeRecord, 
                    extractor.getInputPathParams(exchangeRecord.getInMessage()), 
                    extractor.getInputQueryParams(exchangeRecord.getInMessage()),
                    extractor.getInputContentParam(exchangeRecord.getInMessage()),
                    extractor.getOutputFields(exchangeRecord.getOutMessage()));
            store.addRecordSuggestions(exchangeRecord, suggestions);
        }
        return store;
    }

    @Override
    public SimulationStore getExistingSimulationStore(String storeName, String recordID) throws Exception {
        return fileStore.loadSimulationStore(storeName);
    }

    @Override
    public ExchangeRecord simulate(ExchangeRecord exchangeRecord, SimulationStore simulationStore, SimulationMethod method, TemplateEngine templateEngine, Map<String, List<String>> fieldValues) throws Exception {
        // Call the correlation engine to get field suggestions for input record
        FieldExtractor extractor = new FieldExtractor();
        TemplateFieldSuggestions fieldSuggestions = correlationEngine.correlateWithSubpath(exchangeRecord,
                extractor.getInputPathParams(exchangeRecord.getInMessage()), 
                extractor.getInputQueryParams(exchangeRecord.getInMessage()),
                extractor.getInputContentParam(exchangeRecord.getInMessage()),
                extractor.getOutputFields(exchangeRecord.getOutMessage()));
        templateEngine.generateTemplate(fieldSuggestions, exchangeRecord, simulationStore.getStoreName(), true);
        return method.simulate(exchangeRecord, fieldSuggestions, simulationStore, templateEngine, fieldValues);
    }
}
