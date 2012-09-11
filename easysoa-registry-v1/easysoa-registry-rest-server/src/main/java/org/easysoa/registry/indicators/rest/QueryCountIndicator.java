package org.easysoa.registry.indicators.rest;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.query.sql.NXQL;

public abstract class QueryCountIndicator extends Indicator {

    private String valueQuery = null;
    private String totalQuery = null;
    
    public QueryCountIndicator(String valueQuery) {
        this.valueQuery = valueQuery;
    }

    public QueryCountIndicator(String valueQuery, String totalQuery) {
        this.valueQuery = valueQuery;
        this.totalQuery = totalQuery;
    }

    @Override
    public IndicatorValue compute(CoreSession session, Map<String, IndicatorValue> computedIndicators) throws ClientException {
        IterableQueryResult queryResult = session.queryAndFetch(valueQuery, NXQL.NXQL);
        try {
            if (this.totalQuery == null) {
                return new IndicatorValue((int) queryResult.size(), -1);
            }
            else {
                IterableQueryResult totalQueryResult = session.queryAndFetch(totalQuery, NXQL.NXQL);
                return new IndicatorValue((int) queryResult.size(), (int) totalQueryResult.size());
            }
        }
        finally {
            queryResult.close();
        }
    }
    
}
