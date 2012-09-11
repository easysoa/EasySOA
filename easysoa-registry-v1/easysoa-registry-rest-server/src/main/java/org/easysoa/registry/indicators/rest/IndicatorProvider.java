package org.easysoa.registry.indicators.rest;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

public interface IndicatorProvider {

    static final String NXQL_SELECT_FROM = "SELECT * FROM ";
    
    static final String NXQL_CRITERIA_NO_PROXY = "ecm:currentLifeCycleState <> 'deleted' " +
                "AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 0";
    
    static final String NXQL_WHERE_NO_PROXY = " WHERE " + NXQL_CRITERIA_NO_PROXY;
    
    List<String> getRequiredIndicators();
    
    Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception;
    
}
