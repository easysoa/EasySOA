package org.openwide.easysoa.scaffolding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.tool.converter.Convert;
import org.apache.woden.types.NCName;
import org.apache.woden.wsdl20.BindingOperation;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class WodenFormGenerator implements TemplateFormGeneratorInterface {

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
	public List<WSEndpoint> getEndpoints() {
		ArrayList<WSEndpoint> endpointsList = new ArrayList<WSEndpoint>();
		for(Endpoint endpoint : wsdlDescription.getServices()[0].getEndpoints()){
			endpointsList.add(new WSEndpoint(endpoint.getName().toString() , endpoint.getAddress()));
		}
		return endpointsList;
	}

	@Override
	public String getBindingName(WSEndpoint wsEndpoint) {
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		return endpoint.getName().toString();
	}

	@Override
	public List<WSOperation> getOperations(WSEndpoint wsEndpoint) {
		System.out.println("Entering in getOperations");
		ArrayList<WSOperation> wsOperationList = new ArrayList<WSOperation>();
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			//bindingOperationList.add(operation);
			wsOperationList.add(new WSOperation(operation.getInterfaceOperation().getName()));
		}
		return wsOperationList;
	}

	@Override
	public String getOperationName(WSOperation wsOperation) {
		System.out.println("Entering in getOperationName");
		/*return operation.getInterfaceOperation().getName().getLocalPart();*/
		return wsOperation.getName();
	}

	@Override
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		System.out.println("Entering in getOutputMessageName");
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			if(operation.getInterfaceOperation().getName().equals(wsOperation.getQName())){
				InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();
				for(int i = 0; i <messages.length; i++){
					if(Direction.OUT.equals(messages[i].getDirection())){
						System.out.println("OUT message found");
						return messages[i].getElementDeclaration().getName().getLocalPart();
					}
				}
			}
		}
		return "";
		/*
		InterfaceMessageReference[] messages = bindingOperation.getInterfaceOperation().getInterfaceMessageReferences();
		System.out.println("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(Direction.OUT.equals(messages[i].getDirection())){
				System.out.println("OUT message found");
				return messages[i].getElementDeclaration().getName().getLocalPart();
			}
		}*/
	}

	@Override
	public List<String> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		ArrayList<String> inputFields = new ArrayList<String>();
		System.out.println("Entering in getOutputMessageName");
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			if(operation.getInterfaceOperation().getName().equals(wsOperation.getQName())){
				InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();
				for(int i = 0; i <messages.length; i++){
					if(Direction.IN.equals(messages[i].getDirection())){
						System.out.println("API to use : " + messages[i].getElementDeclaration().getContentModel());
						System.out.println("Object type : " + messages[i].getElementDeclaration().getContent());
						XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());
						System.out.println("Type name : " + schemaElement.getName());
						//System.out.println(".... " + schemaElement.getConstraints());
						//System.out.println(".... " + schemaElement.getSchemaType());
						//System.out.println(".... " + schemaElement.getSchemaType().getDataType());
						//System.out.println(".... " + wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());			 	
						XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
						XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
						XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());
						Iterator<XmlSchemaSequenceMember> iter = sequence.getItems().iterator();
						while(iter.hasNext()){
							Object obj = iter.next();
							if(obj instanceof XmlSchemaElement){
								XmlSchemaElement field = (XmlSchemaElement) obj;
								System.out.println("Field found = " + field.getName());
								inputFields.add(field.getName());
							}
						}
					}
				}
			}
		}
		return inputFields;
	}

	@Override
	public List<String> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		ArrayList<String> outputFields = new ArrayList<String>();
		System.out.println("Entering in getOutputMessageName");
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			if(operation.getInterfaceOperation().getName().equals(wsOperation.getQName())){
				InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();
				System.out.println("messages : " + messages.length);
				for(int i = 0; i <messages.length; i++){
					if(Direction.OUT.equals(messages[i].getDirection())){
						//return messages[i].getElementDeclaration().getName().getLocalPart();
						System.out.println("API to use : " + messages[i].getElementDeclaration().getContentModel());
						System.out.println("Object type : " + messages[i].getElementDeclaration().getContent());
						XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());				
					 	//System.out.println("Type name : " + schemaElement.getName());
					 	//System.out.println(".... " + schemaElement.getConstraints().getCount());
					 	//System.out.println(".... " + schemaElement.getSchemaType());
					 	//System.out.println(".... " + wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
						XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
						XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
						XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());
						Iterator<XmlSchemaSequenceMember> iter = sequence.getItems().iterator();
						while(iter.hasNext()){
							Object obj = iter.next();
							if(obj instanceof XmlSchemaElement){
								XmlSchemaElement field = (XmlSchemaElement) obj;
								System.out.println("Field found = " + field.getName());
								outputFields.add(field.getName());
							}
						}
					}
				}
			}
		}
		return outputFields;
	}

}
