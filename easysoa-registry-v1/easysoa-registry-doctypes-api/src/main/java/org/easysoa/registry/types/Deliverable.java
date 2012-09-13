package org.easysoa.registry.types;

import java.util.List;


public interface Deliverable extends SoaNode {

    public static final String DOCTYPE = "Deliverable";

    public static final String XPATH_SOAVERSION = "soav:version";

    public static final String XPATH_NATURE = "del:nature";

    public static final String XPATH_APPLICATION = "del:application";

    public static final String XPATH_LOCATION = "del:location";
    
    public static final String XPATH_DEPENDENCIES = "del:dependencies";
    
    String getNature() throws Exception;

    void setNature(String nature) throws Exception;

    String getApplication() throws Exception;

    void setApplication(String application) throws Exception;

    String getVersion() throws Exception;

    void setVersion(String version) throws Exception;
    
    List<String> getDependencies() throws Exception;
    
    void setDependencies(List<String> dependencies) throws Exception;
    
}