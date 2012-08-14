package org.easysoa.registry.types;

import org.nuxeo.ecm.core.api.ClientException;

public interface Deliverable extends Document {

    public static final String DOCTYPE = "Deliverable";

    public static final String XPATH_NATURE = "del:nature";

    public static final String XPATH_APPLICATION = "del:application";

    String getDoctype();

    String getNature() throws ClientException;

    void setNature(String nature) throws ClientException;

    String getApplication() throws ClientException;

    void setApplication(String application) throws ClientException;

}