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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.common.ProxyUtil;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSField;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSService;
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
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * Alternative solution to XSLT transformation for form generation
 * To use with velocity and template model => need to change the interface signature 
 * @author jguillemotte
 *
 */
// TODO Composite is like singleton, to change for a multi-user use, try with Conversation scope.
// TODO Check compatibility with WSDL 2.0
//@Scope("COMPOSITE")
@Scope("CONVERSATION")
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
	public String updateWsdl(String wsdlSource) throws Exception {
	    
		// Hack for Talend airport sample
		if(wsdlSource == null || "".equals(wsdlSource)){
			wsdlSource = defaultWsdl;
		}

		URL wsdlUrl = ProxyUtil.getUrlOrFile(wsdlSource);
		
		// Read WSDL version 1.1 or 2.0
		WSDLReader reader;
		logger.debug("WSDl source to parse : " + wsdlSource);
		try {
			reader = WSDLFactory.newInstance().newWSDLReader();
			wsdlDescription = reader.read(wsdlUrl);
			logger.debug("WSDL Reading OK");
		} catch (WSDLException wex) {
			logger.error("A problem occurs during the parsing of the WSDl file " + wsdlSource, wex);
			throw wex;
		} catch (Exception ex) {
			logger.error("Unable to get WSDl file " + wsdlSource, ex);
			throw ex;
		}
		return wsdlUrl.toString();
	}

	@Override
	public List<WSService> getServices(){
		logger.debug("Entering in getServiceName method");
		ArrayList<WSService> serviceList = new ArrayList<WSService>();
		logger.debug("Number of services : " + wsdlDescription.getServices().size());
		for(Service service : wsdlDescription.getServices()){
			serviceList.add(new WSService(service.getQName()));
			logger.debug("Service name : " + service.getQName().getLocalPart());
		}
		return serviceList;
	}
	
	@Override
	public List<WSEndpoint> getEndpoints(WSService wsService) {
		logger.debug("Entering in getEndpoints method");
		ArrayList<WSEndpoint> endpointList = new ArrayList<WSEndpoint>();
		for(Service service : wsdlDescription.getServices()){
			if(service.getQName().getLocalPart().equals(wsService.getName())){
				for(Endpoint endpoint : service.getEndpoints()){
					try {
						endpointList.add(new WSEndpoint(endpoint.getName(), new URI(endpoint.getAddress())));
					} catch (URISyntaxException ex) {
						logger.error(ex);
						ex.printStackTrace();
					}
				}
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
	public List<WSField> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException {
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
	public List<WSField> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException {
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
	 * @throws XmlException 
	 */
	private List<WSField> getFields(List<Part> partList) throws XmlException {
		List<WSField> elementNameList = new ArrayList<WSField>();
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
						// TODO : Not all cases are processed here.
						// In a ComplexType, several subtypes can be found....
						// all, sequence, choice ...
						if(elementType instanceof ComplexType){
							//logger.debug("ComplexType found ...");
							ComplexType typ = (ComplexType) elementType;
							// Complex element can be empty
							if(typ.getSequence() != null){
								List<Element> elementList = typ.getSequence().getElements();
								for(Element element : elementList){
									logger.debug("element found : " + element.getQName().getLocalPart());
									elementNameList.add(new WSField(element.getQName().getLocalPart(), element.getType().getQName().getLocalPart()));
								}
							} else if(typ.getAll() != null){
								List<Element> elementList =	typ.getAll().getElements();
								for(Element element : elementList){
									logger.debug("element found : " + element.getQName().getLocalPart());
									elementNameList.add(new WSField(element.getQName().getLocalPart(), element.getType().getQName().getLocalPart()));
								}								
							}
						} else if(elementType instanceof SimpleType) {
							//logger.debug("SimpleType found ...");
							SimpleType typ = (SimpleType) elementType;
							logger.debug("element found : " + typ.getQName().getLocalPart());
							elementNameList.add(new WSField(typ.getQName().getLocalPart(), typ.getOtherAttributes().get("type")));
						}
					}
				}
			}
		}
		return elementNameList;
	}

	@Override
	public Object getConstant(String constantName){
		return EasySOAConstants.get(constantName);
	}

}
