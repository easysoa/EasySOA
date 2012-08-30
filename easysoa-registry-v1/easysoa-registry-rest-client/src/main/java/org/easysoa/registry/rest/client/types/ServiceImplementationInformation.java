package org.easysoa.registry.rest.client.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.ServiceImplementation;

public class ServiceImplementationInformation extends SoaNodeInformation implements ServiceImplementation {

    public ServiceImplementationInformation(String name) {
        super(new SoaNodeId(ServiceImplementation.DOCTYPE, name), null, null);
    }

    @Override
    public List<OperationImplementation> getOperations() {
        // Proper-ish conversion from List<Map<String, Serializable>> hidden behind Serializable, to List<OperationImplementation>
        List<?> operationUnknowns = (List<?>) properties.get(XPATH_OPERATIONS);
        List<OperationImplementation> operations = new ArrayList<OperationImplementation>();
        for (Object operationUnknown : operationUnknowns) {
            Map<?, ?> operationMap = (Map<?, ?>) operationUnknown;
            operations.add(new OperationImplementation(
                    (String) operationMap.get(OPERATION_NAME),
                    (String) operationMap.get(OPERATION_PARAMETERS),
                    (String) operationMap.get(OPERATION_DOCUMENTATION)));
        }
        return operations;
    }
    
    @Override
    public void setOperations(List<OperationImplementation> operations) {
        // Conversion from List<OperationImplementation> to List<Map<String, Serializable>>
        List<Map<String, Serializable>> operationsSerializable = new ArrayList<Map<String, Serializable>>();
        for (OperationImplementation operation : operations) {
            Map<String, Serializable> operationSerializable = new HashMap<String, Serializable>();
            operationSerializable.put(OPERATION_NAME, operation.getName());
            operationSerializable.put(OPERATION_DOCUMENTATION, operation.getDocumentation());
            operationSerializable.put(OPERATION_PARAMETERS, operation.getParameters());
            operationsSerializable.add(operationSerializable);
        }
        properties.put(XPATH_OPERATIONS, (Serializable) operationsSerializable);
    }
    
}
