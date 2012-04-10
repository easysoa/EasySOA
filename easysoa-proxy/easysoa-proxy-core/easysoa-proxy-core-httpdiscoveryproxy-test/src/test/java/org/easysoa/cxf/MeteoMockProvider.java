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

package org.easysoa.cxf;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.log4j.Logger;

/**
 * Server part for CxfProviderDispatcherTest
 * 
 * @author jguillemotte
 *
 */
@WebServiceProvider(portName="MeteoMockPort", serviceName="MeteoMock", targetNamespace="http://org.easysoa.meteomock/")
@ServiceMode(value=Service.Mode.MESSAGE)
public class  MeteoMockProvider implements Provider<SOAPMessage> {

    // Logger
    private static Logger logger = Logger.getLogger(MeteoMockProvider.class.getName());    

    private final static String soapResponse = "Cloudy;Feels Like:45 °F;Barometer:30.36 in and rising rapidly;Humidity:61 %;Visibility:6.21 mi;Dewpoint:32 °F;Wind:N 7 mph;UV Index:--;UV Description:Low;Sunrise:8:20 AM;Sunset:5:11 PM";
    
    public MeteoMockProvider() {}

    public SOAPMessage invoke(SOAPMessage request) {
        SOAPBody requestBody;
        try {
            requestBody = request.getSOAPBody();
            if(requestBody.getElementName().getLocalName().equals("Body")) {
                MessageFactory mf = MessageFactory.newInstance();
                SOAPFactory sf = SOAPFactory.newInstance();
    
                SOAPMessage response = mf.createMessage();
                SOAPBody respBody = response.getSOAPBody();
                Name bodyName;
                if(requestBody.getFirstChild().getNextSibling().getLocalName().equals("getTomorrowForecast"))
                {
                    bodyName = sf.createName("getTomorrowForecastResponse");
                } else if(requestBody.getFirstChild().getNextSibling().getLocalName().equals("invoke")){
                    bodyName = sf.createName("invokeResponse");
                } else {
                    throw new SOAPException("No operation named 'getTomorrowForecast' or 'invoke' was found !");
                }
                respBody.addBodyElement(bodyName);
                SOAPElement respContent = respBody.addChildElement("return");
                respContent.setValue(soapResponse);
                response.saveChanges();
                return response;
            }
        } catch(SOAPException soapEx)
        {
            logger.error("An error occurs !", soapEx);
            
        }
        return null;
    }
}

