package org.easysoa.registry.types;


public interface Deliverable extends SoaNode {

    public static final String DOCTYPE = "Deliverable";

    public static final String XPATH_NATURE = "del:nature";

    public static final String XPATH_APPLICATION = "del:application";

    public static final String XPATH_SOAVERSION = "soav:version";
    
    String getNature() throws Exception;

    void setNature(String nature) throws Exception;

    String getApplication() throws Exception;

    void setApplication(String application) throws Exception;

    String getVersion() throws Exception;

    void setVersion(String version) throws Exception;
}