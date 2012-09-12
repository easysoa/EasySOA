package org.easysoa.registry.indicators.rest;

import java.util.List;


public class DoctypeCountIndicator extends QueryCountIndicator {

    private final String doctype;

    public DoctypeCountIndicator(String doctype) {
        super(NXQL_SELECT_FROM + doctype + NXQL_WHERE_NO_PROXY);
        this.doctype = doctype;
    }

    @Override
    public String getName() {
        return getName(doctype);
    }

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }
    
    public static String getName(String doctype) {
        return doctype + " count";
    }
    
}
