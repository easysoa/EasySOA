/**
 * 
 */
package org.easysoa.template;

import java.util.Map;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;

/**
 * Centralize the call of field suggester, template builder, template renderer in the same class 
 * 
 * @author jguillemotte
 *
 */
public class TemplateEngine {
    
    // File store
    private ProxyExchangeRecordFileStore fileStore;
    
    /**
     * 
     */
    public TemplateEngine(){
        this.fileStore = new ProxyExchangeRecordFileStore();
    }
    
    /**
     * Suggest the fields and generate a fld file
     * @param exchangeRecord The exchange record
     * @return <code>TemplateFieldSuggestions</code>
     */
    public TemplateFieldSuggestions suggestFields(ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception {
        TemplateFieldSuggester suggester = new TemplateFieldSuggester();
        TemplateFieldSuggestions suggestions = suggester.suggest(exchangeRecord);
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

    /**
     * Generate the templates for an exchange record (and create a template file)
     * @param exchangeRecord
     * @throws Exception 
     */
    public ExchangeRecord generateTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception{
        // call the template builder
        TemplateBuilder builder = new TemplateBuilder();
        ExchangeRecord templatizedRecord = builder.templatizeRecord(fieldSuggestions, exchangeRecord);
        VelocityTemplate template = builder.buildTemplate(templatizedRecord);
        if(generateFile){
            // Store the templatized record
            fileStore.saveCustomRecord(templatizedRecord, storeName);
            // store the template
            fileStore.saveTemplate(template, storeName);
        }
        return templatizedRecord;
    }
    
    /**
     * generate the template for a list of exchange records
     * @param exchangeRecordList
     */
    /*public void generateTemplate( List<ExchangeRecord> exchangeRecordList, boolean generateFile){
        for(ExchangeRecord record : exchangeRecordList){
            generateTemplate(record, generateFile);
        }
    }*/
    
    public void renderTemplate(){
        // call the template renderer
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
    
}
