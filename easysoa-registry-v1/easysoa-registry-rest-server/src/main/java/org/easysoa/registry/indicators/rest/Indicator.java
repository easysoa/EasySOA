package org.easysoa.registry.indicators.rest;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Simple indicator provider, that only computes 1 indicator.
 * 
 * @author mkalam-alami
 *
 */
public abstract class Indicator implements IndicatorProvider {

    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put(getName(), compute(session, computedIndicators));
        return indicators;
    }
    
    public abstract String getName();
    
    public abstract IndicatorValue compute(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception;
    
}
