/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.proxy.core.api.monitoring;

import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.MessageContent;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.exchangehandler.EasySOAv1SOAPDiscoveryMessageHandler;
import org.easysoa.proxy.core.api.exchangehandler.HandlerManager;
import org.easysoa.proxy.core.api.exchangehandler.HandlerResponse;
import org.easysoa.proxy.core.api.exchangehandler.MessageHandlerBase;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.records.Exchange;
import org.easysoa.records.ExchangeRecord;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Specific unit test for the monitoring handlers
 *
 * @author jguillemotte
 */
public class MessageHandlersTest {

    // TODO : All tests uses the Real Nuxeo registry V1 rest service
    // If this service is not started before the test, the test will fail

    // Logger
    private static Logger logger = Logger.getLogger(MessageHandlersTest.class.getName());

    /** The FraSCAti platform */
    protected static FraSCAti frascati;
    protected static ArrayList<Component> componentList;

    /**
     * Start FraSCAti
     * @throws FrascatiException
     */
    protected static void startFraSCAti() throws FrascatiException {
        logger.info("FraSCATI Starting");
        componentList =  new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        // Start fraSCAti
        startFraSCAti();
        // Start Registry API mock
        //componentList.add(frascati.processComposite("registryApiMock", new ProcessingContextImpl()));
        // Start handlerManager
        componentList.add(frascati.processComposite("handlerManagerBase", new ProcessingContextImpl()));
    }

    /**
     * Test the EasySOAv1SOAPDiscoveryMessageHandler with a WSDL file get request
     * @throws Exception
     */
    @Test
    public void WsdlDiscoveryMessageHandlerTest() throws Exception {

        Component comp = frascati.getComposite("handlerManagerBase");
        HandlerManager handler = frascati.getService(comp, "handlerManagerComponentService", HandlerManager.class);

        //EasySOAv1SOAPDiscoveryMessageHandler handler = new EasySOAv1SOAPDiscoveryMessageHandler();
        PropertyManager pm = new PropertyManager("test.properties");

        // proxy configuration
        ProxyConfiguration proxyConf = new ProxyConfiguration();
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.ENVIRONMENT_PARAM_NAME, "Production");
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.PROJECTID_PARAM_NAME, "/default-domain/Intégration DPS - DCV/Realisation_v");

        // build an Exchange message for a WSDL get request
        ExchangeRecord record = new ExchangeRecord();
        record.setExchange(new Exchange());
        record.setInMessage(new InMessage());
        record.setOutMessage(new OutMessage());

        record.getInMessage().setProtocol("http");
        record.getInMessage().setMethod("get");
        record.getInMessage().setPath("/data/info.wso?wsdl");
        record.getInMessage().setServer("footballpool.dataaccess.eu");
        record.getInMessage().setRemoteHost("localhost");
        record.getInMessage().setRemoteAddress("127.0.0.1");

        // Call handler
        handler.setHandlerConfiguration(proxyConf);
        handler.handleMessage(record.getInMessage(), record.getOutMessage());
    }

    /**
     * Test the EasySOAv1SOAPDiscoveryMessageHandler with a SOAP Post request
     * @throws Exception
     */
    @Test
    public void SoapDiscoveryMessageHandlerTest() throws Exception {

        Component comp = frascati.getComposite("handlerManagerBase");
        HandlerManager handler = frascati.getService(comp, "handlerManagerComponentService", HandlerManager.class);

        //EasySOAv1SOAPDiscoveryMessageHandler handler = new EasySOAv1SOAPDiscoveryMessageHandler();
        PropertyManager pm = new PropertyManager("test.properties");

        String soapRequestContent = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:met=\"http://meteomock.mock.test.easysoa.openwide.org/\">"
	            + "<soapenv:Header/>"
	            + "<soapenv:Body>"
	            + "<met:getTomorrowForecast>"
	            + "<met:arg0>Lyon</met:arg0>"
	            +"</met:getTomorrowForecast>"
	            + "</soapenv:Body>"
	            +"</soapenv:Envelope>";

        // proxy configuration
        ProxyConfiguration proxyConf = new ProxyConfiguration();
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.ENVIRONMENT_PARAM_NAME, "Production");
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.PROJECTID_PARAM_NAME, "/default-domain/Intégration DPS - DCV/Deploiement_v");

        // build an Exchange message for a SOAP post request
        ExchangeRecord record = new ExchangeRecord();
        record.setExchange(new Exchange());
        record.setInMessage(new InMessage());
        record.setOutMessage(new OutMessage());

        record.getInMessage().setProtocol("http");
        record.getInMessage().setMethod("post");
        record.getInMessage().setPath("/data/info.wso");
        record.getInMessage().setServer("footballpool.dataaccess.eu");
        record.getInMessage().setRemoteHost("localhost");
        record.getInMessage().setRemoteAddress("127.0.0.1");

        MessageContent messageContent = new MessageContent();
        // Sample SOAP message for test
        messageContent.setRawContent(soapRequestContent);
        record.getInMessage().setMessageContent(messageContent);

        // Call handler
        handler.setHandlerConfiguration(proxyConf);
        Map<String, HandlerResponse> handlerResponses = handler.handleMessage(record.getInMessage(), record.getOutMessage());

        // Check that all handler handle the message with success
        // TODO better, check each handler response
        Assert.assertEquals(5, handlerResponses.size());

    }

}
