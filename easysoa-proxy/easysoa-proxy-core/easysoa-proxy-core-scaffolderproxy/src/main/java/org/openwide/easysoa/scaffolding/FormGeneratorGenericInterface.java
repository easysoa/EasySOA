package org.openwide.easysoa.scaffolding;

import java.util.List;

import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Endpoint;

public interface FormGeneratorGenericInterface {

	/**
	 * Set the WSDl to parse : First thing to do before to call the other methods
	 * @param wsdlXmlSource
	 */
	public void setWsdl(String wsdlXmlSource) throws Exception;
	
	/**
	 * Returns the service name
	 * @return
	 */
	public String getServiceName();
	
	/**
	 * Returns the list of endpoints (or ports)
	 * @return
	 */
	public List<Endpoint> getEndpoints();
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public String getBindingName(Endpoint endpoint);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public List<BindingOperation> getOperations(Endpoint endpoint);
	
	/**
	 * 
	 * @param operation
	 * @return
	 */
	public String getOperationName(BindingOperation operation);

	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public String getOutputMessageName(BindingOperation bindingOperation);	
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<String> getInputFields(BindingOperation bindingOperation);
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<String> getOutputFields(BindingOperation bindingOperation);	
	
}
