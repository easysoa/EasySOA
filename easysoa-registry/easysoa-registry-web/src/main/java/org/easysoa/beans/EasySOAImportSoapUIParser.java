
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
            definition="http://127.0.0.1:9010/PureAirFlowers?wsdl" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">*/
            serviceName = attributes.getValue("name");
            serviceWsdlUrl = attributes.getValue("definition");
            serviceEndpointUrl = null; // Reset binding url
        }
        else if (qName.equals("con:endpoint")) {
            // Example:<con:endpoint>http://localhost:9010/PureAirFlowers</con:endpoint>
            inEndpointTag = true; 
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inEndpointTag) {
            serviceEndpointUrl = String.valueOf(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)  throws SAXException {
        if (qName.equals("con:endpoint")) {
            inEndpointTag = false;
        }
        else if (qName.equals("con:interface")) {
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
