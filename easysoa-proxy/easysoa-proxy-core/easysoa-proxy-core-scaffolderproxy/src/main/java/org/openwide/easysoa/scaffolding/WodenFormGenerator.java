package org.openwide.easysoa.scaffolding;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
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
import org.w3c.dom.Document;

@Scope("COMPOSITE")
public class WodenFormGenerator implements TemplateFormGeneratorInterface {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WodenFormGenerator.class.getClass());	
	
	// Description of the WSDL file to parse
	private Description wsdlDescription;
	
	// Just for compatibily with airport tuto hack
	@Property
	String defaultWsdl;		
	
	@Override
	public void setWsdl(String wsdlSource) throws Exception {
		// Hack for Talend airport sample
		if(wsdlSource == null || "".equals(wsdlSource)){
			wsdlSource = defaultWsdl;
		}		
		logger.debug("Entering in setWsdl method : " + wsdlSource);
		try{
			// Factory and reader
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			
			// WSDL Validation => disabled to work with more WSDL
			//reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
			
			// Converter
			// Check the WSDl version : if version 1.0 or 1.1 => transformation to WSDL 2.0 is required
			if(!isWsdl2(wsdlSource)){
				Convert convert = new Convert();
				String newTargetNS = null;
				String targetDir = System.getProperty("user.dir");
				boolean verbose = true;
				boolean overwrite = true; 
				String convertFile = convert.convertFile(newTargetNS,wsdlSource,targetDir,verbose,overwrite);
				logger.debug(convertFile);
				logger.debug(targetDir + "/" + convertFile);
				String wsdl2read = targetDir + "/" + convertFile;
				wsdlDescription = reader.readWSDL(wsdl2read);
				System.out.println("Bug : " + wsdlDescription);
			}
			else {
				wsdlDescription = reader.readWSDL(wsdlSource);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		logger.debug("WSDL loaded !");
	}

	@Override
	public String getServiceName() {
		logger.debug("Entering in getServiceName method");
		String serviceName = wsdlDescription.getServices()[0].getName().getLocalPart();
		logger.debug("Service name : " + serviceName);
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
		logger.debug("Entering in getOperations");
		ArrayList<WSOperation> wsOperationList = new ArrayList<WSOperation>();
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			wsOperationList.add(new WSOperation(operation.getInterfaceOperation().getName()));
		}
		return wsOperationList;
	}

	@Override
	public String getOperationName(WSOperation wsOperation) {
		logger.debug("Entering in getOperationName");
		/*return operation.getInterfaceOperation().getName().getLocalPart();*/
		return wsOperation.getName();
	}

	@Override
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputMessageName");
		BindingOperation operation = getBindingOperation(wsEndpoint, wsOperation);
		InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();
		for(int i = 0; i <messages.length; i++){
			if(Direction.OUT.equals(messages[i].getDirection())){
				logger.debug("OUT message found");
				return messages[i].getElementDeclaration().getName().getLocalPart();
			}
		}
		return "";
	}

	@Override
	public List<String> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputMessageName");
		BindingOperation operation = getBindingOperation(wsEndpoint, wsOperation);
		return getFields(operation, Direction.IN);
	}

	@Override
	public List<String> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputMessageName");
		BindingOperation operation = getBindingOperation(wsEndpoint, wsOperation);				
		return getFields(operation, Direction.OUT);
	}
	
	/**
	 * Return the binding corresponding to the endpoint and the operation 
	 * @param wsEndpoint
	 * @param wsOperation
	 * @return The binding, null if the binding is not found
	 */
	private BindingOperation getBindingOperation(WSEndpoint wsEndpoint, WSOperation wsOperation){
		Endpoint endpoint = wsdlDescription.getServices()[0].getEndpoint(new NCName(wsEndpoint.getName()));
		for(BindingOperation operation : endpoint.getBinding().getBindingOperations()){
			if(operation.getInterfaceOperation().getName().equals(wsOperation.getQName())){
				return operation;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of fields corresponding to the operation and the message direction
	 * @param operation The operation for which to get the I/O fields
	 * @param direction The message direction
	 * @return A string list of fields 
	 */
	private List<String> getFields(BindingOperation operation, Direction direction){
		ArrayList<String> outputFields = new ArrayList<String>();
		InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();		
		logger.debug("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(direction.equals(messages[i].getDirection())){
				XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());				
				XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
				XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
				XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());			 	
				Iterator<XmlSchemaSequenceMember> iter = sequence.getItems().iterator();
				//Iterator<Object> iter = sequence.getItems().getIterator();
				while(iter.hasNext()){
					Object obj = iter.next();
					if(obj instanceof XmlSchemaElement){
						XmlSchemaElement field = (XmlSchemaElement) obj;
						logger.debug("Field found = " + field.getName());
						outputFields.add(field.getName());
					}
				}
			}
		}
		return outputFields;		
	}
	
	/**
	 * Return true is the WSDl version is 2.0, false othervise
	 * @param xmlSource The WSDL File to check
	 * @return True if the document version is 2.0, false otherwise 
	 * @throws Exception If a problem occurs
	 */
	private boolean isWsdl2(String xmlSource) throws Exception {
		File file = new File(new URI(xmlSource));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		if("definitions".equalsIgnoreCase(doc.getDocumentElement().getNodeName().substring(doc.getDocumentElement().getNodeName().indexOf(":")+1))){
			return false;
		}
		else {
			return true;
		}
	}

}
