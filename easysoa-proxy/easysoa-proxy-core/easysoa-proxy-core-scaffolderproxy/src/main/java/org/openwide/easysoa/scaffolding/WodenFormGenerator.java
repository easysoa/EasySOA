package org.openwide.easysoa.scaffolding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.tool.converter.Convert;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaSequence;

@Scope("COMPOSITE")
public class WodenFormGenerator implements FormGeneratorGenericInterface {

	private Description wsdlDescription;
	
	// Just for compatibily with airport tuto hack
	@Property
	String defaultWsdl;		
	
	@Override
	public void setWsdl(String wsdlXmlSource) throws Exception {
		System.out.println("Entering in setWsdl method : " + wsdlXmlSource);
		try{
			// Factory and reader
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			
			// WSDL Validation => disabled to work with more WSDL
			//reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
			
			// Converter
			// TODO add a test to check the version of the WSDL to transform
			// If version is 1.0 or 1.1, need to convert it to 2.0 for Woden
			//if(){
				Convert convert = new Convert();
				String newTargetNS = null;
				String targetDir = "/home/jguillemotte/tests";;
				boolean verbose = true;
				boolean overwrite = true; 
				
				String convertFile = convert.convertFile(newTargetNS,wsdlXmlSource,targetDir,verbose,overwrite);
				System.out.println(convertFile);
	
				wsdlDescription = reader.readWSDL(targetDir + "/" + convertFile);
			/*}
			else {
				wsdlDescription = reader.readWSDL(wsdlXmlSource);
			}*/
				
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		System.out.println("WSDL loaded !");
	}

	@Override
	public String getServiceName() {
		System.out.println("Entering in getServiceName method");
		String serviceName = wsdlDescription.getServices()[0].getName().getLocalPart();
		System.out.println("Service name : " + serviceName);
		return serviceName;
	}

	@Override
	public List<Endpoint> getEndpoints() {
		ArrayList<Endpoint> endpointsList = new ArrayList<Endpoint>();
		for(Endpoint endpoint : wsdlDescription.getServices()[0].getEndpoints()){
			endpointsList.add(endpoint);
		}
		return endpointsList;
	}

	@Override
	public String getBindingName(Endpoint endpoint) {
		return endpoint.getName().toString();
	}

	@Override
	public List<BindingOperation> getOperations(Endpoint endpoint) {
		ArrayList<BindingOperation> bindingOperationList = new ArrayList<BindingOperation>();
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			bindingOperationList.add(operation);
		}
		return bindingOperationList;
	}

	@Override
	public String getOperationName(BindingOperation operation) {
		return operation.getInterfaceOperation().getName().getLocalPart();
	}

	@Override
	public String getOutputMessageName(BindingOperation bindingOperation) {
		System.out.println("Entering in getOutputMessageName");
		InterfaceMessageReference[] messages = bindingOperation.getInterfaceOperation().getInterfaceMessageReferences();
		System.out.println("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(Direction.OUT.equals(messages[i].getDirection())){
				System.out.println("OUT message found");
				return messages[i].getElementDeclaration().getName().getLocalPart();
			}
		}
		return null;
	}

	@Override
	public List<String> getInputFields(BindingOperation bindingOperation) {
		ArrayList<String> inputFields = new ArrayList<String>();
		System.out.println("Entering in getOutputMessageName");
		InterfaceMessageReference[] messages = bindingOperation.getInterfaceOperation().getInterfaceMessageReferences();
		System.out.println("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(Direction.IN.equals(messages[i].getDirection())){
				System.out.println("API to use : " + messages[i].getElementDeclaration().getContentModel());
				System.out.println("Object type : " + messages[i].getElementDeclaration().getContent());
				XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());
			 	System.out.println("Type name : " + schemaElement.getName());
			 	//System.out.println(".... " + schemaElement.getConstraints().getCount());
			 	System.out.println(".... " + schemaElement.getConstraints());
			 	System.out.println(".... " + schemaElement.getSchemaType());
			 	System.out.println(".... " + schemaElement.getSchemaType().getDataType());
			 	System.out.println(".... " + wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());			 	
			 	
			 	XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
			 	XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
			 	XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());
			 	int count = sequence.getItems().getCount();
			 	@SuppressWarnings("unchecked")
				Iterator<Object> iter = sequence.getItems().getIterator();
			 	while(iter.hasNext()){
			 		Object obj = iter.next();
			 		if(obj instanceof XmlSchemaElement){
			 			XmlSchemaElement field = (XmlSchemaElement) obj;
			 			System.out.println("Field found = " + field.getName());
			 			inputFields.add(field.getName());
			 		}
			 	}
			 	System.out.println(" ... : " + elem.getName() + "," + elem.getConstraints().getCount());
			}
		}
		return inputFields;
	}

	@Override
	public List<String> getOutputFields(BindingOperation bindingOperation) {
		ArrayList<String> outputFields = new ArrayList<String>();
		System.out.println("Entering in getOutputMessageName");
		InterfaceMessageReference[] messages = bindingOperation.getInterfaceOperation().getInterfaceMessageReferences();
		System.out.println("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(Direction.OUT.equals(messages[i].getDirection())){
				//return messages[i].getElementDeclaration().getName().getLocalPart();
				System.out.println("API to use : " + messages[i].getElementDeclaration().getContentModel());
				System.out.println("Object type : " + messages[i].getElementDeclaration().getContent());
				XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());				
			 	System.out.println("Type name : " + schemaElement.getName());
			 	System.out.println(".... " + schemaElement.getConstraints().getCount());
			 	System.out.println(".... " + schemaElement.getSchemaType());
			 	System.out.println(".... " + wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
			 	
			 	XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
			 	XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
			 	XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());			 	
			 	int count = sequence.getItems().getCount();
			 	@SuppressWarnings("unchecked")
				Iterator<Object> iter = sequence.getItems().getIterator();
			 	while(iter.hasNext()){
			 		Object obj = iter.next();
			 		if(obj instanceof XmlSchemaElement){
			 			XmlSchemaElement field = (XmlSchemaElement) obj;
			 			System.out.println("Field found = " + field.getName());
			 			outputFields.add(field.getName());
			 		}
			 	}
			 	System.out.println(" ... : " + elem.getName() + "," + elem.getConstraints().getCount());			 	
			}
		}
		return outputFields;
	}

}
