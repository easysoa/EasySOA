package org.easysoa.registry.indicators.rest;

import java.util.List;

import org.easysoa.registry.types.SoaNode;

public class PlaceholdersIndicatorProvider extends QueryCountIndicator {

    public PlaceholdersIndicatorProvider() {
        super(NXQL_SELECT_FROM + SoaNode.DOCTYPE
                + NXQL_WHERE_NO_PROXY
                + NXQL_AND + NXQL_IS_NOT_DELETED
                + NXQL_AND + SoaNode.XPATH_ISPLACEHOLDER + " = 1",
              NXQL_SELECT_FROM + SoaNode.DOCTYPE
                + NXQL_WHERE_NO_PROXY
                + NXQL_AND + NXQL_IS_NOT_DELETED);
    }

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public String getName() {
        return "Placeholders count";
    }

}
