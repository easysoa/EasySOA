package org.openwide.easysoa.scaffolding;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * Alternative solution to XSLT transformation for form generation
 * To use with velocity and template model => need to change the interface signature 
 * @author jguillemotte
 *
 */
// TODO Composite is like singleton, not sure that it it the best scope to use here ...
// TODO Check compatibility with WSDL 2.0 
// TODO return all the bindings contained in the WSDL, not only the first
// TODO return the field type for the HTML form, for now all the fields are tagged as String (Hardcoded in the velocity template) 
@Scope("COMPOSITE")
public class EasyWsdlFormGenerator implements TemplateFormGeneratorInterface  {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(EasyWsdlFormGenerator.class.getClass());	

	// Description of the WSDL file to parse
	private Description wsdlDescription;
	
	//TODO : Only for the Airport Talend Tuto Hack
	@Property
	String defaultWsdl;	
	
	@Override
	public void setWsdl(String wsdlSource) throws Exception {
		// Hack for Talend airport sample
		if(wsdlSource == null || "".equals(wsdlSource)){
			wsdlSource = defaultWsdl;
		}
		// Read WSDL version 1.1 or 2.0
		WSDLReader reader;
		logger.debug("WSDl source to parse : " + wsdlSource);
		try {
			reader = WSDLFactory.newInstance().newWSDLReader();
			wsdlDescription = reader.read(new URL(wsdlSource));
			logger.debug("WSDL Reading OK");
		} catch (WSDLException wex) {
			logger.error("A problem occurs during the parsing of the WSDl file !", wex);
			throw wex;
		}
	}
	
	@Override
	public String getServiceName(){
		logger.debug("Entering in getServiceName method");
		logger.debug("Number of services : " + wsdlDescription.getServices().size());
		String serviceName = wsdlDescription.getServices().get(0).getQName().getLocalPart();
		logger.debug("Service name : " + serviceName);
		return serviceName;
	}
	
	@Override
	public List<WSEndpoint> getEndpoints() {
		logger.debug("Entering in getEndpoints method");
		ArrayList<WSEndpoint> endpointList = new ArrayList<WSEndpoint>();
		for(Endpoint endpoint : wsdlDescription.getServices().get(0).getEndpoints()){
			try {
				endpointList.add(new WSEndpoint(endpoint.getName(), new URI(endpoint.getAddress())));
			} catch (URISyntaxException ex) {
				logger.error(ex);
				ex.printStackTrace();
			} 
		}
		return endpointList;
	}
	
	@Override
	public String getBindingName(WSEndpoint wsEndpoint){
		logger.debug("Entering in getBindingName method");				
		return wsdlDescription.getServices().get(0).getEndpoint(wsEndpoint.getName()).getBinding().getQName().getLocalPart();
	}
	
	@Override
	public List<WSOperation> getOperations(WSEndpoint wsEndpoint){
		logger.debug("Entering in getOperations method");
		ArrayList<WSOperation> wsOperationList = new ArrayList<WSOperation>();
		for(BindingOperation bindingOperation : wsdlDescription.getServices().get(0).getEndpoint(wsEndpoint.getName()).getBinding().getBindingOperations()){
			wsOperationList.add(new WSOperation(bindingOperation.getQName()));
		}
		return wsOperationList;
	}
	
	@Override
	public String getOperationName(WSOperation wsOperation) {
		logger.debug("Entering in getOperatioName method");
		return wsOperation.getName();
	}

	@Override
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputMessageName method");
		for(BindingOperation bindingOperation : wsdlDescription.getServices().get(0).getEndpoint(wsEndpoint.getName()).getBinding().getBindingOperations()){
			if(bindingOperation.getQName().equals(wsOperation.getQName())){
				return bindingOperation.getOperation().getOutput().getName();
			}
		}
		return "";
	}	
	
	@Override
	public List<String> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getInputFields method");
		List<Part> partList = new ArrayList<Part>();
		for(BindingOperation bindingOperation : wsdlDescription.getServices().get(0).getEndpoint(wsEndpoint.getName()).getBinding().getBindingOperations()){
			if(bindingOperation.getQName().equals(wsOperation.getQName())){
				partList = bindingOperation.getOperation().getInput().getParts();
			}
		}
		return getFields(partList);
	}
	
	@Override
	public List<String> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputFields method");	
		List<Part> partList = new ArrayList<Part>();
		for(BindingOperation bindingOperation : wsdlDescription.getServices().get(0).getEndpoint(wsEndpoint.getName()).getBinding().getBindingOperations()){
			if(bindingOperation.getQName().equals(wsOperation.getQName())){
				partList = bindingOperation.getOperation().getOutput().getParts();
			}
		}
		return getFields(partList);
	}
	
	/**
	 * Return the field list for the given message or partType
	 * @param partList The Part list to parse to retrieve the fields
	 * @return A list of fields
	 */
	private List<String> getFields(List<Part> partList) {
		List<String> elementNameList = new ArrayList<String>();
		if(partList != null){
			for(Part part : partList){
				if(part.getElement() != null){
					logger.debug("message found : " + part.getElement().getQName().getLocalPart());
					Type elementType = wsdlDescription.getTypes().getSchemas().get(0).getType(part.getElement().getQName());
					// In case of complexType tag inside element tag
					if(elementType == null){
						elementType = wsdlDescription.getTypes().getSchemas().get(0).getElement(part.getElement().getQName()).getType();
					}
					// Casting and adding the element in the list
					if(elementType != null){
						if(elementType instanceof ComplexType){
							//logger.debug("ComplexType found ...");
							ComplexType typ = (ComplexType) elementType;
							// Complex element can be empty
							if(typ.getSequence() != null){
								List<Element> elementList = typ.getSequence().getElements();
								for(Element element : elementList){
									logger.debug("element found : " + element.getQName().getLocalPart());
									elementNameList.add(element.getQName().getLocalPart());
								}
							}
						} else if(elementType instanceof SimpleType) {
							//logger.debug("SimpleType found ...");
							SimpleType typ = (SimpleType) elementType;
							logger.debug("element found : " + typ.getQName().getLocalPart());
							elementNameList.add(typ.getQName().getLocalPart());
						}
					}
				}
			}
		}
		return elementNameList;
	}

}
