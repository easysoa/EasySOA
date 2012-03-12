/**
 * 
 */
package org.easysoa.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;

import org.apache.cxf.endpoint.Client;

/**
 * @author jguillemotte
 *
 */
public class MeteoMockDispatcher {

    
    public String sendRequest(boolean providerInFraSCAti) throws IOException, SOAPException{
        MessageFactory factory = MessageFactory.newInstance();
        //System.out.println(wsdlURL + "\n\n");
        String wsdlURL = "http://localhost:8987/meteoMockProvider?wsdl";
        
        QName serviceName;
        QName portName;
        InputStream is1;
        
        // To use with service generated by FraSCAti
        if(providerInFraSCAti){
            serviceName = new QName("http://ws.xml.javax/", "Provider");
            portName = new QName("http://ws.xml.javax/", "ProviderPort");
            is1 = Client.class.getResourceAsStream("/meteoMockRequestFraSCAti.xml");
        }
        // To use with service corresponding to JAX-WS annotations
        else {
            serviceName = new QName("http://org.easysoa.meteomock/", "MeteoMock");
            portName = new QName("http://org.easysoa.meteomock/", "MeteoMockPort");
            is1 = Client.class.getResourceAsStream("/meteoMockRequest.xml");
        }
        
        Service service = Service.create(new URL(wsdlURL), serviceName);
        //Dispatch<DOMSource> dispatch = s.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
        
        // Original request
        //InputStream is1 = Client.class.getResourceAsStream("/meteoMockRequest.xml");
        // Request to be used with FraSCAti. See issue 23. FraSCAti doesn't use JAX-WS annotations so service name, port name and Method name are different
        //InputStream is1 = Client.class.getResourceAsStream("/meteoMockRequestFraSCAti.xml");
        if (is1 == null) {
            System.err.println("Failed to create input stream from file "
                    + "meteoMockRequest.xml, please check");
            return null;
        }
        SOAPMessage soapReq1 = factory.createMessage(null, is1);
        Dispatch<SOAPMessage> dispSOAPMsg = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

        System.out.println("Invoking server through Dispatch interface using SOAPMessage");
        SOAPMessage soapResp = dispSOAPMsg.invoke(soapReq1);
        System.out.println("Response from server: " + soapResp.getSOAPBody().getTextContent());
        return soapResp.getSOAPBody().getTextContent();
    }
    
}
