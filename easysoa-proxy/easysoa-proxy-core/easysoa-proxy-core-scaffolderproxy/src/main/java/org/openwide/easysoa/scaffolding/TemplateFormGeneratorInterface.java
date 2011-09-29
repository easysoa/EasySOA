package org.openwide.easysoa.scaffolding;

import java.util.List;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;

public interface TemplateFormGeneratorInterface {

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
	public List<WSEndpoint> getEndpoints();
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public String getBindingName(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public List<WSOperation> getOperations(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param operation
	 * @return
	 */
	public String getOperationName(WSOperation wsOperation);

	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation);	
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<String> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation);
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<String> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation);	
	
}
