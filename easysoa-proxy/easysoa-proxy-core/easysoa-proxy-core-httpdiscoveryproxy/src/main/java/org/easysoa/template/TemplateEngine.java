package org.easysoa.template;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.easysoa.records.ExchangeRecord;

import com.openwide.easysoa.message.OutMessage;

public interface TemplateEngine {
    
    /**
     * Suggest the fields and generate a fld file
     * @param exchangeRecord The exchange record
     * @return <code>TemplateFieldSuggestions</code>
     */
    public abstract TemplateFieldSuggestions suggestFields(ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception;

    /**
     * Generate the templates for an exchange record (and create a template file)
     * @param exchangeRecord
     * @throws Exception 
     */
    public abstract ExchangeRecord generateTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception;

    /**
     * generate the template for a list of exchange records
     * @param exchangeRecordList
     */
    /*public void generateTemplate( List<ExchangeRecord> exchangeRecordList, boolean generateFile){
        for(ExchangeRecord record : exchangeRecordList){
            generateTemplate(record, generateFile);
        }
    }*/

    /**
     * Call the template renderer and replay the templatized record
     * @param storeName
     * @param recordID
     * @param fieldValues
     * @return the replayed response
     */
    public abstract OutMessage renderTemplateAndReplay(String storeName, ExchangeRecord record, Map<String, List<String>> fieldValues) throws Exception;

}