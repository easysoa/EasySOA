package org.easysoa.registry.indicators.rest;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Provider of one or several indicators.
 * Can depend on other indicators that are then passed as parameters for computing. 
 * 
 * @author mkalam-alami
 *
 */
public interface IndicatorProvider {

    static final String NXQL_SELECT_FROM = "SELECT * FROM ";
    
    static final String NXQL_CRITERIA_NO_PROXY = "ecm:currentLifeCycleState <> 'deleted' " +
                "AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 0";
    
    static final String NXQL_WHERE_NO_PROXY = " WHERE " + NXQL_CRITERIA_NO_PROXY;
    
    /**
     * @return The required indicator names
     */
    List<String> getRequiredIndicators();
    
    /**
     * @param session
     * @param computedIndicators A map of the previously computed indicators.
     * Will always contain values of the required indicators.
     * @return The new indicators to register
     * @throws Exception
     */
    Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception;
    
}
