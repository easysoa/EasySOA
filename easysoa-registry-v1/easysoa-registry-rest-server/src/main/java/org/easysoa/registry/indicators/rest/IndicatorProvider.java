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
    static final String NXQL_WHERE = " WHERE ";
    static final String NXQL_AND = " AND ";
    static final String NXQL_QUOTE = "'";

    static final String NXQL_IS_NOT_DELETED = "ecm:currentLifeCycleState <> 'deleted'";

    static final String NXQL_IS_NOT_VERSIONED = "ecm:isCheckedInVersion = 0";
    
    static final String NXQL_IS_PROXY = "ecm:isProxy = 1";
    static final String NXQL_IS_NO_PROXY = "ecm:isProxy = 0";
    
    static final String NXQL_WHERE_NO_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED
            + NXQL_AND + NXQL_IS_NOT_VERSIONED + NXQL_AND + NXQL_IS_NO_PROXY;
    static final String NXQL_WHERE_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED
            + NXQL_AND + NXQL_IS_NOT_VERSIONED + NXQL_AND + NXQL_IS_PROXY;

    static final String NXQL_PATH_STARTSWITH = "ecm:path STARTSWITH '";
    
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
