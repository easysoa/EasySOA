package org.easysoa.registry.types;


/**
 * 
 * @author mkalam-alami
 *
 */
public class OperationImplementation {

    private final String name;
    private final String parameters;
    private final String documentation;
    
    public OperationImplementation(String name, String parameters, String documentation) {
        this.name = name;
        this.parameters = parameters;
        this.documentation = documentation;
    }
   
    public String getName() {
        return name;
    }

   
    public String getParameters() {
        return parameters;
    }

   
    public String getDocumentation() {
        return documentation;
    }
    
}
