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
package org.easysoa.proxy.core.api.template;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.proxy.core.api.template.TemplateBuilder;
import org.easysoa.proxy.core.api.template.TemplateEngine;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggester;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.proxy.core.api.template.TemplateProcessorRendererItf;
import org.easysoa.proxy.core.api.template.VelocityTemplate;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Centralize the call of field suggester, template builder, template renderer in the same class 
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class TemplateEngineImpl implements TemplateEngine {

    // Logger
    private static Logger logger = Logger.getLogger(TemplateEngineImpl.class.getName());    
    
    // SCA Reference to template renderer
    @Reference
    TemplateProcessorRendererItf templateRenderer;
    
    // File store
    ProxyFileStore fileStore;
    
    //private List<CustomParamSetter> paramSetterList = new ArrayList<CustomParamSetter>();
    
    /**
     * 
     */
    public TemplateEngineImpl() {
        this.fileStore = new ProxyFileStore();
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateEngine#suggestFields(org.easysoa.records.ExchangeRecord, java.lang.String, boolean)
     */
    @Override
    public TemplateFieldSuggestions suggestFields(ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception {
        TemplateFieldSuggester suggester = new TemplateFieldSuggester();
        TemplateFieldSuggestions suggestions = suggester.suggest(exchangeRecord);
        // TODO : move this line in proxy fileStore
        if(generateFile){
            fileStore.saveFieldSuggestions(suggestions, storeName, exchangeRecord.getExchange().getExchangeID());            
        }
        return suggestions;
    }
    
    /**
     * Generate suggestion fields (included fld files) for a list of ExchangeRecord. 
     * @param exchangeRecordList The exchange record list
     * @return a Map of <code>TemplateFieldSuggestions</code>, map keys corresponding to the record ID's.
     */
    /*public Map<String, TemplateFieldSuggestions> suggestFields(List<ExchangeRecord> exchangeRecordList, boolean generateFile){
        HashMap<String, TemplateFieldSuggestions> results = new HashMap<String, TemplateFieldSuggestions>();
        for(ExchangeRecord record : exchangeRecordList){
            results.put(record.getExchange().getExchangeID(), suggestFields(record, generateFile));
        }
        return results;
    }*/

    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateEngine#generateTemplate(org.easysoa.template.TemplateFieldSuggestions, org.easysoa.records.ExchangeRecord, java.lang.String, boolean)
     */
    @Override
    public ExchangeRecord generateTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception{
        // call the template builder
        TemplateBuilder builder = new TemplateBuilder();
        ExchangeRecord templatizedRecord = builder.templatizeRecord(fieldSuggestions, exchangeRecord);
        VelocityTemplate template = builder.buildTemplate(templatizedRecord);
        // TODO : move this block in proxyFileStore
        if(generateFile){
            // Store the templatized record
            fileStore.saveCustomRecord(templatizedRecord, storeName);
            // store the template
            fileStore.saveTemplate(template, storeName);
        }
        return templatizedRecord;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateEngine#renderTemplate()
     */    
    @Override
    public OutMessage renderTemplateAndReplay(String storeName, ExchangeRecord record, Map<String, List<String>> fieldValues, boolean simulation) throws Exception {
        // call the template renderer
        //String templatePath = ProxyExchangeRecordFileStore.REQ_TEMPLATE_FILE_PREFIX + record.getExchange().getExchangeID() + ProxyExchangeRecordFileStore.TEMPLATE_FILE_EXTENSION;
        // TODO : Move the constants
        // TODO : call the renderReq Method in case of replay engine, renderRes otherwise
        String templatePath;
        if(simulation){
            templatePath = "resTemplateRecord_" + record.getExchange().getExchangeID() + ".vm";
            return templateRenderer.renderRes(templatePath, record, storeName, fieldValues);
        } else {
            templatePath = "reqTemplateRecord_" + record.getExchange().getExchangeID() + ".vm";
            return templateRenderer.renderReq(templatePath, record, storeName, fieldValues);            
        }
    }
   
    /**
     * Complete template renderering : field suggestions, template generation and template rendering
     */
    /*public void generateAndRenderTemplate(String runName, boolean generateFile){
        // Field suggestion
        suggestFields(exchangeRecord, generateFile);
        // Template building
        generateTemplate(exchangeRecord, generateFile);
        // Template rendering
        renderTemplate();
    }*/
    
    /**
     * Method used in first solution .... Now have to use Custom ProxyImplementationVelocity or ServletImplementationVelocity
     * @param template
     * @param message
     * @param mapParams
     */
    /*private void setCustomParams(TemplateFieldSuggestions templateFieldSuggestions, ExchangeRecord record, Map<String, List<String>> fieldValues){
        // Write the code to set custom parameters
        // For each templateField described in the template and For each Custom Param setter in the paramSetter list,
        // call the method isOKFor and if ok call the method setParams
        for(TemplateField templateField : templateFieldSuggestions.getTemplateFields()){
            for(CustomParamSetter paramSetter : paramSetterList){
                if(paramSetter.isOkFor(templateField)){
                    paramSetter.setParam(templateField, record.getInMessage(), mapParams);
                }
            }
        }
    }*/    
    
}
