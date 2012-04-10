/**
 * EasySOA Registry
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


package org.easysoa.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.impl.EasySOALocalApi;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author mkalam-alami
 *
 */
public class EasySOAImportSoapUIParser extends DefaultHandler {

    private static Log log = LogFactory.getLog(EasySOAImportSoapUIParser.class);
    
    // EasySOA model access
    private final EasySOALocalApi api;

    // Service information
    private String workspaceName;
    private String serviceName = null;
    private String serviceWsdlUrl = null;
    private String serviceEndpointUrl = null;
    
    // Parsing state
    private boolean inEndpointTag = false;
    
    public EasySOAImportSoapUIParser(EasySOALocalApi api, String workspaceName) {
        this.api = api;
        this.workspaceName = workspaceName;
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (qName.equals("con:interface")) {
            /* Example: <con:interface xsi:type="con:WsdlInterface" wsaVersion="NONE" name="PureAirFlowersServiceService"
            type="wsdl" bindingName="{http://paf.samples.easysoa.org/}PureAirFlowersServiceServiceSoapBinding" soapVersion="1_1" anonymous="optional"
            definition="http://127.0.0.1:9010/PureAirFlowers?wsdl" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> */
            serviceName = attributes.getValue("name");
            serviceWsdlUrl = attributes.getValue("definition");
            serviceEndpointUrl = null; // Reset binding url
        }
        else if (qName.equals("con:mockService")) {
            /* Example: <con:mockService port="8088" path="/mockPureAirFlowersServiceService" 
             host="MKALAMALAMI" name="PureAirFlowersServiceService MockService"> */
            serviceName = attributes.getValue("name");
            serviceWsdlUrl = null;
            serviceEndpointUrl = "http://localhost:" + attributes.getValue("port") + attributes.getValue("path"); 
        }
        else if (qName.equals("con:endpoint")) {
            // Example:<con:endpoint>http://localhost:9010/PureAirFlowers</con:endpoint>
            inEndpointTag = true; 
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Note: Only considers the first endpoint tag (filters mocks but might forget some other endpoints)
        if (inEndpointTag && serviceEndpointUrl == null) {
            serviceEndpointUrl = String.valueOf(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)  throws SAXException {
        if (qName.equals("con:endpoint")) {
            inEndpointTag = false;
        }
        else if (qName.equals("con:interface") || qName.equals("con:mockService")) {
            
            // Discover mock app
            if (qName.equals("con:mockService")) {
                Map<String, String> properties = new HashMap<String, String>();
                properties.put(Service.PROP_ENVIRONMENT, workspaceName);
                properties.put(Service.PROP_TITLE, "SoapUI mocks");
                properties.put(Service.PROP_URL, "http://localhost");
                try {
                    api.notifyAppliImpl(properties);
                } catch (Exception e) {
                    log.error("Failed to save Appli. Impl during SoapUI conf. parsing", e);
                }
            }
            
            // Discover service
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(Service.PROP_ENVIRONMENT, workspaceName);
            properties.put(Service.PROP_TITLE, serviceName);
            properties.put(Service.PROP_URL, serviceEndpointUrl);
            properties.put(Service.PROP_FILEURL, serviceWsdlUrl);
            try {
                api.notifyService(properties);
            } catch (Exception e) {
                log.error("Failed to save Service during SoapUI conf. parsing", e);
            }
        }
    }
    
}
