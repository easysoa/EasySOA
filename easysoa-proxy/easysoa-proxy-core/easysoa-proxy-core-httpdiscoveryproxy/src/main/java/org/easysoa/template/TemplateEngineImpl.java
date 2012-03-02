/**
 * 
 */
package org.easysoa.template;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import com.openwide.easysoa.message.OutMessage;

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
    
    /**
     * 
     */
    public TemplateEngineImpl(){
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
    public OutMessage renderTemplateAndReplay(String storeName, ExchangeRecord record, Map<String, List<String>> fieldValues) throws Exception {
        // call the template renderer
        //String templatePath = ProxyExchangeRecordFileStore.REQ_TEMPLATE_FILE_PREFIX + record.getExchange().getExchangeID() + ProxyExchangeRecordFileStore.TEMPLATE_FILE_EXTENSION;
        // TODO : Move the constants
        String templatePath = "reqTemplateRecord_" + record.getExchange().getExchangeID() + ".vm";
        return templateRenderer.renderReq(templatePath, record, storeName, fieldValues);        
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
