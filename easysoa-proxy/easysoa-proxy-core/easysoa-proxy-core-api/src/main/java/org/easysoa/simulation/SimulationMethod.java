/**
 * 
 */
package org.easysoa.simulation;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateFieldSuggestions;

/**
 * @author jguillemotte
 *
 */
public interface SimulationMethod {

    /**
     * Get the method name
     * @return
     */
    public String getName();
    
    /**
     * Simulate the exchange
     * @param inputSuggestions 
     * @param templateEngine 
     * @throws Exception 
     */
    public ExchangeRecord simulate(ExchangeRecord inputRecord, TemplateFieldSuggestions inputSuggestions, SimulationStore store, TemplateEngine templateEngine) throws Exception;
    
}
