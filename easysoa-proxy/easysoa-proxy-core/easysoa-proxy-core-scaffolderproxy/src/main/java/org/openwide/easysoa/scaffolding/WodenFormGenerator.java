/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
import org.apache.woden.wsdl20.Service;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.common.ProxyUtil;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSField;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSService;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.w3c.dom.Document;

//TODO Composite is like singleton, to change for a multi-user use, test of Conversation composite
//@Scope("COMPOSITE")
@Scope("CONVERSATION")
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
	public String updateWsdl(String wsdlSource) throws Exception {
		// Hack for Talend airport sample
		if(wsdlSource == null || "".equals(wsdlSource)){
			wsdlSource = defaultWsdl;
		}

		wsdlSource = ProxyUtil.getUrlOrFile(wsdlSource).toString();
		
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
		return wsdlSource;
	}

	@Override
	public List<WSService> getServices() {
		logger.debug("Entering in getServiceName method");
		ArrayList<WSService> serviceList = new ArrayList<WSService>();		
		String serviceName = wsdlDescription.getServices()[0].getName().getLocalPart();
		for(int i=0; i< wsdlDescription.getServices().length; i++){
			serviceList.add(new WSService(wsdlDescription.getServices()[i].getName()));
			logger.debug("Service name : " + wsdlDescription.getServices()[i].getName());
		}		
		logger.debug("Service name : " + serviceName);
		return serviceList;
	}

	@Override
	public List<WSEndpoint> getEndpoints(WSService wsService) {
		ArrayList<WSEndpoint> endpointsList = new ArrayList<WSEndpoint>();
		for(int i=0; i< wsdlDescription.getServices().length; i++){
			Service service = wsdlDescription.getServices()[i];
			if(service.getName().getLocalPart().equals(wsService.getName())){		
				for(Endpoint endpoint : wsdlDescription.getServices()[0].getEndpoints()){
					endpointsList.add(new WSEndpoint(endpoint.getName().toString() , endpoint.getAddress()));
				}
			}
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
	public List<WSField> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
		logger.debug("Entering in getOutputMessageName");
		BindingOperation operation = getBindingOperation(wsEndpoint, wsOperation);
		return getFields(operation, Direction.IN);
	}

	@Override
	public List<WSField> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) {
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
	private List<WSField> getFields(BindingOperation operation, Direction direction){
		ArrayList<WSField> outputFields = new ArrayList<WSField>();
		InterfaceMessageReference[] messages = operation.getInterfaceOperation().getInterfaceMessageReferences();		
		logger.debug("messages : " + messages.length);
		for(int i = 0; i <messages.length; i++){
			if(direction.equals(messages[i].getDirection())){
				XmlSchemaElement schemaElement =  (XmlSchemaElement) (messages[i].getElementDeclaration().getContent());				
				XmlSchemaElement elem = (XmlSchemaElement) (wsdlDescription.getElementDeclaration(schemaElement.getSchemaType().getQName()).getContent());
				XmlSchemaComplexType elemComplexType = (XmlSchemaComplexType)elem.getSchemaType();
				XmlSchemaSequence sequence = (XmlSchemaSequence)(elemComplexType.getParticle());			 	
				Iterator<XmlSchemaSequenceMember> iter = sequence.getItems().iterator();
				// Only compatible with XML Schema version 2.x
				//Iterator<Object> iter = sequence.getItems().getIterator();
				while(iter.hasNext()){
					Object obj = iter.next();
					if(obj instanceof XmlSchemaElement){
						XmlSchemaElement field = (XmlSchemaElement) obj;
						logger.debug("Field found = " + field.getName());
						outputFields.add(new WSField(field.getName(), "String"));
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

	@Override
	public Object getConstant(String constantName){
		return EasySOAConstants.get(constantName);
	}

}
