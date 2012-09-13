package org.easysoa.registry.rest.client.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.ServiceImplementation;

public class ServiceImplementationInformation extends SoaNodeInformation implements ServiceImplementation {
    
    public ServiceImplementationInformation(String name) {
        super(new SoaNodeId(ServiceImplementation.DOCTYPE, name), null, null);
    }
    
    public List<OperationImplementation> getOperations() {
        // Proper-ish conversion from List<Map<String, Serializable>> hidden behind Serializable, to List<OperationImplementation>
        List<?> operationsUnknown = (List<?>) properties.get(XPATH_OPERATIONS);
        List<OperationImplementation> operations = new ArrayList<OperationImplementation>();
        if (operationsUnknown != null) {
	        for (Object operationUnknown : operationsUnknown) {
	            Map<?, ?> operationMap = (Map<?, ?>) operationUnknown;
	            operations.add(new OperationImplementation(
	                    (String) operationMap.get(OPERATION_NAME),
	                    (String) operationMap.get(OPERATION_PARAMETERS),
	                    (String) operationMap.get(OPERATION_DOCUMENTATION)));
	        }
        }
        return operations;
    }
    
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

	public List<String> getTests() throws Exception {
		Serializable[] testsArray = (Serializable[]) properties.get(XPATH_TESTS);
		List<String> tests = new ArrayList<String>();
		for (Serializable test : testsArray) {
			tests.add(test.toString());
		}
		return tests;
	}

	public void setTests(List<String> tests) throws Exception {
		properties.put(XPATH_TESTS, (Serializable) tests);
	}

	@JsonIgnore
	public boolean isMock() {
		return properties.containsKey(XPATH_ISMOCK)
				&& Boolean.parseBoolean((String) properties.get(XPATH_ISMOCK));
	}
	
}
