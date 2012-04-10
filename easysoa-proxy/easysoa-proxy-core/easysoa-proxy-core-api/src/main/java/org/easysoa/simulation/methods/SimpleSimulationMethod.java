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

/**
 * 
 */
package org.easysoa.simulation.methods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.simulation.SimulationMethod;
import org.easysoa.simulation.SimulationStore;
import org.easysoa.template.AbstractTemplateField;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateFieldSuggestions;

import com.openwide.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public class SimpleSimulationMethod implements SimulationMethod {

    @Override
    public String getName(){
        return "SimpleSimulationMethod"; 
    }
    
    /**
     * 
     * @param templateField
     * @return
     */
    private boolean match(AbstractTemplateField inputField, AbstractTemplateField recordedField){
        boolean match = false;
        // Match all fields
        if(inputField.getFieldName().equals(recordedField.getFieldName()) && inputField.getDefaultValue().equals(recordedField.getDefaultValue())){
            match = true;
        }
        return match;
    } 
    
    /**
     * Match all fields suggestions
     * @return
     */
    private boolean matchAll(TemplateFieldSuggestions inputSuggestions, TemplateFieldSuggestions recordedSuggestions){
        boolean matchAll = true;
        for(AbstractTemplateField inputField : inputSuggestions.getTemplateFields()){
            // find the corresponding field in recorded suggestions
            for(AbstractTemplateField recordedField : recordedSuggestions.getTemplateFields()){
                // Match field name and field value
                //if(inputField.getFieldName().equals(recordedField.getFieldName()) && inputField.getDefaultValue().equals(recordedField.getDefaultValue())){
                if(match(inputField, recordedField)){
                    matchAll = matchAll & true;
                } else {
                    matchAll = matchAll & false;
                }
            }
        }
        return false;
    }
    
    /**
     * Match some field suggestions
     * @return
     */
    private boolean matchSome(TemplateFieldSuggestions inputSuggestions, TemplateFieldSuggestions recordedSuggestions){
        
        return false;
    }
    
    @Override
    public ExchangeRecord simulate(ExchangeRecord inputRecord, TemplateFieldSuggestions inputSuggestions, SimulationStore store, TemplateEngine templateEngine) throws Exception {
        ExchangeRecord outputRecord = new ExchangeRecord();
        // Sorting store by decreasing correlation level
        //for(AbstractTemplateField templateField : simulationStore.getTemplateList()){
            // TODO : put each simulation algo in specific class implementing a common interface
            //if(SimulationMethod. method){
                
            //}            
        //}
        //simulationStore.orderBy(OrderingMethod.DECREASING_CORRELATION_LEVEL);        
        
        // for each record in the list
        Iterator<ExchangeRecord> recordKeyIterator = store.getRecordList().keySet().iterator();
        while(recordKeyIterator.hasNext()){
            ExchangeRecord record = recordKeyIterator.next();
            TemplateFieldSuggestions recordedSuggestions = store.getRecordList().get(record);
            if(matchAll(inputSuggestions, recordedSuggestions)){
                // get output values from recorded suggestions in the param list for rendering
                
                //recordedSuggestions.getTemplateFields()
                Map<String, List<String>> fieldValues = new HashMap<String, List<String>>();
                // TODO : complete these method to give the template engine the values to use 
                
                OutMessage outMessage = templateEngine.renderTemplateAndReplay(store.getStoreName(), inputRecord, fieldValues);
                inputRecord.setOutMessage(outMessage);
                // If there are several matching records in the store, take the first one
                break;
            } else if(matchSome(inputSuggestions, recordedSuggestions)) {
                
            } else {
                // ordering field suggestions by correlation level
                
            }
        }
        
        
        

        /*for each exchange in simulStore {
            try to match all fields together (AND) (*)
            ..
            (try to match some fields)
            ...
            try to match one of each field independently, ordered by correlLevel (**)
            if match add to matches (with correl)
            }
            choose better match according to correl
            if none return "pb"
            else return templateEngine.render(matchedResponseTemplate, fieldsExtractedFromReq)
        */
        return inputRecord;
    }

}
