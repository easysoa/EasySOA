package org.easysoa.template;

import org.easysoa.records.ExchangeRecord;

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

    public abstract void renderTemplate();

}