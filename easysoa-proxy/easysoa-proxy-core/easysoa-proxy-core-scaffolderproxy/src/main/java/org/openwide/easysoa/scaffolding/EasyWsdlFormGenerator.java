package org.openwide.easysoa.scaffolding;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.XmlException;
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
// TODO return the field type for the HTML form, for now all the fiels are tagged as String (Hardcoded in the velocity template) 
@Scope("COMPOSITE")
public class EasyWsdlFormGenerator implements FormGeneratorItf {

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
		// Read a WSDL 1.1 or 2.0
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
	public List<Endpoint> getEndpoints(){
		logger.debug("Entering in getEndpoints method");
		List<Endpoint> endpointList = wsdlDescription.getServices().get(0).getEndpoints();
		return endpointList;
	}
	
	@Override
	public String getBindingName(Endpoint endpoint){
		logger.debug("Entering in getBindingName method");				
		return endpoint.getBinding().getQName().getLocalPart();
	}
	
	@Override
	public List<BindingOperation> getOperations(Endpoint endpoint){
		logger.debug("Entering in getOperations method");
		return endpoint.getBinding().getBindingOperations();
	}
	
	@Override
	public String getOperationName(BindingOperation bindingOperation) {
		logger.debug("Entering in getOperatioName method");
		return bindingOperation.getQName().getLocalPart();
	}
	
	@Override
	public List<String> getInputFields(BindingOperation bindingOperation) {
		logger.debug("Entering in getInputFields method");
		logger.debug("BindingOperation : " + bindingOperation);
		logger.debug("Input : " + bindingOperation.getOperation().getInput().getName());
		List<Part> partList = bindingOperation.getOperation().getInput().getParts();
		return getFields(partList);
	}
	
	@Override
	public List<String> getOutputFields(BindingOperation bindingOperation) {
		logger.debug("Entering in getOutputFields method");	
		List<Part> partList = bindingOperation.getOperation().getOutput().getParts();
		return getFields(partList);
	}

	@Override
	public String getOutputMessageName(BindingOperation bindingOperation) {
		logger.debug("Entering in getOutputMessageName method");
		return bindingOperation.getOperation().getOutput().getName();
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
		
									// To find attribute list, only for dev !!
									/*
									try {
										Map<QName, String> attributes = element.getOtherAttributes();
										for(QName key : attributes.keySet()){
											System.out.println("attribute : (" + key + "," + attributes.get(key) + ")");								
										}
									} catch (XmlException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
									
									elementNameList.add(element.getQName().getLocalPart());
								}
							}
						} else if(elementType instanceof SimpleType) {
							//logger.debug("SimpleType found ...");
							SimpleType typ = (SimpleType) elementType;
		
							// To find attribute list, only for dev !!
							/*try {
								Map<QName, String> attributes = typ.getOtherAttributes();
								for(QName key : attributes.keySet()){
									System.out.println("attribute : (" + key + "," + attributes.get(key) + ")");								
								}
							} catch (XmlException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
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
