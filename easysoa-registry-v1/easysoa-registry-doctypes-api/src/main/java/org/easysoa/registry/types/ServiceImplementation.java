package org.easysoa.registry.types;

import java.util.List;


/**
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceImplementation extends SoaNode {

    static final String DOCTYPE = "ServiceImplementation";
    
    static final String XPATH_TECHNOLOGY = "impl:technology";
    
    static final String XPATH_OPERATIONS = "impl:operations";
    
    static final String XPATH_DOCUMENTATION = "impl:documentation";

    static final String XPATH_ISMOCK = "impl:ismock";

    static final String XPATH_TESTS = "impl:tests";
    
    static final String OPERATION_NAME = "operationName";
    
    static final String OPERATION_PARAMETERS = "operationParameters";
    
    static final String OPERATION_DOCUMENTATION = "operationDocumentation";
    
    List<OperationImplementation> getOperations() throws Exception;
    
    void setOperations(List<OperationImplementation> operations) throws Exception;

    List<String> getTests() throws Exception;

    void setTests(List<String> tests) throws Exception;
    
}
